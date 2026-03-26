#!/bin/bash

if [ $# -ne 3 ]; then
	echo "usage : bash run_any.sh JMS 147 0 (region version minor_version)"
	exit 0
fi

export CLASSPATH=".:dist/*"
MS_REGION_NAME=$1
MS_VERSION=$2
MS_SUBVERSION=$3
java -server tacos.Start ${MS_REGION_NAME} ${MS_VERSION} ${MS_SUBVERSION}
