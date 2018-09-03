package zipinfo.client;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.time.Instant;

/**
 * Represents a HTTP Client used for querying Google Maps APIs. Current implementation supports
 * requests to the following APIs:
 * - Elevation API
 * - Timezone API
 */
public class GoogleMapsClient {

	private final String BASE_REQUEST_URI = "/maps/api";
	private final String RESPONSE_FORMAT = "json";
	private final String BASE_ELEVATION_REQUEST_URI = BASE_REQUEST_URI + "/elevation/" + RESPONSE_FORMAT;
	private final String BASE_TIMEZONE_REQUEST_URI = BASE_REQUEST_URI + "/timezone/" + RESPONSE_FORMAT;
	private final String API_KEY;

	protected WebClient client;

	/**
	 * Default constructor for creating a HTTP client for interacting with Google Maps API
	 *
	 * @param vertx current Vertx instance used for creating a WebClient (HttpClient)
	 * @param apiKey Google API key used for request authorization
	 * @param apiHost Google API host used for making requests
	 * @param useSsl boolean indicating whether or not to use SSL in requests
	 */
	public GoogleMapsClient(Vertx vertx, String apiKey, String apiHost, boolean useSsl) {
		this.API_KEY = apiKey;
		this.client = WebClient.create(vertx, new WebClientOptions()
			.setDefaultHost(apiHost)
			.setDefaultPort(useSsl ? 443 : 80)
			.setSsl(useSsl));
	}

	/**
	 * Make an asynchronous HTTP request to the Google Maps Elevation API
	 *
	 * @param coord JSON object representing geolocation coordinates
	 * @param handler Asynchronous callback handler of containing response of type Integer
	 */
	public void getElevation(JsonObject coord, Handler<AsyncResult<Integer>> handler) {

		// Construct request URI
		String requestUri = BASE_ELEVATION_REQUEST_URI + "?locations="
			+ coord.getDouble("lat").toString() + "," + coord.getDouble("lon").toString()
			+ "&key=" + API_KEY;

		// Make HTTP GET request
		this.client.get(requestUri).send(ar -> {
			if (ar.succeeded()) {
				JsonObject data = ar.result().bodyAsJsonObject();
				handler.handle(Future.succeededFuture(Math.round(data.getJsonArray("results")
					.getJsonObject(0)
					.getLong("elevation"))));
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});
	}

	/**
	 * Make an asynchronous HTTP request to the Google Maps Timezone API
	 *
	 * @param coord JSON object representing geolocation coordinates
	 * @param handler Asynchronous callback handler of containing response of type String
	 */
	public void getTimeZone(JsonObject coord, Handler<AsyncResult<String>> handler) {

		// Construct request URI
		String requestUri = BASE_TIMEZONE_REQUEST_URI + "?location="
			+ coord.getDouble("lat").toString() + "," + coord.getDouble("lon").toString()
			+ "&timestamp=" + Math.round(Instant.now().toEpochMilli()/1000)
			+ "&key=" + API_KEY;

		// Make HTTP GET request
		this.client.get(requestUri).send(ar -> {
			if (ar.succeeded()) {
				JsonObject data = ar.result().bodyAsJsonObject();
				handler.handle(Future.succeededFuture(data.getString("timeZoneName")));
			} else {
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});
	}
}
