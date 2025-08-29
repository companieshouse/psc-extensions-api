package uk.gov.companieshouse.psc.extensions.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsApi;
import uk.gov.companieshouse.psc.extensions.api.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;

@Mapper(componentModel = "spring")
public interface PscExtensionsMapper {

    default PscExtension toEntity(final PscExtensionsData data) {
        PscExtension entity = new PscExtension();
        entity.setData(toData(data));
        return entity;
    }
    
    Data toData(final PscExtensionsData data);

    @Mapping(target = "companyNumber", source = "data.companyNumber")
    @Mapping(target = "pscNotificationId", source = "data.pscNotificationId")
    @Mapping(target = "extensionDetails", source = "data.extensionDetails")
    PscExtensionsData toDto(final PscExtension extensions);

    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "kind", ignore = true)
    PscExtensionsApi toApi(final PscExtension extensions);
}