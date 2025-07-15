# psc-extensions-api
psc-extensions-api is a Spring Boot REST API which forms part of the Identification Verification (IDV) service and is responsible for handling and processing PSC Extention requests.

## Overview

This service:
- Extends the Identification Verification (IDV) service.
- Receives requests to extend the identify verification deadline for PSCs via a REST interface


## Related Services

- [psc-extensions-web](https://github.com/companieshouse/psc-extensions-web) (Web frontend for PSC extension requests)


## Requirements

To build the `psc-extensions-api`, you will need:
* [Git](https://git-scm.com/downloads)
* [Java 21](https://www.oracle.com/uk/java/technologies/downloads/#java21)
* [Maven](https://maven.apache.org/download.cgi)
* Internal Companies House core services

You will also need a REST client (e.g. Postman or Bruno) if you wish to interact with the `psc-extensions-api` service endpoints.

## Running Locally using Docker

1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the README.

1. Enable the `psc-extensions-api` services

2. Run `chs-dev up` and wait for all services to start

### To make local changes

Development mode is available for this service in [Docker CHS Development](https://github.com/companieshouse/docker-chs-development).

    ./bin/chs-dev development enable psc-extensions-api

This will clone the <code>psc-extensions-api</code> into the repositories folder inside <code>docker-chs-dev</code>. Any changes to the code, or resources will automatically trigger a rebuild and relaunch.