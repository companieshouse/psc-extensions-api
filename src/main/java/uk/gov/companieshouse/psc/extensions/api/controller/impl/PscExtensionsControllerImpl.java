package uk.gov.companieshouse.psc.extensions.api.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.common.ResourceLinks;
import uk.gov.companieshouse.api.model.psc.PscIndividualFullRecordApi;
import uk.gov.companieshouse.api.model.pscverification.PscVerificationApi;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.psc.extensions.api.controller.PscExtensionsController;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;
import uk.gov.companieshouse.psc.extensions.api.exception.InvalidFilingException;
import uk.gov.companieshouse.psc.extensions.api.exception.PscLookupServiceException;
import uk.gov.companieshouse.psc.extensions.api.mapper.PscExtensionsMapper;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsApi;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.InternalData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.service.ExtensionValidityService;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionDetailsService;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.service.PscLookupService;
import uk.gov.companieshouse.psc.extensions.api.service.TransactionService;
import uk.gov.companieshouse.psc.extensions.api.utils.LogMapHelper;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/transactions/{transactionId}/persons-with-significant-control-extensions")
public class PscExtensionsControllerImpl implements PscExtensionsController {

    public static final String VALIDATION_STATUS = "validation_status";
    private static final String PSC_EXTENSIONS_APP_NAME = "psc-extensions-api";

    private final TransactionService transactionService;
    private final PscExtensionsService pscExtensionsService;
    private final PscExtensionDetailsService pscExtensionDetailsService;
    private final PscLookupService pscLookupService;
    private final PscExtensionsMapper filingMapper;
    private final ExtensionValidityService extensionValidityService;
    private final Clock clock;
    private final Logger logger;

    @Autowired
    public PscExtensionsControllerImpl(final TransactionService transactionService,
                                      final PscExtensionsService pscExtensionsService,
        PscExtensionDetailsService pscExtensionDetailsService,
                                      final PscLookupService pscLookupService,
                                      PscExtensionsMapper filingMapper,
                                       final ExtensionValidityService extensionValidityService,
                                      final Clock clock) {
        this.transactionService = transactionService;
        this.pscExtensionsService = pscExtensionsService;
        this.pscExtensionDetailsService = pscExtensionDetailsService;
        this.pscLookupService = pscLookupService;
        this.filingMapper = filingMapper;
        this.extensionValidityService = extensionValidityService;
        this.clock = clock;
        this.logger = LoggerFactory.getLogger(PSC_EXTENSIONS_APP_NAME);
    }

    @Override
    @Transactional
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PscExtensionsApi> createPscExtension(
            @PathVariable("transactionId") final String transId,
            @RequestAttribute(required = false, name = "transaction") final Transaction transaction,
            @RequestBody @Valid @NotNull final PscExtensionsData data,
            final BindingResult result,
            final HttpServletRequest request
    ) throws PscLookupServiceException {
        final var logMap = LogMapHelper.createLogMap(transId);
        logMap.put("path", request.getRequestURI());
        logMap.put("method", request.getMethod());
        logger.debugRequest(request, "POST", logMap);

        Optional.ofNullable(result).ifPresent(PscExtensionsControllerImpl::checkBindingErrors);

        if (!extensionValidityService.canSubmitExtensionRequest(data)) {
            logger.errorContext(transId, "PSC already has maximum number of extension requests", null, logMap);
            throw new RuntimeException("PSC has already submitted the maximum number of extension requests");
        }

        final var requestTransaction = getTransaction(transId, transaction, logMap,
                getPassthroughHeader(request));

        final var entity = filingMapper.toEntity(data);
        final PscIndividualFullRecordApi pscIndividualFullRecordApi;
        try {
            pscIndividualFullRecordApi = pscLookupService.getPscIndividualFullRecord(
                    requestTransaction, data, PscType.INDIVIDUAL);
        } catch (PscLookupServiceException e) {
            logMap.put("psc_notification_id", data.getPscNotificationId());
            logger.errorContext(String.format("PSC Id %s does not have an Internal ID in PSC Data API for company number %s",
                    data.getPscNotificationId(), data.getCompanyNumber()), null, logMap);
            throw new PscLookupServiceException(
                    "We are currently unable to process an Extension filing for this PSC", new Exception("Internal Id"));
        }

        var internalData = InternalData.newBuilder().internalId(String.valueOf(pscIndividualFullRecordApi.getInternalId())).build();
        entity.setInternalData(internalData);

        final var savedEntity = saveFilingWithLinks(entity, transId, request, logMap);

        if (requestTransaction != null) {
            updateTransactionResources(requestTransaction, savedEntity.getLinks());
        }

        final var response = filingMapper.toApi(savedEntity);

        return ResponseEntity.created(savedEntity.getLinks().self()).body(response);
    }

    @Override
    @GetMapping("/{pscNotificationId}")
    public ResponseEntity<PscExtension> getPscExtensionCount(
        @PathVariable("pscNotificationId") final String pscNotificationId) {

        final var extensionCount = pscExtensionDetailsService.getExtensionCount(pscNotificationId);
        if (extensionCount.isPresent()){
            logger.info("Extension found for the given notification ID.");
        }else{
            throw new IllegalArgumentException("No extension found for the given notification ID.");
        }


        return ResponseEntity.ok(extensionCount.get());
    }

    private Transaction getTransaction(final String transId, Transaction transaction,
                                     final Map<String, Object> logMap, final String passthroughHeader) {
        if (transaction == null) {
            try {
                transaction = transactionService.getTransaction(transId, passthroughHeader);
            } catch (Exception e) {
                logger.errorContext(transId, "Failed to get transaction", e, logMap);
                throw new RuntimeException("Failed to get transaction", e);
            }
        }

        logger.infoContext(transId, "transaction found", logMap);
        return transaction;
    }

    private String getPassthroughHeader(final HttpServletRequest request) {
        return request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
    }

    protected static void checkBindingErrors(final BindingResult bindingResult) {
        final var validationErrors = Optional.ofNullable(bindingResult)
                .map(Errors::getFieldErrors)
                .map(ArrayList::new)
                .orElseGet(ArrayList::new);

        if (!validationErrors.isEmpty()) {
            throw new InvalidFilingException(validationErrors);
        }
    }

    private PscExtension saveFilingWithLinks(
            final PscExtension entity,
            final String transId,
            final HttpServletRequest request,
            final Map<String, Object> logMap
    ) {
        logger.debugContext(transId, "saving PSC Extension", logMap);

        final var now = clock.instant();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        final var saved = pscExtensionsService.save(entity);
        final var links = buildLinks(request, saved);
        saved.setLinks(links);
        final var resaved = pscExtensionsService.save(saved);

        logMap.put("filing_id", resaved.getId());
        logger.infoContext(transId, "Filing saved", logMap);

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
        try {
            transactionService.updateTransaction(transaction);
        } catch (Exception e) {
            logger.errorContext(transaction.getId(), "Failed to update transaction", e, null);
            throw new RuntimeException("Failed to update transaction", e);
        }
    }

    private Map<String, Resource> buildResourceMap(final ResourceLinks links) {
        final Map<String, Resource> resourceMap = new HashMap<>();
        final var resource = new Resource();
        final var linksMap = new HashMap<>(
                Map.of("resource", links.self().toString(), VALIDATION_STATUS,
                        links.validationStatus().toString()));

        resource.setKind("psc-extension");
        resource.setLinks(linksMap);
        resource.setUpdatedAt(clock.instant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        resourceMap.put(links.self().toString(), resource);

        return resourceMap;
    }
}