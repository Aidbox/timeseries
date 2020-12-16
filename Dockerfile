FROM timescale/timescaledb:1.7.4-pg12

RUN set -ex && apk update && apk add --no-cache \
  binutils-gold \
  g++ \
  gcc \
  git \
  clang \
  make \
  llvm \
  postgresql-contrib \
  wget


RUN cd /usr/share/postgresql/extension \
  && git clone https://github.com/niquola/jsonknife \
  && cd jsonknife \
  && make USE_PGXS=1 \
  && make USE_PGXS=1 install


RUN apk add --no-cache \
  bison \
  flex \
  && cd /usr/share/postgresql/extension \
  && git clone https://github.com/postgrespro/jsquery.git \
  && cd jsquery \
  && make USE_PGXS=1 \
  && make USE_PGXS=1 install
  # psql DB -c "CREATE EXTENSION jsquery;"

RUN apk add --no-cache \
  openssl-dev \
  zlib-dev \
  && wget -q -O /pg_repack-1.4.6.zip http://api.pgxn.org/dist/pg_repack/1.4.6/pg_repack-1.4.6.zip \
  && unzip /pg_repack-1.4.6.zip \
  && cd /pg_repack-1.4.6 \
  && make \
  && make install
  # psql -c "CREATE EXTENSION pg_repack" -d your_database


# RUN apk add --no-cache \
#   lzo-dev \
#   wget \
#   tar \
#   && mkdir -p /pg \
#   && wget -q -O /pg/wal-g.tar.gz "https://github.com/wal-g/wal-g/releases/download/v0.2.19/wal-g.linux-amd64.tar.gz" \
#   && cd /pg && tar -zxvf wal-g.tar.gz \
#   && mv wal-g /bin/
