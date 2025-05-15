# Aidbox with Timescale DB ![Clojure CI](https://github.com/Aidbox/timeseries/workflows/Clojure%20CI/badge.svg)


## Quik Installation

* Get Aidbox development license  https://aidbox.app

* Copy `.license.tpl` to `.license`
``` bash
cp .license.tpl .license
```

* Edit `.license` file and enter your `License ID` and `License Key`
``` bash
AIDBOX_LICENSE=<your-license-id-here>
```

* Run `docker-compose.yml`
``` bash
make up
```

* Open in browser http://localhost:8585

## What inside WIP

* Aidbox/devbox ....
* Timescale db adopted for Aidbox .....
* Aidbox app for
  * converting Observation in to TS hypertabe
  * reading TS hypertabe
* Large dataset for demonstrating performance
  * ECG dataset is a large dataset of 21837 clinical 12-lead ECGs from 18885 patients of 10 second length from https://physionet.org/content/ptb-xl/1.0.1/
  * Heart Rate
* Simple UI for demonstrating ECG results ...
* Grafana for mode options ....

## Performance comape

- compare default aidbox structure and TS hypertable
...



## Problem

  App          ->   TS storage
[aidbox + pg]     [prometheus ]


1) 2 systems
2) Data join

  App
[aidbox + Timescale]

Alert!!
job ?

1) Med documents  ECG/EEG and etc
2) Primitive TS   hear_rate/blood_pressure/resperatory_rate/spo2 (hf/lf) etc
3) Complex TS     Medical devices ALV/reanimation/....

High frequency data
Low frequency data


----
data_1.csv

hr spo2 respra  foo
80  80   23     88
80  80   23     88
80  80   23     88
80  80   23     88
80  80   23     88


device-table.csv

pt_id     file
123123    data_1.csv
123231    data_2.csv
555555    data_1.csv
999999    data_2.csv
000011    data_1.csv
123123    data_2.csv


FHIR - Only Observation

Observation = Meta information + Data (very large)

HR tracer
POST /Observation

{:deviceId "xiaomi_123213"
 :subject {:id "123" :resourceType "Patient"}
 :device {:id "marat_iphone"}
 :type {:code "heart_rate"}
 :unit {:code "bpm}
 .......}


 Status: 200 Ok
 Body:
 {:resourceType "Obeservation"
  :id "33333"}


POST /Observation/33333/$record

Observation.component
``` yaml
period:
  start: 2020-12-01
  end: 2020-12-01
code:
  coding:
  - code: 123

```

```
   type="HKQuantityTypeIdentifierHeartRate"
   sourceName="Александра’s Apple Watch"
   sourceVersion="5.1.2"
   device="&lt;&lt;HKDevice: 0x281293340&gt;, name:Apple Watch, manufacturer:Apple, model:Watch, hardware:Watch4,1, software:5.1.2&gt;"

   unit         = "count/min"
   creationDate = "2019-01-20 16:12:09 +0200"
   startDate    = "2019-01-20 16:08:03 +0200"
   endDate      = "2019-01-20 16:08:03 +0200"
   value        = "55"
```




GET /Observation


## Development

* Run `docker-compose.dev.yml` env
``` bash
make up-dev
```
* Run aidbox app
``` bash
make app-repl
```
* Run ui
``` bash
make ui-repl
```









## License

Copyright © 2020 Aidbox

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
