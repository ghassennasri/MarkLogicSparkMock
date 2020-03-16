#!/bin/bash

cd /usr/src/app
cp build/libs/${SPARK_APPLICATION_JAR_NAME}.jar ${SPARK_APPLICATION_JAR_LOCATION}

sh /submit.sh