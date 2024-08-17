#! /bin/bash
set -e
pushd bank_service 
./cleanup.sh
./build_deploy_test.sh `hostname -f` 8080
popd
