## ZipInfo REST API ##

ZipInfo Rest API provides location information for a supplied U.S. Zip Code.
The API wraps individual API calls to OpenWeatherMap.com API and Google Maps API.

***Required:*** In order to run and use this API successfully, you will be required to
supply individual API keys for OpenWeatherMap and Google APIs.

see: https://openweathermap.org/api
see: https://cloud.google.com/maps-platform/#get-started

#### Configuration ####

The following configuration options are accessible from the file config.json in the root directory:

{
	"http.port": 8080,
	"apis": {
		"google": {
			"key": "INSERT_GOOGLE_API_KEY_HERE",
			"host.maps": "maps.googleapis.com",
			"ssl": true
		},
		"openWeatherMap": {
			"key": "INSERT_OPEN_WEATHER_MAP_API_KEY_HERE",
			"host": "api.openweathermap.org",
			"ssl": true
		}
	}
}

***Required:*** In order to run and use this API successfully, you will be required to
supply individual API keys for OpenWeatherMap and Google APIs.

see: https://openweathermap.org/api
see: https://cloud.google.com/maps-platform/#get-started

#### Running Server ####

To run this server from source files, simply run the following command from the root directory:

`sh build_and_run.sh`
