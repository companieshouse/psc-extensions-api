package uk.gov.companieshouse.psc.extensions.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsApi;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtensions;

@Mapper(componentModel = "spring")
public interface PscExtensionsMapper {
    
    default PscExtensions toEntity(final PscExtensionsData data) {
        PscExtensions entity = new PscExtensions();
        entity.setData(toData(data));
        return entity;
    }
    
    Data toData(final PscExtensionsData data);

    @Mapping(target = "companyNumber", source = "data.companyNumber")
    @Mapping(target = "pscNotificationId", source = "data.pscNotificationId")
    @Mapping(target = "relevantOfficer", source = "data.relevantOfficer")
    @Mapping(target = "extensionDetails", source = "data.extensionDetails")
    PscExtensionsData toDto(final PscExtensions extensions);

    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "kind", ignore = true)
    @Mapping(target = "internalData", ignore = true)
    PscExtensionsApi toApi(final PscExtensions extensions);
}