package zipinfo;

import zipinfo.client.GoogleMapsClient;
import zipinfo.client.OpenWeatherMapClient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler;

public class MainVerticle extends AbstractVerticle {

@Override
public void start() {

	Router router = Router.router(vertx);

	// Zip Code Info end-point
	router.get("/:zip_code")
		// Add Regex validation to ensure resource is a valid Zip Code format
		.handler(HTTPRequestValidationHandler.create()
			.addPathParamWithPattern("zip_code", "[0-9]{5}(-[0-9]{4})?"))
		.handler(routingContext->{
			RequestParameters params = routingContext.get("parsedParameters");

			// Parse Zip Code 5 from request
			String zip5 = params.pathParameter("zip_code").getString().substring(0,5);

			// Retrieve configurations for API clients
			JsonObject apiConfig = config().getJsonObject("apis");

			// OpenWeatherMap API configurations
			JsonObject openWeatherMapConfig = apiConfig.getJsonObject("openWeatherMap");

			// Create OpenWeatherMap client
			OpenWeatherMapClient openWeatherMap = new OpenWeatherMapClient(
				getVertx(),
				(System.getenv("OPENWEATHERMAP_KEY") != null)
					? System.getenv("OPENWEATHERMAP_KEY")
					: openWeatherMapConfig.getString("key"),
				openWeatherMapConfig.getString("host"),
				openWeatherMapConfig.getBoolean("ssl"));

			// Query (asynchronously) OpenWeatherMap API for current weather information by zip code
			openWeatherMap.getCurrentWeatherByZip(zip5, wr -> {

				// Request succeeded, populate weather and move on to next request
				if (wr.succeeded()) {
					JsonObject weather = wr.result();

					// Google Maps API configurations
					JsonObject googleApiConfig = apiConfig.getJsonObject("google");

					// Create Google Maps client
					GoogleMapsClient googleMaps = new GoogleMapsClient(
						getVertx(),
						(System.getenv("GOOGLE_API_KEY") != null)
							? System.getenv("GOOGLE_API_KEY")
							: googleApiConfig.getString("key"),
						googleApiConfig.getString("host.maps"),
						googleApiConfig.getBoolean("ssl"));

					// Query (asynchronously) Google Maps API for timezone by geolocation
					googleMaps.getTimeZone(weather.getJsonObject("coord"), tr -> {

						final String timezone = tr.succeeded() ? tr.result() : null;

						// Query (asynchronously) Google Maps API for elevation by geolocation
						googleMaps.getElevation(weather.getJsonObject("coord"), er -> {

							// Construct JSON response payload
							JsonObject response = new JsonObject()
								.put("city", weather.getString("city"))
								.put("temp", weather.getDouble("temp"))
								.put("timezone", timezone)
								.put("elevation", er.succeeded() ? er.result() : null);

							// Respond with HTTP 200 and idempotent JSON response
							// NOTE: Response status code is 200 regardless of success of Google Maps request
							// NOTE: Response is idempotent regardless of success of Google Maps request (null values)
							routingContext.response()
								.setStatusCode(200)
								.putHeader("content-type", "application/json; charset=utf-8")
								.end(Json.encodePrettily(response));
						});
					});
				} else {
					System.out.println(wr.cause().getMessage());
					routingContext.response().setStatusCode(404).end("Zip Code Not Found");
				}
			});
		})
		.failureHandler(routingContext->{
			routingContext.response().setStatusCode(400).end("Unable to process request");
		});

	// Start HTTP server
	HttpServer server = vertx.createHttpServer(
		new HttpServerOptions()
			.setPort(config().getInteger("http.port", 8080))
			.setHost(config().getString("http.host", "localhost")));
		server.requestHandler(router::accept).listen();
	}
}
