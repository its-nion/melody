#!/bin/bash

while getopts a:f: flag
do
    case "${flag}" in
        a) action=${OPTARG};;
        f) file=${OPTARG};;
        *)
    esac
done

if ! command -v java &> /dev/null
then
    echo "Installing java"
    sudo apt install openjdk-15-jdk
fi

if [ "$action" == "upsertGlobal" ]
then
  if [[ ! -n "$file" ]]
  then
    index=0
    for file in ./build/libs/*.jar ; do
	    f=$file
	    if [ $index -ge 1 ]
	    then
	      break
	    fi
	    ((index=index+1))
    done
    fName=$(basename "$f")
    fDir=$(dirname "$f")
    file="$fDir/$fName"
    echo "Assumed jar file at: $file"
  fi
  echo "Executing java -jar $file"
  java -jar "$file"
  exit
fi

if [ "$action" == "upsertLocal" ]
then
  if [[ ! -n "$file" ]]
  then
    index=0
    for file in ./build/libs/*.jar ; do
	    f=$file
	    if [ $index -ge 2 ]
	    then
	      break
	    fi
	    ((index=index+1))
    done
    fName=$(basename "$f")
    fDir=$(dirname "$f")
    file="$fDir/$fName"
    echo "Assumed jar file at: $file"
  fi
  echo "Executing java -jar $file"
  java -jar "$file"
  exit
fi

if [[ ! -n "$file" ]]
then
  index=0
  for file in ./build/libs/*.jar ; do
    f=$file
    if [ $index -ge 0 ]
    then
      break
    fi
    ((index=index+1))
  done
  fName=$(basename "$f")
  fDir=$(dirname "$f")
  file="$fDir/$fName"
  echo "Assumed jar file at: $file"
fi
echo "Executing java -jar $file"
java -jar "$file"
