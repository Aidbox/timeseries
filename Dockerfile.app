FROM clojure:latest
COPY app/app.jar /app.jar
CMD  java -jar /app.jar
