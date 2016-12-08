#!/bin/bash
WEEK_NUM=$1
WEEK_DIR=Text_Files/Week_${WEEK_NUM}
mkdir $WEEK_DIR
DAY_NUM=1

echo Starting Week $WEEK_NUM > Text_Files/ErrorLog.txt
echo Starting Week $WEEK_NUM

while (( DAY_NUM <= 5 ))
do
    echo ——Starting Day: ${DAY_NUM}——  >> Text_Files/ErrorLog.txt
    # Load in the correct MAF and VAF into the temp files
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
    cp new_MasterAccounts.txt $WEEK_DIR/MAF_Day_${DAY_NUM}.txt
    cp new_ValidAccounts.txt $WEEK_DIR/VAF_Day_${DAY_NUM}.txt
    ((DAY_NUM++))
    
done

# clean up by removing temp text files
rm *new*
rm MasterAccounts.txt
rm TSF.txt
rm ValidAccounts.txt
echo "Week script $WEEK_NUM done"