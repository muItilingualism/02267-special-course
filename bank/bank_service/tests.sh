#!/bin/sh
set -e 
SHOST=${1:-localhost}
SPORT=${2:-80}
pushd ../bank_client_test
./build.sh $SHOST $SPORT
popd
pushd ../rest_test
./tests.sh $SHOST $SPORT
popd
pushd ../soap+rest_test/
./tests.sh $SHOST $SPORT
popd
