#!/bin/bash
WEEK_DIR=$1
DAY_NUM=$2
DAY_DIR=${WEEK_DIR}/DAY_${DAY_NUM}
TRANS_FILE=Text_Files/Transactions/Transactions1.txt
mkdir $DAY_DIR
mkdir ${DAY_DIR}/TSFs


echo "TSF: TSF.txt"
echo "VAF: EmptyValidAccounts.txt"
echo ${TRANS_FILE}
java -classpath FrontEnd FrontEndStartPoint EmptyValidAccounts.txt TSF.txt < "$TRANS_FILE"


# makes a new Day folder
# makes a new TSFs folder
# run the front end on one of the sample sessions and saves the output to the TSFs folder
# merge the TSFs and save them to MergedTSF.txt
# run the back end the Weeks MAF and the Day's MTSF overwrites the weeks MasterAccounts.txt and ValidAccounts 


echo "Daily script ${DAY_NUM} is done"