#!/bin/bash

FRONT_END_SRC=SimBankSource/FrontEnd/src
FRONT_END_CLASS=Assignment_6/FrontEnd
BACK_END_SRC=SimBankSource/BackEnd/src
BACK_END_CLASS=Assignment_6/BackEnd

#Compiles the java source files into the correct directories
for JAVA_FILE in $(find $FRONT_END_SRC -name "*.java")
do
    javac $JAVA_FILE -sourcepath $FRONT_END_SRC -d $FRONT_END_CLASS
done

for JAVA_FILE in $(find $BACK_END_SRC -name "*.java")
do
    javac $JAVA_FILE -sourcepath $BACK_END_SRC -d $BACK_END_CLASS
done

echo "SimBank Files comiled into Assignment_6"
