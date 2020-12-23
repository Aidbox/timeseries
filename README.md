# Aidbox with Timescale DB ![Clojure CI](https://github.com/Aidbox/timeseries/workflows/Clojure%20CI/badge.svg)


## Quik Installation

* Get DevBox license key https://license-ui.aidbox.app

* Copy `.license.tpl` to `.license`
``` bash
cp .license.tpl .license
```

* Edit `.license` file and enter your `License ID` and `License Key`
``` bash
AIDBOX_LICENSE_ID=<your-license-id-here>
AIDBOX_LICENSE_KEY=<your-license-key-here>
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

## Performance compe

- compare default aidbox structure and TS hypertable
...



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
