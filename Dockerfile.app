FROM clojure:openjdk-11-tools-deps-buster
COPY app/target/aidbox-ts-1.0.0-SNAPSHOT-standalone.jar /app.jar
CMD  java -jar /app.jar --add-modules java.xml.bind
