PG_IMAGE    = aidbox/ts-pg12
PG_PORT     = 5444
PG_PASSWORD = postgres
PG_USER     = postgres

GF_EMBEDDING = true
GF_ANONYMOUS = true

.EXPORT_ALL_VARIABLES:

up:
	docker-compose -p aidbox-ts up -d
down:
	docker-compose -p aidbox-ts down
build-db:
	docker build -t ${PG_IMAGE} .
push-db:
	docker push ${PG_IMAGE}
