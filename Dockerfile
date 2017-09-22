FROM agrdocker/agr_java_env:latest

WORKDIR /workdir/agr_indexer

ADD . .
RUN mvn clean package

CMD java -jar -Xmn8g -Xms8g target/agr_indexer-jar-with-dependencies.jar
