FROM clojure:openjdk-8-tools-deps-1.10.1.739-buster
COPY app/target/aidbox-ts-1.0.0-SNAPSHOT-standalone.jar /app.jar
CMD  java -jar /app.jar
