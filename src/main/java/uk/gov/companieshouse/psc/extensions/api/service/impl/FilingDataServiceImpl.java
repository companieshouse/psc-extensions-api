package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.psc.extensions.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.psc.extensions.api.model.FilingKind;
import uk.gov.companieshouse.psc.extensions.api.service.FilingDataService;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.utils.LogMapHelper;
import uk.gov.companieshouse.psc.extensions.api.utils.MapHelper;

@Service
public class FilingDataServiceImpl implements FilingDataService {

    private final PscExtensionsService pscExtensionsService;

    private final Logger logger;

    @Autowired
    public FilingDataServiceImpl(PscExtensionsService pscExtensionsService, Logger logger) {
        this.pscExtensionsService = pscExtensionsService;
        this.logger = logger;
    }

    @Override
    public FilingApi generateFilingApi(String filingId, Transaction transaction) {

        final var transactionId = transaction.getId();
        final var logMap = LogMapHelper.createLogMap(transactionId, filingId);
        logger.debugContext(transactionId, "Fetching PSC extension", logMap);

        final var pscExtensionOpt = pscExtensionsService.get(filingId);
        final var pscExtension = pscExtensionOpt.orElseThrow(() -> new FilingResourceNotFoundException(
                String.format("PSC extension not found when generating filing for %s", filingId)));

        final var filingApi = new FilingApi();
        filingApi.setKind(FilingKind.FULL_KIND);
        filingApi.setDescription("Extension request for PSC verification deadline");

        final var dataMap = MapHelper.convertObject(pscExtension.getData());
        dataMap.remove("psc_notification_id");
        dataMap.put("appointment_id", pscExtension.getInternalData().getInternalId());
        logMap.put("Filing data to submit", dataMap);
        logger.debugContext(transactionId, filingId, logMap);
        filingApi.setData(dataMap);

        return filingApi;
    }
}