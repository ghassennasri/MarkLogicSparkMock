# MarkLogicSparkMock
This is sample apache spark job demo reading from an xml file books.xml (https://docs.microsoft.com/en-us/previous-versions/windows/desktop/ms762271(v%3Dvs.85))
and writing books as MarkLogic xml documents.
Images setting up a standalone Apache Spark cluster (running one Spark Master and multiple Spark workers) were pulled from https://github.com/big-data-europe/docker-spark
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
  - marklogic-server:
 
