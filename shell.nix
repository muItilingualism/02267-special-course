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
  ];

  shellHook = ''
    export JAVA_HOME=${pkgs.jdk17}/lib/openjdk
    export PATH=$JAVA_HOME/bin:$PATH

    echo "Dev environment ready."
    echo "JDK: $(java -version 2>&1 | head -n 1)"
    echo "Maven: $(mvn --version | head -n 1)"
    echo "Run 'code .' to open vscode in the current directory."
  '';
}
