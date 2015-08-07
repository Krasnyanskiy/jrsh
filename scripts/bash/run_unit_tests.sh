#!/bin/bash
cd ../..
mvn test -DrunSuite=**/UnitTestSuite.class -DfailIfNoTests=false