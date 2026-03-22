#!/bin/bash

export CLASSPATH=".:dist/*"
MS_REGION_NAME=$1
MS_VERSION=$2
MS_SUBVERSION=$3
WZ_XML_PATH=${PWD}/wz_xml/xml_${MS_REGION_NAME}_v${MS_VERSION}/
SCRIPT_PATH=${PWD}/scripts/scripts_${MS_REGION_NAME}/

echo $MS_REGION_NAME
echo $MS_VERSION
echo $MS_SUBVERSION
echo $WZ_XML_PATH
echo $SCRIPT_PATH
java -server -Driresaba.path.wz=${WZ_XML_PATH} -Driresaba.path.script=${SCRIPT_PATH} -Djavax.net.ssl.keyStore=filename.keystore -Djavax.net.ssl.keyStorePassword=passwd -Djavax.net.ssl.trustStore=filename.keystore -Djavax.net.ssl.trustStorePassword=passwd tacos.Start ${MS_REGION_NAME} ${MS_VERSION} ${MS_SUBVERSION}
