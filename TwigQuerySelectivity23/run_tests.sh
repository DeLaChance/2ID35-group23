#!/bin/bash
for height in `seq 10 10 100`
do
	for leafs in `seq 10000 10000 100000`
	do
		for depthVar in `seq 0.1 0.1 1`
		do
			for interconnectedness in `seq 0 0.1 0.5`
			do
				`java -jar dist/TwigQuerySelectivity23.jar $height $leafs $depthVar $interconnectedness 1000 --summary >> testresults/averages.csv`
				echo "ran 1000 queries on graph with height=$height, leafs=$leafs, depthVar=$depthVar, interconnectedness=$interconnectedness"
			done
		done
	done
done
echo "Done!!"
