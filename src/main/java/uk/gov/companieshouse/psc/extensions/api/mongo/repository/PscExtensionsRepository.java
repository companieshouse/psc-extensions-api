package uk.gov.companieshouse.psc.extensions.api.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;

@Repository
public interface PscExtensionsRepository extends MongoRepository<PscExtension, String> { }
