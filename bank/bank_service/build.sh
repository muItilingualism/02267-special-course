#!/bin/sh
set -e
SHOST=${1:-localhost}
SPORT=${2:-80}
export SHOST
export SPORT
pushd ../bank/
mvn clean install
popd 
echo $SHOST:$SPORT
sed -e "s/---host---/$SHOST/g" -e "s/---port---/$SPORT/g" < src/main/docker/docker-compose.yml > docker-compose.yml
sed -e "s/---host---/$SHOST/g" -e "s/---port---/$SPORT/g" < src/main/docker/Dockerfile > Dockerfile
pushd src/main/webapp
sed -e "s/---host---/$SHOST/g" -e "s/---port---/$SPORT/g" < template/index.html > index.html
sed -e "s/---host---/$SHOST/g" -e "s/---port---/$SPORT/g" < template/bank.yaml > bank.yaml
sed -e "s/---host---/$SHOST/g" -e "s/---port---/$SPORT/g" < template/BankService.wsdl.xml > BankService.wsdl.xml
cp template/cnt-index.html .
popd
pushd ../cmd_line_tools
./createToolsPackage $SHOST $SPORT
popd
mvn clean package
docker compose build
docker image prune -f
