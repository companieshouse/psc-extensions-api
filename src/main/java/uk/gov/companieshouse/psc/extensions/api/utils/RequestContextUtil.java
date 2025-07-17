package uk.gov.companieshouse.psc.extensions.api.utils;

import uk.gov.companieshouse.psc.extensions.api.models.context.RequestContextData;
import uk.gov.companieshouse.api.accounts.user.model.User;

import java.util.Optional;
import java.util.function.Function;

import static uk.gov.companieshouse.psc.extensions.api.models.Constants.UNKNOWN;
import static uk.gov.companieshouse.psc.extensions.api.models.context.RequestContext.getRequestContext;

public final class RequestContextUtil {

    private RequestContextUtil(){}

    private static <T> T getFieldFromRequestContext( final Function<RequestContextData, T> getterMethod, final T defaultValue ){
        return Optional.ofNullable( getRequestContext() ).map( getterMethod ).orElse( defaultValue );
    }

    public static String getXRequestId(){
        return getFieldFromRequestContext( RequestContextData::getXRequestId, UNKNOWN );
    }

    public static String getEricIdentity(){
        return getFieldFromRequestContext( RequestContextData::getEricIdentity, UNKNOWN );
    }

    public static String getEricIdentityType(){
        return getFieldFromRequestContext( RequestContextData::getEricIdentityType, UNKNOWN );
    }

    public static User getUser(){
        return getFieldFromRequestContext( RequestContextData::getUser, null );
    }

}
