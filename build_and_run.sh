#!/usr/bin/env bash

# Check Maven installed
command -v mvn >/dev/null 2>&1 || {
	echo >&2 "Maven executable required but not installed. Aborting.";
	exit 1;
}

# Parse artifact version from Maven configuration file
version=`mvn org.apache.maven.plugins:maven-help-plugin:2.2:evaluate -Dexpression=project.version |grep -Ev '(^\[|Download\w+:)'`

# Build package and run
mvn package -DskipTests; java -jar target/zipinfo-${version}-fat.jar -conf config.json
