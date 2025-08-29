package uk.gov.companieshouse.psc.extensions.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.psc.extensions.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.psc.extensions.api.service.FilingDataService;
import uk.gov.companieshouse.psc.extensions.api.service.PscExtensionsService;
import uk.gov.companieshouse.psc.extensions.api.utils.MapHelper;

import static uk.gov.companieshouse.psc.extensions.api.model.FilingKind.PSC_EXTENSION_INDIVIDUAL;

@Service
public class FilingDataServiceImpl implements FilingDataService {

    private final PscExtensionsService pscExtensionsService;

    @Autowired
    public FilingDataServiceImpl(PscExtensionsService pscExtensionsService) {
        this.pscExtensionsService = pscExtensionsService;
    }

    @Override
    public FilingApi generateFilingApi(String filingId, Transaction transaction) {
        final var pscExtensionOpt = pscExtensionsService.get(filingId);
        final var pscExtension = pscExtensionOpt.orElseThrow(() -> new FilingResourceNotFoundException(
                String.format("PSC extension not found when generating filing for %s", filingId)));

        final var filingApi = new FilingApi();
        filingApi.setKind(PSC_EXTENSION_INDIVIDUAL.getValue());
        filingApi.setDescription("Extension request for PSC verification deadline");

        final var dataMap = MapHelper.convertObject(pscExtension.getData());
        dataMap.remove("psc_notification_id");
        dataMap.put("appointment_id", pscExtension.getInternalData().getInternalId());
        filingApi.setData(dataMap);

        return filingApi;
    }
}