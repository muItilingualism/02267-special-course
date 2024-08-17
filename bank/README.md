# Installation
Installation in a new Linux virtual machine, like multipass ([multipass.run](https://multipass.run)).

First step is to run ``sudo apt update`` to update the contents of all software repositories.

Installation instructions:
1. Install Java 11 (JDK)
   - ``sudo apt install openjdk-11-jdk``
2. Install Maven
   - ``sudo apt install maven``
3. Install Zip
   - ``sudo apt install zip``
4. Install Docker and docker-compose
    - ``sudo apt install docker-compose``
5. Add user ubuntu to group docker
    - ``sudo addgroup ubuntu docker``
    - Then logout of the shell and login again for the commando to take effect.
    - Check that docker runs with ``docker run hello-world``
6. Copy bank.zip to your VM
    - ``multipass transfer bank.zip primary:``
7. In the VM, unzip bank.zip and goto bank/bank_service and build the bank. The parameters to the build command are the host name under which the bank is available (in this case ``fm-00.compute.dtu.dk``)
    - ``./build_deploy.sh fm-00.compute.dtu.dk``
8. To also run the tests within the VM itself, one has to add the line ``127.0.0.1 fm-00.compute.dtu.dk`` to the ``/etc/hosts`` file within the VM.
   - ``./build-deploy-test fm-00.compute.dtu.dk``
9. Using ``multipass list`` the VMs and add the IP address of the VM which runs the bank to ``/etc/hosts`` file of the host machine (Windows/Mac) as, e.g.,
    - ``192.168.67.3 fm-00.compute.dtu.dk`` has to be added to the ``/etc/hosts`` file that to all computers that want to access the bank (but compare with the previous step)
10. Test that the bank is available by going to ``http://fm-00.compute.dtu.dk`` in a Web browser. A Web page with instructions should appear.
# Java version
- Works with Java 11 and Java 8
- The SOAP java 11 tests, testing the WSImport Maven plugin, needs Java 11
