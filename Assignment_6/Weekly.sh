#!/bin/bash

WEEK_DIR=Text_Files/Week_${1}
mkdir $WEEK_DIR
DAY_NUM=1

while (( DAY_NUM <= 4 ))
do
    if (( DAY_NUM == 1 ))
      then 
        cp EmptyValidAccounts.txt ValidAccounts.txt
        cp EmptyMAF.txt MasterAccounts.txt
      else
        ((YESTERDAY_NUM = DAY_NUM - 1))
	cp $WEEK_DIR/VAF_Day_${YESTERDAY_NUM}.txt ValidAccounts.txt
        cp $WEEK_DIR/MAF_Day_${YESTERDAY_NUM}.txt MasterAccounts.txt
    fi
    bash Daily.sh $WEEK_DIR $DAY_NUM ValidAccounts.txt MasterAccounts.txt
    cp new_MasterAccounts.txt $WEEK_DIR/MAF_Day_${DAY_NUM}.txt
    cp new_ValidAccounts.txt $WEEK_DIR/VAF_Day_${DAY_NUM}.txt
    ((DAY_NUM++))
    
done
echo "Week script done"