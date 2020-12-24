---- db: -h localhost -p 5488 -U postgres devbox
----
\c devbox
CREATE EXTENSION IF NOT EXISTS timescaledb;
----
DROP table if exists observation_data;

CREATE TABLE observation_data (
 -- Time fields
 -- -- ts - primary time column, fill from  Observation.effective
 ts                        TIMESTAMPTZ NOT NULL,

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
 valuePeriod_end           TIMESTAMPTZ

 -- -- valueRange
 -- -- valueRatio
 -- -- valueSampledData

);

SELECT create_hypertable('observation_data', 'ts');
----
----
----
