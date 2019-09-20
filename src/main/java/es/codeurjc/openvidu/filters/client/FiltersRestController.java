package es.codeurjc.openvidu.filters.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.openvidu.filters.client.websocket.MyException;
import es.codeurjc.openvidu.filters.client.websocket.OpenViduWebSocket;

/**
 * REST API to invoke RPC methods related to OpenVidu filters. Every REST method
 * will trigger the sending of a JSON-RPC message through the application's
 * websocket to OpenVidu Server
 * 
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
@RestController
@CrossOrigin
public class FiltersRestController {

	private static final Logger log = LoggerFactory.getLogger(FiltersRestController.class);

	@Autowired
	private OpenViduWebSocket websocket;

	/**
	 * Example of body request
	 * 
	 * { "sessionId": "MySession", "streamId": "exyoujh3l7wlhrru_CAMERA_SVTZP",
	 * "type": "GStreamerFilter", "options": "{\"command\": \"videobalance
	 * saturation=0.0\"}" }
	 * 
	 */
	@RequestMapping(value = "/apply-filter", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> applyFilter(@RequestBody(required = true) Map<?, ?> params) {

		if (params == null) {
			return new ResponseEntity<>("Body cannot be empty", HttpStatus.BAD_REQUEST);
		}

		log.info("REST API: POST /apply-filter {}", params.toString());

		String sessionId;
		String streamId;
		String type;
		String options;

		try {
			sessionId = (String) params.get("sessionId");
			streamId = (String) params.get("streamId");
			type = (String) params.get("type");
			options = (String) params.get("options");
		} catch (ClassCastException | NullPointerException e) {
			String errorMessage = "Error on some parameter: " + e.getMessage();
			log.error(errorMessage);
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		}

		try {
			websocket.syncConnectToSession(sessionId);
		} catch (MyException e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			websocket.applyFilter(streamId, type, options);
		} catch (MyException e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Example of body request
	 * 
	 * { "sessionId": "MySession", "streamId": "exyoujh3l7wlhrru_CAMERA_SVTZP" }
	 * 
	 */
	@RequestMapping("/remove-filter")
	public ResponseEntity<?> removeFilter(@RequestBody(required = true) Map<?, ?> params) {

		if (params == null) {
			return new ResponseEntity<>("Body cannot be empty", HttpStatus.BAD_REQUEST);
		}

		log.info("REST API: POST /remove-filter {}", params.toString());

		String sessionId;
		String streamId;

		try {
			sessionId = (String) params.get("sessionId");
			streamId = (String) params.get("streamId");
		} catch (ClassCastException | NullPointerException e) {
			String errorMessage = "Error on some parameter: " + e.getMessage();
			log.error(errorMessage);
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		}

		try {
			websocket.syncConnectToSession(sessionId);
		} catch (MyException e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			websocket.removeFilter(streamId);
		} catch (MyException e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Example of body request
	 * 
	 * { "sessionId": "MySession", "streamId": "exyoujh3l7wlhrru_CAMERA_SVTZP",
	 * "method": "setElementProperty", "params":
	 * "{\"propertyName\":\"saturation\",\"propertyValue\":\"1.0\"}" }
	 * 
	 */
	@RequestMapping("/exec-filter-method")
	public ResponseEntity<?> execFilterMethod(@RequestBody(required = true) Map<?, ?> params) {

		if (params == null) {
			return new ResponseEntity<>("Body cannot be empty", HttpStatus.BAD_REQUEST);
		}

		log.info("REST API: POST /exec-filter-method {}", params.toString());

		String sessionId;
		String streamId;
		String filterMethod;
		String filterParams;

		try {
			sessionId = (String) params.get("sessionId");
			streamId = (String) params.get("streamId");
			filterMethod = (String) params.get("method");
			filterParams = (String) params.get("params");
		} catch (ClassCastException | NullPointerException e) {
			String errorMessage = "Error on some parameter: " + e.getMessage();
			log.error(errorMessage);
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		}

		try {
			websocket.syncConnectToSession(sessionId);
		} catch (MyException e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			websocket.execFilterMethod(streamId, filterMethod, filterParams);
		} catch (MyException e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Example of body request
	 * 
	 * { "sessionId": "MySession", "streamId": "exyoujh3l7wlhrru_CAMERA_SVTZP",
	 * "eventType": "CodeFound" }
	 * 
	 */
	@RequestMapping("/add-filter-event-listener")
	public ResponseEntity<?> addFilterEventListener(@RequestBody(required = true) Map<?, ?> params) {

		if (params == null) {
			return new ResponseEntity<>("Body cannot be empty", HttpStatus.BAD_REQUEST);
		}

		log.info("REST API: POST /add-filter-event-listener {}", params.toString());

		String sessionId;
		String streamId;
		String eventType;

		try {
			sessionId = (String) params.get("sessionId");
			streamId = (String) params.get("streamId");
			eventType = (String) params.get("eventType");
		} catch (ClassCastException | NullPointerException e) {
			String errorMessage = "Error on some parameter: " + e.getMessage();
			log.error(errorMessage);
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		}

		try {
			websocket.syncConnectToSession(sessionId);
		} catch (MyException e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			websocket.addFilterEventListener(streamId, eventType);
		} catch (MyException e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping("/remove-filter-event-listener")
	public ResponseEntity<?> removeFilterEventListener(@RequestBody(required = true) Map<?, ?> params) {

		if (params == null) {
			return new ResponseEntity<>("Body cannot be empty", HttpStatus.BAD_REQUEST);
		}

		log.info("REST API: POST /remove-filter-event-listener {}", params.toString());

		String sessionId;
		String streamId;
		String eventType;

		try {
			sessionId = (String) params.get("sessionId");
			streamId = (String) params.get("streamId");
			eventType = (String) params.get("eventType");
		} catch (ClassCastException | NullPointerException e) {
			String errorMessage = "Error on some parameter: " + e.getMessage();
			log.error(errorMessage);
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		}

		try {
			websocket.syncConnectToSession(sessionId);
		} catch (MyException e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			websocket.removeFilterEventListener(streamId, eventType);
		} catch (MyException e) {
			log.error(e.toString());
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
