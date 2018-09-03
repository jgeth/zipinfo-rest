package zipinfo.client;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

/**
 * Represents a HTTP Client used for querying OpenWeatherMap APIs. Current implementation supports
 * requests to the following APIs:
 * - Current Weather API
 */
public class OpenWeatherMapClient {

	private final String BASE_REQUEST_URI = "/data/2.5/weather";
	private final String API_KEY;

	protected WebClient client;

	/**
	 * Default constructor for creating a HTTP client for interacting with Google Maps API
	 *
	 * @param vertx current Vertx instance used for creating a WebClient (HttpClient)
	 * @param apiKey OpenWeatherMap API key used for request authorization
	 * @param apiHost OpenWeatherMap API host used for making requests
	 * @param useSsl boolean indicating whether or not to use SSL in requests
	 */
	public OpenWeatherMapClient(Vertx vertx, String apiKey, String apiHost, boolean useSsl) {
		this.API_KEY = apiKey;
		this.client = WebClient.create(vertx, new WebClientOptions()
			.setDefaultHost(apiHost)
			.setDefaultPort(useSsl ? 443 : 80)
			.setSsl(useSsl));
	}

	/**
	 * Make an asynchronous HTTP request to the OpenWeatherMap Current Weather API
	 *
	 * @param coord JSON object representing geolocation coordinates
	 * @param handler Asynchronous callback handler of containing response of type Integer
	 */
	public void getCurrentWeatherByZip(String zipCode, Handler<AsyncResult<JsonObject>> handler) {

		// Construct request URI
		String requestUri = BASE_REQUEST_URI + "?zip=" + zipCode + "&appid=" + API_KEY;

		// Make HTTP GET request
		this.client.get(requestUri).send(ar -> {

			// HTTP request was successful
			if (ar.succeeded()) {

				// Parse JSON response into a JsonObject
				JsonObject data = ar.result().bodyAsJsonObject();
				System.out.println(data);

				// Successful query results in an API result "code" of 200
				if (data.getInteger("cod") == 200) {
					handler.handle(Future.succeededFuture(new JsonObject()
						.put("coord", data.getJsonObject("coord"))
						.put("temp", data.getJsonObject("main").getDouble("temp"))
						.put("city", data.getString("name"))));
				} else {
					// Unsuccessful query (possible invalid zipcode)
					handler.handle(Future.failedFuture(data.getString("message")));
				}
			} else {
				// HTTP request failed
				handler.handle(Future.failedFuture(ar.cause()));
			}
		});
	}
}
