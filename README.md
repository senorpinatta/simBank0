# SimBank

This project was developed for Queens CISC 327 : Software Quality Assurance
It contains two components - a front end and a back end.
The front end functions as the software/UI for an atm console into which transactions are logged.
It end does error checking to ensure that the supplied information meets constraints on amounts, 
length of names ect. It produces a record of all the transactions logged on that ATM in a given day.

Using bash scripts we then combine possible Transaction Summary Files (TSF) into a daliy master file.
This file is then given to the back end for processing. 

The back end functions as the actual bank records. 
The back end accepts the master TSF and attempts to update it's records based on the supplied transactions. 
It does error checking to ensure that balances remain non negative, that closed accounts cannot be accesed, 
ect. If it encounters invalid transactions it rejects them. It also creats a summary file of transactions
it could not complete for notificiation and analysis. 

We also developed a hand written test suite for the front and back end. 
The front end consists of a black box testing suit based on our supplied requirements.
While the back end consists of white box test tests useing code coverage and input partitioning.

This software was developed by myself and classmates Ben Mitchel and Zaid Mohmand.

This repository is a clone of one that can be found at https://github.com/BenjaminNMitchell/SimBank which is the actual repository in which this software was developed.
