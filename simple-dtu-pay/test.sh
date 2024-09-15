#!/bin/sh
set -e

wait_for_service() {
    local service_name=$1
    local log_message=$2

    echo "Waiting for $service_name to be ready..."
    while ! docker logs $service_name | grep -q "$log_message"; do
        sleep 1
    done
    echo "$service_name is ready."
}

run_simpledtupay() {
    ( cd simple-dtu-pay ; mvn clean package )
    ( cd account ; mvn clean package )
    ( cd payment ; mvn clean package )

    if docker-compose up --build -d; then
        echo "SimpleDTUPay service started successfully"
    else
        echo "Failed to start SimpleDTUPay service"
        exit 1
    fi
}

run_client_tests() {
    cd client-test
    mvn clean verify
    if [ $? -eq 0 ]; then
        echo "End-to-end tests completed successfully"
    else
        echo "End-to-end tests failed"
        exit 1
    fi
    cd ..
}

main() {
    echo "Starting SimpleDTUPay service..."
    run_simpledtupay

    wait_for_service "simple-dtu-pay-bank-1" "Thorntail is Ready"

    wait_for_service "simple-dtu-pay-rabbit-1" "Server startup complete"

    echo "Running end-to-end tests..."
    run_client_tests

    echo "All tests passed."
    echo "To stop the containers, use 'docker-compose down'"
    echo "To rerun the tests, run this script again."
}

main