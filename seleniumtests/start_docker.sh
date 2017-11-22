#!/usr/bin/env bash

set -e

docker-compose up -d

#./wait-for-it.sh localconfluence:8090 -t 120
#./wait-for-it.sh 127.0.0.1:4000 -t 120
#./wait-for-it.sh 127.0.0.1:4001 -t 120

mvn test-compile
#mvn surefire:test -Dtest=SetupSuite
