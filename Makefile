build-db:
	docker build -t aidboxdbts-pg12 .
build-wal-g:
	docker build -t alpine-wal-g -f Dockerfile.wal-g .
run-db:
	docker run -p 5444:5432 --env POSTGRES_PASSWORD=postgres aidboxdbts-pg12
up:
	docker-compose up -d
