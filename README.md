# psc-extensions-api
psc-extensions-api is a Spring Boot REST API which forms part of the Identification Verification (IDV) service and is responsible for handling and processing PSC Extention requests.

## Related Services

-[psc-extensions-web](https://github.com/companieshouse/psc-extensions-web) (Web frontend for PSC extension requests)

## Overview

This service:
- Extends the Identification Verification (IDV) service.
- Receives requests to extend the identify verification deadline for PSCs via a REST interface

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


## Terraform ECS

### What does this code do?

The code present in this repository is used to define and deploy a dockerised container in AWS ECS.
This is done by calling a [module](https://github.com/companieshouse/terraform-modules/tree/main/aws/ecs) from terraform-modules. Application specific attributes are injected and the service is then deployed using Terraform via the CICD platform 'Concourse'.


Application specific attributes | Value                                | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**ECS Cluster**        |filing-maintain                                      | ECS cluster (stack) the service belongs to
**Load balancer**      |{env}-chs-apichgovuk                                            | The load balancer that sits in front of the service
**Concourse pipeline**     |[Pipeline link](https://ci-platform.companieshouse.gov.uk/teams/team-development/pipelines/psc-extensions-api) <br> [Pipeline code](https://github.com/companieshouse/ci-pipelines/blob/master/pipelines/ssplatform/team-development/psc-extensions-api)                                  | Concourse pipeline link in shared services


### Contributing
- Please refer to the [ECS Development and Infrastructure Documentation](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/4390649858/Copy+of+ECS+Development+and+Infrastructure+Documentation+Updated) for detailed information on the infrastructure being deployed.

### Testing
- Ensure the terraform runner local plan executes without issues. For information on terraform runners please see the [Terraform Runner Quickstart guide](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/1694236886/Terraform+Runner+Quickstart).
- If you encounter any issues or have questions, reach out to the team on the **#platform** slack channel.

### Vault Configuration Updates
- Any secrets required for this service will be stored in Vault. For any updates to the Vault configuration, please consult with the **#platform** team and submit a workflow request.

### Useful Links
- [ECS service uk.gov.companieshouse.psc.extensions.config dev repository](https://github.com/companieshouse/ecs-service-configs-dev)
- [ECS service uk.gov.companieshouse.psc.extensions.config production repository](https://github.com/companieshouse/ecs-service-configs-production)

