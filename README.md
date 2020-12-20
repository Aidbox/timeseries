# Aidbox with TimeScale DB


## Install

* Get DevBox license key
* Copy `.license.tpl` to `.license`
``` bash
cp .license.tpl .license
```
* Edit `.license` file and enter your License ID and License Key
* Run docker compose
``` bash
make up
```







hypertable

observationid  | ts                     | data | code                 | deviceid  | patientid  | unit
------------------------------------------------------------------------------------------------------
ekg            | 19/02/2015 9:30:35.000 | 2041 | MDC_ECG_ELEC_POTL_I  | 123       | 123        | mv
ekg            | 19/02/2015 9:30:35.010 | 2043 | MDC_ECG_ELEC_POTL_I  | 123       | 123        | mv
ekg            | 19/02/2015 9:30:35.020 | 2037 | MDC_ECG_ELEC_POTL_I  | 123       | 123        | mv
ekg            | 19/02/2015 9:30:35.030 | 2047 | MDC_ECG_ELEC_POTL_I  | 123       | 123        | mv
ekg            | 19/02/2015 9:30:35.040 | 2060 | MDC_ECG_ELEC_POTL_I  | 123       | 123        | mv
ekg            | 19/02/2015 9:30:35.050 | 2062 | MDC_ECG_ELEC_POTL_I  | 123       | 123        | mv
....																  | 123       | 123        | mv
ekg            | 19/02/2015 9:30:35.000 | 2041 | MDC_ECG_ELEC_POTL_II | 123       | 123        | mv
ekg            | 19/02/2015 9:30:35.010 | 2043 | MDC_ECG_ELEC_POTL_II | 123       | 123        | mv
ekg            | 19/02/2015 9:30:35.020 | 2037 | MDC_ECG_ELEC_POTL_II | 123       | 123        | mv
ekg            | 19/02/2015 9:30:35.030 | 2047 | MDC_ECG_ELEC_POTL_II | 123       | 123        | mv
ekg            | 19/02/2015 9:30:35.040 | 2060 | MDC_ECG_ELEC_POTL_II | 123       | 123        | mv
ekg            | 19/02/2015 9:30:35.050 | 2062 | MDC_ECG_ELEC_POTL_II | 123       | 123        | mv
....







FIXME: my new application.

## Installation

Download from https://github.com/aidbox/timeseries

## Usage

FIXME: explanation

Run the project directly:

    $ clojure -M -m aidbox.timeseries

Run the project's tests (they'll fail until you edit them):

    $ clojure -M:test:runner

Build an uberjar:

    $ clojure -M:uberjar

Run that uberjar:

    $ java -jar timeseries.jar

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2020 Aidbox

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
