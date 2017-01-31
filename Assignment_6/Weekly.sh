#!/bin/bash
# initialize command line argument
WEEK_NUM=$1
WEEK_DIR=Text_Files/Week_${WEEK_NUM}
# creates directory for the week
mkdir $WEEK_DIR

echo Starting Week $WEEK_NUM > Text_Files/OutputLog.txt
echo Starting Week $WEEK_NUM

#runs the back End and processes and places the files in the required location
DAY_NUM=1
while (( DAY_NUM <= 5 ))
do
    echo ——Starting Day: ${DAY_NUM}——  >> Text_Files/OutputLog.txt
    # Load in the correct MAF and VAF into the temp files
    # We use ValidAccounts.txt and MasterAccounts.txt as temporary files
    # We then move the files to the correct location
    if (( DAY_NUM == 1 ))
      then 
        cp EmptyValidAccounts.txt ValidAccounts.txt
        cp EmptyMAF.txt MasterAccounts.txt
      else
        ((YESTERDAY_NUM = DAY_NUM - 1))
	cp $WEEK_DIR/VAF_Day_${YESTERDAY_NUM}.txt ValidAccounts.txt
        cp $WEEK_DIR/MAF_Day_${YESTERDAY_NUM}.txt MasterAccounts.txt
    fi
    # Run the Daily Script 
    bash Daily.sh $WEEK_DIR $DAY_NUM ValidAccounts.txt MasterAccounts.txt
    # Save the files in the Week’s directory
    cp new_MasterAccounts.txt $WEEK_DIR/MAF_Day_${DAY_NUM}.txt
    cp new_ValidAccounts.txt $WEEK_DIR/VAF_Day_${DAY_NUM}.txt
    ((DAY_NUM++))
    
done

# clean up by removing the temporary text files
rm *new*
rm MasterAccounts.txt
rm TSF.txt
rm ValidAccounts.txt
echo "Week script $WEEK_NUM done"