#!/bin/bash
#
# Start script for psc-extensions-api

PORT=8080

exec java -jar -Dserver.port="${PORT}" "psc-extensions-api.jar"