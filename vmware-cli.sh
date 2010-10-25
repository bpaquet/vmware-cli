#!/bin/sh -e

java -Djava.endorsed.dirs=target -jar target/vmware-cli-1.0-SNAPSHOT-jar-with-dependencies.jar "$1" "$2" "$3" "$4" "$5" "$6" "$7" 
