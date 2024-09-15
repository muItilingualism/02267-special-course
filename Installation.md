Dette dokument vil gennemgå hvordan man kører `simple-dtu-pay`, det endelige projekt.

Dette dokument findes også som markdown i stedet for PDF.
## Dependencies

- General GNU Linux/WSL
	- openJDK 17
	- maven
	- docker
	- docker-compose
- NixOS or Nix enabled systems
	- run `nix-shell` in the root directory (same dir as `shell.nix`)
	- run `dockerd-rootless&` to start docker.

## Test

To run all the end-to-end tests. Run the `setup.sh` first to initially build the bank and then `simple-dtu-pay/test.sh` to actually run the tests. To retest, please run the `test.sh` script again. To shut down all containers, use `docker-compose down`.

## Run

To run all the services alike the testing but without running the tests run the `setup.sh` and then `run.sh`. To shut down all containers, use `docker-compose down`.

### Dev

To run all the services in dev mode, i recommend running each each microservice manually in separate terminals with `./mvnw quarkus:dev`, except for rabbitmq and the bank which you can launch in docker containers.

