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
