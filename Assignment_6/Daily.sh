#!/bin/bash
WEEK_DIR=$1
DAY_NUM=$2
VAF_NAME=$3
MAF_NAME=$4
DAY_DIR=${WEEK_DIR}/DAY_${DAY_NUM}
TSF_DIR=${DAY_DIR}/TSFs
MERGED_TSF=${DAY_DIR}/MergedTSF_${DAY_NUM}
mkdir $DAY_DIR
mkdir $TSF_DIR

# Run the three front end sessions
FRONT_END_NUM=1
while (( $FRONT_END_NUM <= 3 ))
do
    (( TRANS_NUM = $FRONT_END_NUM + 3 * (DAY_NUM - 1) ))
    TRANS_FILE=Text_Files/Transactions/Transactions${TRANS_NUM}.txt
    java -classpath FrontEnd FrontEndStartPoint $VAF_NAME TSF.txt < $TRANS_FILE >> Text_Files/ErrorLog.txt
    cp TSF.txt $TSF_DIR/TSF_${FRONT_END_NUM}.txt
    cat $TSF_DIR/TSF_${FRONT_END_NUM}.txt >> $MERGED_TSF
    ((FRONT_END_NUM++))
done

java -classpath BackEnd BackEndStartPoint $MAF_NAME $VAF_NAME $MERGED_TSF >> Text_Files/ErrorLog.txt


echo "Daily script ${DAY_NUM} is done"