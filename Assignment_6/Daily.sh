#!/bin/bash
# Get the input variables 
WEEK_DIR=$1
DAY_NUM=$2
VAF_NAME=$3
MAF_NAME=$4
# create directory variables
DAY_DIR=${WEEK_DIR}/DAY_${DAY_NUM}
TSF_DIR=${DAY_DIR}/TSFs
MERGED_TSF=${DAY_DIR}/MergedTSF_${DAY_NUM}.txt
# Make the new directories 
mkdir $DAY_DIR
mkdir $TSF_DIR

# Run the three front end sessions
FRONT_END_NUM=1
while (( $FRONT_END_NUM <= 3 ))
do
    (( TRANS_NUM = $FRONT_END_NUM + 3 * (DAY_NUM - 1) ))
    TRANS_FILE=Text_Files/Transactions/Transactions${TRANS_NUM}.txt
    java -classpath FrontEnd FrontEndStartPoint $VAF_NAME TSF.txt < $TRANS_FILE >> Text_Files/OutputLog.txt
    cp TSF.txt $TSF_DIR/TSF_${FRONT_END_NUM}.txt
    cat $TSF_DIR/TSF_${FRONT_END_NUM}.txt >> $MERGED_TSF
    ((FRONT_END_NUM++))
done

# Format the merged Transaction Summary File removing all the End of service lines. 
# and adding a new one to the end.
sed /ES\t*/d $MERGED_TSF > temp.txt
echo ES 00000000 00000000 000 \*\*\* >> temp.txt
mv temp.txt $MERGED_TSF

# Run the back End on the merged TSF and the input MAF and VAF
java -classpath BackEnd BackEndStartPoint $MAF_NAME $VAF_NAME $MERGED_TSF >> Text_Files/OutputLog.txt


echo "Daily script ${DAY_NUM} is done"