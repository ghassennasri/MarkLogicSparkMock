# MarkLogicSparkMock  
This is sample apache spark 2.4.5 job demo reading from an xml file books.xml (https://docs.microsoft.com/en-us/previous-versions/windows/desktop/ms762271(v%3Dvs.85))  
and writing books as MarkLogic xml documents.  
Images setting up a standalone Apache Spark 2.4.5 cluster (running one Spark Master and multiple Spark workers) were pulled from https://github.com/big-data-europe/docker-spark  
Submit image taken from the same source was also adapted to make the spark-submit command fit job dependencies and repositories    
  
## Prerequisites  
 - Docker and Docker-compose  
  
## Running the demo application  
 - Download the application  
 - Navigate to project root directory Execute Docker-compose up command :  
```  
docker-compose  up -d  
```  
The command will build the following services :  
 - marklogic-server : Initialized MarkLogic instance with admin username and password set to admin
 - spark-master : spark master node (Master webUI available from localhost at http://localhost:8080 having a mounted volume /opt/spark-data bound to ./data directory of the project.
 - spark-worker-1: spark worker node linked to spark-master node and  having a mounted volume /opt/spark-data bound to ./data directory of the project.
 
You will also need to build and run the application docker image by executing the command:

    docker build --network marklogicsparkmock_default -t bde/my-saprk-app . && docker run --name my-spark-app -d -e ENABLE_INIT_DAEMON=false --link spark-master --network marklogicsparkmock_default bde/my-saprk-app 

The application image inherit from bde2020/spark-submit:2.4.5-hadoop2.7 and will do the following:

 1. Copy the application code to /opt/spark-data directory of the container OS and execute gradle mlDeploy task to build the application jar file and set up a REST application server on MarkLogic side. 
Port and authentication mode are set by default to 8010 and Basic.
You can change them by adapting files rest-api-server.json, gradle.properties and config.properties.
The spark job main class is [Main.java
 ](https://github.com/ghassennasri/MarkLogicSparkMock/blob/master/src/main/java/Main.java)

 2.  The application jar file will be submitted to spark-master via the spark-submit command (see submit-modified.sh)

Once the container is run, you can visualize the spark job application log by executing the command:

    docker logs my-spark-app
  
 An example of what the log would look like is included in the project files [ApplicationContainerLog.txt
 ](https://github.com/ghassennasri/MarkLogicSparkMock/blob/master/ApplicationContainerLog.txt)
