package uk.gov.companieshouse.psc.extensions.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.common.ResourceLinks;
import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.pscextensions.api.PscExtensionRequestApi;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionResponse;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.ExtensionRequestServiceException;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;
import uk.gov.companieshouse.psc.extensions.api.exception.TransactionServiceException;
import uk.gov.companieshouse.psc.extensions.api.mapper.PscExtensionsMapper;
import uk.gov.companieshouse.psc.extensions.api.model.FilingKind;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.InternalData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.service.ExtensionValidityService;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;
import uk.gov.companieshouse.psc.extensions.api.service.TransactionService;
import uk.gov.companieshouse.psc.extensions.api.utils.LogMapHelper;

import java.time.Clock;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static uk.gov.companieshouse.psc.extensions.api.PscExtensionsApiApplication.APPLICATION_NAMESPACE;

@RestController
public class PscExtensionsControllerImpl implements PscExtensionRequestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private static final String VALIDATION_STATUS = "validation_status";

    private final TransactionService transactionService;
    private final PscExtensionsService pscExtensionsService;
    private final PscLookupService pscLookupService;
    private final PscExtensionsMapper filingMapper;
    private final ExtensionValidityService extensionValidityService;
    private final Clock clock;

    public PscExtensionsControllerImpl(final TransactionService transactionService,
                                       final PscExtensionsService pscExtensionsService,
                                       final PscLookupService pscLookupService,
                                       final PscExtensionsMapper filingMapper,
                                       final ExtensionValidityService extensionValidityService,
                                       final Clock clock) {
        this.transactionService = transactionService;
        this.pscExtensionsService = pscExtensionsService;
        this.pscLookupService = pscLookupService;
        this.filingMapper = filingMapper;
        this.extensionValidityService = extensionValidityService;
        this.clock = clock;
    }

    @Override
    @Transactional
    public ResponseEntity<PscExtensionResponse> _createPscExtension(String transactionId, PscExtensionsData data) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        final Transaction transaction = (Transaction) request.getAttribute("transaction");
        if (transaction == null) {
            throw new TransactionServiceException("Transaction failed to be retrieved, we need the transaction to proceed.");
        }

        final var logMap = LogMapHelper.createLogMap(transactionId);
        logMap.put("path", request.getRequestURI());
        logMap.put("method", request.getMethod());
        LOGGER.debugRequest(request, "POST", logMap);

        if (!extensionValidityService.canSubmitExtensionRequest(data)) {
            LOGGER.errorContext(transactionId, "PSC already has maximum number of extension requests", null, logMap);
            throw new ExtensionRequestServiceException("PSC has already submitted the maximum number of extension requests");
        }

        final PscExtension entity = filingMapper.toEntity(data);

        final PscIndividualFullRecordApi pscIndividualFullRecordApi;
        try {
            pscIndividualFullRecordApi = pscLookupService.getPscIndividualFullRecord(
                    transactionId,
                    data.getCompanyNumber(),
                    data.getPscNotificationId(),
                    PscType.INDIVIDUAL
            );
        } catch (PscLookupServiceException e) {
            logMap.put("psc_notification_id", data.getPscNotificationId());
            LOGGER.errorContext(String.format("PSC Id %s does not have an Internal ID in PSC Data API for company number %s",
                    data.getPscNotificationId(), data.getCompanyNumber()), null, logMap);
            throw new PscLookupServiceException(
                    "We are currently unable to process an Extension filing for this PSC", new Exception("Internal Id"));
        }

        final String internalId = String.valueOf(pscIndividualFullRecordApi.getInternalId());
        final InternalData internalData = new InternalData(internalId);
        entity.setInternalData(internalData);

        final var savedEntity = saveFilingWithLinks(entity, transactionId, request, logMap);
        updateTransactionResources(transaction, savedEntity.getLinks());

        final var response = filingMapper.toApi(savedEntity);

        return ResponseEntity.created(savedEntity.getLinks().self()).body(response);
    }


    @GetMapping("/{pscNotificationId}")
    public ResponseEntity<Long> getPscExtensionCount(@PathVariable("pscNotificationId") final String pscNotificationId) {

        final var extensionCount = pscExtensionsService.getExtensionCount(pscNotificationId);

        if (extensionCount.isPresent()) {
            LOGGER.info("Extension found for the given notification ID.");
            return ResponseEntity.ok(extensionCount.get());

        } else {
            throw new IllegalArgumentException("No extension found for the given notification ID.");
        }
    }

    private PscExtension saveFilingWithLinks(
            final PscExtension entity,
            final String transId,
            final HttpServletRequest request,
            final Map<String, Object> logMap
    ) {
        LOGGER.debugContext(transId, "saving PSC Extension", logMap);

        final var now = clock.instant();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        final var saved = pscExtensionsService.save(entity);
        final var links = buildLinks(request, saved);
        saved.setLinks(links);
        final var resaved = pscExtensionsService.save(saved);

        logMap.put("filing_id", resaved.getId());
        LOGGER.infoContext(transId, "Filing saved", logMap);

        return resaved;
    }

    private ResourceLinks buildLinks(final HttpServletRequest request,
                                     final PscExtension savedFiling) {
        final var objectId = new ObjectId(Objects.requireNonNull(savedFiling.getId()));
        final var selfUri = UriComponentsBuilder.fromUriString(request.getRequestURI())
                .pathSegment(objectId.toHexString())
                .build()
                .toUri();
        final var validateUri = UriComponentsBuilder.fromUriString(request.getRequestURI())
                .pathSegment(objectId.toHexString())
                .pathSegment(VALIDATION_STATUS)
                .build()
                .toUri();

        return ResourceLinks.newBuilder().self(selfUri).validationStatus(validateUri)
                .build();
    }

    private void updateTransactionResources(
            final Transaction transaction,
            final ResourceLinks links
    ) {
        final var resourceMap = buildResourceMap(links);

        transaction.setResources(resourceMap);

        transactionService.updateTransaction(transaction);
    }

    private Map<String, Resource> buildResourceMap(final ResourceLinks links) {
        final Map<String, Resource> resourceMap = new HashMap<>();
        final var resource = new Resource();
        final var linksMap = new HashMap<>(
                Map.of(
                        "resource", links.self().toString(),
                        VALIDATION_STATUS, links.validationStatus().toString()
                )
        );

        resource.setKind(FilingKind.KIND);
        resource.setLinks(linksMap);
        resource.setUpdatedAt(clock.instant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        resourceMap.put(links.self().toString(), resource);

        return resourceMap;
    }
}