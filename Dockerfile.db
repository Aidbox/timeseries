FROM golang:1.12.7-alpine AS wal-g-builder
MAINTAINER apkawa <apkawa@gmail.com>

ENV WALG_VERSION=v0.2.19

ENV _build_deps="wget cmake git build-base bash"

RUN set -ex  \
     && apk add --no-cache $_build_deps \
     && git clone https://github.com/wal-g/wal-g/  $GOPATH/src/wal-g \
     && cd $GOPATH/src/wal-g/ \
     && git checkout $WALG_VERSION \
     &&  export GO111MODULE="on" \
     && GO111MODULE="on" make install \
     && GO111MODULE="on" make deps \
     && GO111MODULE="on" make pg_build \
     && install main/pg/wal-g / \
     && /wal-g --version

FROM timescale/timescaledb-postgis:1.7.4-pg12

RUN set -ex \
	&& apk update \
	&& apk add --no-cache \
	binutils-gold \
	g++ \
	gcc \
	git \
	clang \
	lzo-dev \
	tar \
	make \
	llvm \
	postgresql-contrib \
	wget \
	bison \
	flex \
	openssl-dev \
	zlib-dev

# Install wal-g
COPY --from=wal-g-builder /wal-g /usr/local/bin

# Install jsonknife
RUN cd /usr/share/postgresql/extension \
	&& git clone https://github.com/niquola/jsonknife \
	&& cd jsonknife \
	&& make USE_PGXS=1 \
	&& make USE_PGXS=1 install

# Install jsquery
RUN cd /usr/share/postgresql/extension \
	&& git clone https://github.com/postgrespro/jsquery.git \
	&& cd jsquery \
	&& make USE_PGXS=1 \
	&& make USE_PGXS=1 install

# Install pg_repack
RUN cd /usr/share/postgresql/extension \
	&& wget -q -O pg_repack-1.4.6.zip http://api.pgxn.org/dist/pg_repack/1.4.6/pg_repack-1.4.6.zip \
	&& unzip pg_repack-1.4.6.zip \
	&& cd pg_repack-1.4.6 \
	&& make \
	&& make install
