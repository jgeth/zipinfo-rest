## ZipInfo REST API ##

ZipInfo Rest API provides location information for a supplied U.S. Zip Code.
The API wraps individual API calls to OpenWeatherMap.com API and Google Maps API.

#### Configuration ####

The following configuration options are accessible from the file config.json in the root directory:

- HTTP port: 8080 (default)
- Google Maps API key: supply your own
- OpenWeatherMap API key: supply your own

***Required:*** In order to run and use this API successfully, you will be required to
supply individual API keys for OpenWeatherMap and Google APIs.

- https://openweathermap.org/api
- https://cloud.google.com/maps-platform/#get-started

#### Running Server ####

To run this server from source files, simply run the following command from the root directory:

`sh build_and_run.sh`
