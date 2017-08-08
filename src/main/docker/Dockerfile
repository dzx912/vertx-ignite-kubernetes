FROM java

# Define working directory
WORKDIR /opt/test-app/config

RUN mkdir /opt/test-app/jar

# Deploy core
ADD config/ignite.xml /opt/test-app/config/ignite.xml

ADD build/libs/apache-ignite-vertx-test-*.jar /opt/test-app/jar/app.jar

ENTRYPOINT java -jar /opt/test-app/jar/app.jar