FROM bde2020/spark-submit:2.4.5-hadoop2.7

LABEL maintainer="Gezim Sejdiu <g.sejdiu@gmail.com>, Giannis Mouchakis <gmouchakis@gmail.com>"

ENV SPARK_APPLICATION_JAR_NAME MarkLogicSparkMock-1.0-SNAPSHOT
ENV SPARK_APPLICATION_MAIN_CLASS Main

COPY template.sh /

RUN apk add --no-cache openjdk8 maven\
      && chmod +x /template.sh \
      && mkdir -p /app \
      && mkdir -p /usr/src/app



# Copy the source code and build the application
COPY . /usr/src/app
RUN cd /usr/src/app \
      && ./gradlew build


CMD ["/bin/bash", "/template.sh"]