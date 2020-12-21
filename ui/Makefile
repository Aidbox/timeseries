.EXPORT_ALL_VARIABLES:
.PHONY: test build

dev:
	clj -A:shadow:dev:test watch app
test:
	clj -A:test:run-test
build:
	clj -A:shadow release app
report:
	clj -A:report app report.html
docker:
	docker build -t narus-pwa .
docker-run:
	docker run -e APP_VERSION=app-version -e BASE_URL="http://localhost:8888" -p 8088:80 narus-pwa
