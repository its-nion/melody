#!/bin/bash

# checking for file argument
while getopts f: flag
do
    case "${flag}" in
        f) file=${OPTARG};;
        *)
    esac
done

# checking for java installation and installing if not present
if ! command -v java &> /dev/null
then
    echo "Installing java"
    sudo apt install openjdk-15-jdk
fi

# finding jar file
if [[ ! -n "$file" ]]
then
  for file in ./builds/*.jar ; do
    f=$file
  done
  fName=$(basename "$f")
  fDir=$(dirname "$f")
  file="$fDir/$fName"
  echo "Assumed jar file at: $file"
fi

# starting melody
echo "Executing java -jar $file"
java -jar "$file" --no-gui
