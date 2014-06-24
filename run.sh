#!/bin/sh
SCRIPT_PATH=$(cd `dirname ${0}`; pwd)
JAVA_LIBS="bin/:lib/ojdbc6.jar"
JAVA_BIN=java

if [ -f /usr/java6_64/bin/java ]; then
	JAVA_BIN=/usr/java6_64/bin/java
elif [ -f /usr/java6/bin/java ]; then
	JAVA_BIN=/usr/java6/bin/java
elif [ -f /usr/java5_64/bin/java ]; then
	JAVA_BIN=/usr/java5_64/bin/java
elif [ -f /usr/java5/bin/java ]; then
	JAVA_BIN=/usr/java5/bin/java
fi;

$JAVA_BIN -cp $JAVA_LIBS OracleExport cvs example.sql servername:1531:oraclesid username password > test.csv

