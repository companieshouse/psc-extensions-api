package uk.gov.companieshouse.psc.extensions.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionResponse;
import uk.gov.companieshouse.api.pscextensions.model.PscExtensionsData;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.Data;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.SensitiveData;

@Mapper(componentModel = "spring")
public interface PscExtensionsMapper {

    default PscExtension toEntity(final PscExtensionsData data) {
        PscExtension entity = new PscExtension();
        Data mappedData = toData(data);
        mappedData.setSensitiveData(new SensitiveData(data.getRequesterEmail()));
        entity.setData(mappedData);
        return entity;
    }
    
    Data toData(final PscExtensionsData data);

    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "kind", ignore = true)
    PscExtensionResponse toApi(final PscExtension extensions);
}