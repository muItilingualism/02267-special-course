#!/bin/sh
set -e
./build.sh $1 $2
./deploy.sh
