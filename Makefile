.EXPORT_ALL_VARIABLES:

# Run presentation env
up:
	docker-compose -p aidbox-ts up -d
down:
	docker-compose -p aidbox-ts down
#------------------

# Run dev env
up-dev:
	docker-compose -f docker-compose.dev.yaml -p aidbox-ts-dev up -d
down-dev:
	docker-compose -f docker-compose.dev.yaml -p aidbox-ts-dev down
#------------------

build-db:
	docker build -t ${PG_IMAGE} .
push-db:
	docker push ${PG_IMAGE}

app-repl:
	cd app && clj  -M:test:nrepl

app-build:
	#cd app && clojure -A:uberjar
	cd app && clojure -A:build
app-run:
	java -jar app/app.jar
app-run-container:
	docker run aidbox/aidbox-ts-app:main


ui-repl:
	cd ui && clj -A:shadow:dev:test watch app
ui-build:
	cd ui && clj -A:shadow release app
