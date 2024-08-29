#!/bin/sh
echo "If you are unable to access the end-points, run 'mvn clean package before this script"

if docker-compose up --build -d; then
    echo "Container(s) succesfully started"
else
    echo "docker-compose command failed"
fi

