#!/bin/bash
for i in `seq 0 100`
do
	`java -jar dist/TwigQuerySelectivity23.jar 60 100000 0.1 0.5 10000 --summary >> testresults/averages.csv`
	echo "Ran about 10000 querys on grap $i"
done
echo "Done!"
