#!/usr/bin/env bash
export JAVA_PROGRAM_ARGS=`echo "$@"`
mvn exec:java -Dexec.mainClass="com.rudenko.RunMovieData" -Dexec.args="$JAVA_PROGRAM_ARGS"
