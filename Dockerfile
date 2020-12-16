FROM timescale/timescaledb:1.7.4-pg12

RUN set -ex && apk update && apk add --no-cache \
  binutils-gold \
  g++ \
  gcc \
  git \
  clang \
  make \
  llvm \
  postgresql-contrib

RUN cd /usr/share/postgresql/extension \
   && git clone https://github.com/niquola/jsonknife \
   && cd jsonknife \
   && make USE_PGXS=1 \
   && make USE_PGXS=1 install
