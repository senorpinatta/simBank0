#!/bin/bash

WEEK_DIR=Text_Files/Week_1

mkdir $WEEK_DIR
bash Daily.sh $WEEK_DIR 1 EmptyValidAccounts.txt EmptyMAF.txt

echo "Week script done"