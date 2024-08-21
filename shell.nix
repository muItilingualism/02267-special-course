let
  nixpkgsCommit = "6bf2a1180b788ff167d10ea61284b38e44b059c3";
  nixpkgsSha256 = "0hga0kkgmcmybp1929qqgmgdgwmnmc1wca10k4nlhvhxlp4ywi21";

  pkgs = import (builtins.fetchTarball {
    url = "https://github.com/nixos/nixpkgs/archive/${nixpkgsCommit}.tar.gz";
    sha256 = nixpkgsSha256;
  }) {
    config = { allowUnfree = true; };
  };

  customVSCode = pkgs.vscode-with-extensions.override {
    vscodeExtensions = with pkgs.vscode-extensions; [
      vscjava.vscode-java-pack
      redhat.java
      vscjava.vscode-java-debug
      vscjava.vscode-java-test
      vscjava.vscode-maven
      vscjava.vscode-java-dependency
      visualstudioexptteam.vscodeintellicode
   ];
  };
in pkgs.mkShell {
  buildInputs = with pkgs; [
    customVSCode
    jdk17
    maven
    zip
    docker
    docker-compose
    rootlesskit
    mermaid-cli
  ];

  shellHook = ''
    export JAVA_HOME=${pkgs.jdk17}/lib/openjdk
    export PATH=$JAVA_HOME/bin:$PATH

    # Docker seems to look in /var/run/docker.sock for the daemon (at least on NixOS),
    # but without root it runs in /run/user/<userid>.
    # see: https://discourse.nixos.org/t/is-there-a-way-to-run-docker-inside-a-nix-shell/46824/2
    export DOCKER_HOST=unix://$XDG_RUNTIME_DIR/docker.sock

    echo "Dev environment ready."
    echo "JDK: $(java -version 2>&1 | head -n 1)"
    echo "Maven: $(mvn --version | head -n 1)"
    echo "Docker: $(docker --version)"
    echo "Docker Compose: $(docker-compose --version)"
    echo "Run 'code .' to open vscode in the current directory."
    echo "Run './build_deploy_test.sh localhost 8081' in 'bank/bank_service/' to start the bank service"
  '';
}
