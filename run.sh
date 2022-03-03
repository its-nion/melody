#!/bin/bash

if ! command -v java &> /dev/null
then
    echo "Installing java"
    sudo apt install openjdk-17-jdk
fi

if ! command -v gradle &> /dev/null
then
  if ! command -v zip &> /dev/null
  then
    echo "Installing zip"
    sudo apt install zip
  fi
  if ! command -v unzip &> /dev/null
  then
    echo "Installing unzip"
    sudo apt install unzip
  fi
  echo "Installing gradle"
  sudo curl -s "https://get.sdkman.io" | bash
  bash "$HOME/.sdkman/bin/sdkman-init.sh"
  sdk install gradle 7.4
fi

if [ "$1" == "upsertGlobal" ]
then
  gradle -q upsertGlobal
fi

if [ "$1" == "upsertLocal" ]
then
  gradle -q upsertLocal
fi

gradle -q execute