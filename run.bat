@echo off
setlocal
SET JAVA_BIN=java
SET JAVA_LIBS="bin/;lib/ojdbc6.jar"


%JAVA_BIN% -cp %JAVA_LIBS% OracleExport cvs example.sql servername:1531:oraclesid username password > test.csv

pause
