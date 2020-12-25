---- db: -h localhost -p 5488 -U postgres devbox
\d+ patient
----
select id from patient;
----
\c devbox
CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;
----
DROP VIEW if exists hr_view CASCADE;
DROP VIEW if exists pulse_view CASCADE;
DROP VIEW if exists resp_view CASCADE;
DROP VIEW if exists oxy_view CASCADE;
----

DROP table if exists observation_data;

CREATE TABLE observation_data (
 -- Time fields
 -- -- ts - primary time column, fill from  Observation.effective
 ts                        TIMESTAMPTZ NOT NULL,
 Observation_id            TEXT,
 Patient_id                TEXT,

 -- codings
 -- -- code
 code                      TEXT,
 -- -- system
 system                    TEXT,
 -- -- display
 display                   TEXT,

 -- --  effectiveDateTime
 effectiveDateTime         TIMESTAMPTZ ,
 -- --  effectiveInstant
 effectiveInstant          TIMESTAMPTZ,
 -- --  effectivePeriod
 effectivePeriod_start     TIMESTAMPTZ,
 effectivePeriod_end       TIMESTAMPTZ,
 -- -- effectiveTiming


 -- Data fields
 -- -- valueQuantity
 valueQuantity_value       DECIMAL,
 valueQuantity_comparator  TEXT,
 valueQuantity_unit        TEXT,
 valueQuantity_system      TEXT,
 valueQuantity_code        TEXT,
 -- -- valueString
 valueString               TEXT,
 -- -- valueBoolean
 valueBoolean              BOOLEAN,
 -- -- valueInteger
 valueInteger              INTEGER,
 -- -- valueTime
 valueTime                 TIMESTAMPTZ,
 -- -- valueDateTime
 valueDateTime             TIMESTAMPTZ,
 -- -- valuePeriod
 valuePeriod_start         TIMESTAMPTZ,
 valuePeriod_end           TIMESTAMPTZ,

 -- -- valueRange
 -- -- valueRatio
 -- -- valueSampledData
 valueSampledData_data     DECIMAL

);

SELECT create_hypertable('observation_data', 'ts');
SELECT add_dimension('observation_data', 'patient_id',     number_partitions => 20, if_not_exists => true);
SELECT add_dimension('observation_data', 'observation_id', number_partitions => 20, if_not_exists => true);

----
\d+ observation_data
----

SELECT
pid ,wait_event, age(clock_timestamp(), query_start) ,query
FROM pg_stat_activity
WHERE query != '<IDLE>' AND query NOT ILIKE '%pg_stat_activity%'  and "state" = 'active'
ORDER BY query_start  nulls last;
----
\x
select count(*)
from observation_data;
----
\x
select count(*)
from observation;
----
\x
truncate observation;
----

select distinct(patient_id)
from observation_data;;
----
\x
select * from  observation_data limit 1;
----
----
DROP VIEW if exists hr_view CASCADE;
DROP VIEW if exists pulse_view CASCADE;
DROP VIEW if exists resp_view CASCADE;
DROP VIEW if exists oxy_view CASCADE;

truncate observation_data;
----
----
SELECT
  time_bucket('10s', ts) as bucket,
  Patient_id,
  AVG(valueQuantity_value) as avg_bpm,
  array_agg(valueQuantity_value)
FROM observation_data
WHERE code = '8867-4' -- heart rate
group by patient_id, time_bucket('10s', ts)
HAVING AVG(valueQuantity_value) > 100
ORDER BY bucket

----

1 avg(1 2 3)
2 avg(2 3 4)
3 avg(3 4 5)
4
5


SELECT
  $__time(ts),
  valueQuantity_value
FROM
  observation_data

----
----------------------------------------------------------------------
DROP VIEW if exists hr_view CASCADE;


CREATE VIEW hr_view WITH
(timescaledb.continuous, timescaledb.refresh_interval = '30s')
AS
SELECT
  time_bucket('10s', ts) as time,
  Patient_id,
  AVG(valueQuantity_value) as avg
FROM observation_data
WHERE code = '8867-4'
group by patient_id, time_bucket('10s', ts)
HAVING AVG(valueQuantity_value) > 100
----
DROP VIEW if exists pulse_view CASCADE;

CREATE VIEW pulse_view WITH
(timescaledb.continuous, timescaledb.refresh_interval = '30s')
AS
SELECT
  time_bucket('10s', ts) as time,
  Patient_id,
  AVG(valueQuantity_value) as avg
FROM observation_data
WHERE code = '8867-3' -- pulse
group by patient_id, time_bucket('10s', ts)
HAVING AVG(valueQuantity_value) > 100
----
DROP VIEW if exists resp_view CASCADE;

CREATE VIEW resp_view WITH
(timescaledb.continuous, timescaledb.refresh_interval = '30s')
AS
SELECT
  time_bucket('10s', ts) as time,
  Patient_id,
  AVG(valueQuantity_value) as avg
FROM observation_data
WHERE code = '9279-1' -- resp
group by patient_id, time_bucket('10s', ts)
HAVING AVG(valueQuantity_value) > 20 or AVG(valueQuantity_value) < 10
----
DROP VIEW if exists oxy_view CASCADE;

CREATE VIEW oxy_view WITH
(timescaledb.continuous, timescaledb.refresh_interval = '30s')
AS
SELECT
  time_bucket('10s', ts) as time,
  Patient_id,
  AVG(valueQuantity_value) as avg
FROM observation_data
WHERE code = '2708-6' -- resp
group by patient_id, time_bucket('10s', ts)
HAVING AVG(valueQuantity_value) < 96

----
-- ECG
----
explain analyze
SELECT ts AS "time"
, valuesampleddata_data I
FROM observation_data
where code = '131329'
--and ts BETWEEN (1601823599500)::timestamp AND (1601823610500)::timestamp
and ts BETWEEN '2020-10-04T14:59:59.500Z' AND '2020-10-04T15:00:10.500Z'
and patient_id = 'bfd8ecb3-06dd-8f08-7121-020a9a589602'
order by ts
--limit 1000

----
-- Oxygen
SELECT
ts as time,
valueQuantity_value oxygen_saturation
FROM observation_data
WHERE ts BETWEEN '2020-12-24T10:49:43.374Z' AND '2020-12-27T21:04:43.374Z'
and code = '2708-6'
and patient_id = 'bb1cf28f-b3b6-5a90-22f3-71dcecb6fad5' order by ts desc
limit 10

----
SELECT ts as time,
valueQuantity_value oxygen_saturation
FROM observation_data
WHERE
ts BETWEEN '2020-12-25T16:43:40.231Z' AND '2020-12-25T16:58:40.231Z'
and code = '2708-6'
and patient_id = 'bffc6742-0671-0f7f-3837-d3ecdbb31e8b' order by ts desc limit 10
----
show max_connection;
----
delete from patient
where id in (select id from patient limit 100);
----
select id from patient;
----
select count(*) from observation_data;
----
select 1;
----
create index obsdatacode on observation_data (code);
----
analyze observation_data;
----
\x
SELECT * FROM timescaledb_information.continuous_aggregate_stats;
----

----
----
