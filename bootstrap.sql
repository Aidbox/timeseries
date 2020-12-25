---- db: -h localhost -p 5488 -U postgres devbox
\d+ patient
----
\c devbox
CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;
----
DROP VIEW if exists hr_view CASCADE;
DROP VIEW if exists pulse_view CASCADE;
DROP VIEW if exists resp_view CASCADE;
DROP VIEW if exists oxy_view CASCADE;

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
SELECT add_dimension('observation_data', 'Patient_id',     number_partitions => 200, if_not_exists => true);
SELECT add_dimension('observation_data', 'Observation_id', number_partitions => 200, if_not_exists => true);

----
\x
select count(*)
from observation_data;
----
\x
select count(*)
from patient;
----
\x
truncate patient;
----
delete from patient
where id in (select id from patient limit 100)
----
select id from patient;
----

select distinct(patient_id)
from observation_data;;
----
\x
select * from  observation_data limit 1;
----
DROP VIEW if exists hr_view CASCADE;
DROP VIEW if exists pulse_view CASCADE;
DROP VIEW if exists resp_view CASCADE;
DROP VIEW if exists oxy_view CASCADE;

truncate observation_data;
----
select patient_id, (array_agg(smooth_bpm))[1]
from (
SELECT
  ts,
  Patient_id,
  AVG(valueQuantity_value) OVER(
    PARTITION BY Patient_id
    ORDER BY ts
    ROWS BETWEEN 60 PRECEDING AND CURRENT ROW
  )
  AS smooth_bpm
FROM observation_data
WHERE code = '8867-4' -- heart rate
  and ts > NOW() - INTERVAL '1 minute'
  --and smooth_bpm > 90
ORDER BY ts DESC
) as avgg
where avgg.smooth_bpm > 100
group by patient_id;
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
HAVING AVG(valueQuantity_value) > 100;

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
HAVING AVG(valueQuantity_value) > 100;

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
HAVING AVG(valueQuantity_value) > 20 or AVG(valueQuantity_value) < 10;

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
HAVING AVG(valueQuantity_value) < 96;

----
\x
SELECT * FROM timescaledb_information.continuous_aggregate_stats;
----
----
----
