package es.codeurjc.openvidu.filters.client;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import es.codeurjc.openvidu.filters.client.websocket.OpenViduWebSocket;

/**
 * Run this application with the following command
 * 
 * java -jar -DOPENVIDU_URL=https://your-url.com -DOPENVIDU_SECRET=your-secret openvidu-filters-client.jar
 * 
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
@SpringBootApplication
public class App {

	private static final Logger log = LoggerFactory.getLogger(App.class);

	private static String openviduUrl = "https://localhost:4443/";
	public static String websocketUrl;
	public static String OPENVIDU_SECRET = "MY_SECRET";
	public static URL OPENVIDU_URL;

	@Bean
	public OpenViduWebSocket websocket() {
		try {
			return new OpenViduWebSocket(websocketUrl);
		} catch (Exception e) {
			log.error("Shutting down application");
			System.exit(1);
			return null;
		}
	}

	public static void main(String[] args) {
		String httpUrl = System.getProperty("OPENVIDU_URL");
		if (httpUrl != null) {
			openviduUrl = httpUrl;
		}
		log.info("OpenVidu URL set to {}", openviduUrl);
		try {
			OPENVIDU_URL = new URL(openviduUrl);
			websocketUrl = "wss://" + OPENVIDU_URL.getHost() + ":" + OPENVIDU_URL.getPort() + "/openvidu";
		} catch (MalformedURLException e) {
			log.error("Parameter OPENVIDU_URL ({}) has not a valid URL format");
			System.exit(1);
		}
		log.info("OpenVidu WebSocket endpoint is {}", websocketUrl);
		String openviduSecret = System.getProperty("OPENVIDU_SECRET");
		if (openviduSecret != null) {
			OPENVIDU_SECRET = openviduSecret;
		}
		log.info("OpenVidu secret set to {}", OPENVIDU_SECRET);
		SpringApplication.run(App.class, args);
	}

}
