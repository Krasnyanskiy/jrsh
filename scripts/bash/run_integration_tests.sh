#!/bin/bash
cd ../..
mvn test -DrunSuite=**/IntegrationTestSuit.class -DfailIfNoTests=false