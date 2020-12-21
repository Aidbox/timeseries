.EXPORT_ALL_VARIABLES:

include .env

up:
	docker-compose -p aidbox-ts up -d
down:
	docker-compose -p aidbox-ts down
build-db:
	docker build -t ${PG_IMAGE} .
push-db:
	docker push ${PG_IMAGE}

app-repl:
	cd app && clj  -M:test:nrepl

app-build:
	#cd app && clojure -A:build
	cd app && clojure -A:uberjar
app-run:
	#java -jar app/target/app-1.0.0-SNAPSHOT-standalone.jar -m aidbox.timeseries
	java -jar app/app.jar


ui-repl:
	cd ui && clj -A:shadow:dev:test watch app
