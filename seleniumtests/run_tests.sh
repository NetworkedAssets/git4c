#!/usr/bin/env bash

set -e

mvn test-compile
mvn surefire:test -Dtest=SeleniumSuite
