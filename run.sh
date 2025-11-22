#!/bin/bash
# Script to run the Bank application with Java 21

export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn clean compile exec:java
