package uk.gov.companieshouse.psc.extensions.api.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public final class LogMapHelper {

    private LogMapHelper() {
        // intentionally blank
    }
    
    /**
     * Create a log map with transaction ID.
     *
     * @param transactionId the transaction ID
     * @return Map for logging
     */
    public static Map<String, Object> createLogMap(String transactionId) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("transaction_id", transactionId);
        return logMap;
    }
    
    /**
     * Create a log map with transaction ID and filing ID.
     *
     * @param transactionId the transaction ID
     * @param filingId the filing ID
     * @return Map for logging
     */
    public static Map<String, Object> createLogMap(String transactionId, String filingId) {
        Map<String, Object> logMap = createLogMap(transactionId);
        logMap.put("filing_id", filingId);
        return logMap;
    }
}