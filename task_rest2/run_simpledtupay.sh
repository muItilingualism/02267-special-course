#!/bin/sh
( cd ./simple-dtu-pay ; mvn clean package )
if docker-compose up --build -d; then
    echo "Container(s) succesfully started"
else
    echo "docker-compose command failed"
fi

# run bank_service build and deploy script
( cd ../bank/bank_service ; ./build_deploy.sh localhost 8081 )

