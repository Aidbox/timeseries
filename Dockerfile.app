FROM clojure:latest
COPY app/target/aidbox-ts-1.0.0-SNAPSHOT-standalone.jar /app.jar
CMD  java -jar /app.jar
