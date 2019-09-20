package es.codeurjc.openvidu.filters.client.websocket;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import es.codeurjc.openvidu.filters.client.App;
import es.codeurjc.openvidu.filters.client.rpc.RpcConstants;
import es.codeurjc.openvidu.filters.client.rpc.RpcEvent;
import es.codeurjc.openvidu.filters.client.rpc.RpcResponse;

/**
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
public class OpenViduWebSocket {

	private static final Logger log = LoggerFactory.getLogger(OpenViduWebSocket.class);

	public static WebSocket websocket;

	// RPC id that increments for each call to a method
	private AtomicInteger RPC_ID = new AtomicInteger(0);
	// RPC id of last "ping" call
	private AtomicInteger ID_PING = new AtomicInteger(-1);
	// RPC ids of "joinRoom" calls, paired with their sessionIds
	private Map<Integer, String> IDS_JOIN_ROOM = new ConcurrentHashMap<>();
	// RPC id of last "applyFilter" call
	private Map<Integer, CancellableCountDownLatch> IDS_APPLY_FILTER = new ConcurrentHashMap<>();
	// RPC id of last "removeFilter" call
	private Map<Integer, CancellableCountDownLatch> IDS_REMOVE_FILTER = new ConcurrentHashMap<>();
	// RPC id of last "execFilterMethod" call
	private Map<Integer, CancellableCountDownLatch> IDS_EXEC_FILTER_METHOD = new ConcurrentHashMap<>();
	// RPC id of last "addFilterEventListener" call
	private Map<Integer, CancellableCountDownLatch> IDS_ADD_FILTER_EVENT_LISTENER = new ConcurrentHashMap<>();
	// RPC id of last "removeFilterEventListener" call
	private Map<Integer, CancellableCountDownLatch> IDS_REMOVE_FILTER_EVENT_LISTENER = new ConcurrentHashMap<>();

	private Map<String, CancellableCountDownLatch> joinRoomLatches = new ConcurrentHashMap<>();

	private String connectedSession;

	public OpenViduWebSocket(String websocketUrl) throws Exception {
		try {
			WebSocketFactory factory = new WebSocketFactory();
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, acceptAllTrustManagers(), new java.security.SecureRandom());
			factory.setSSLContext(sslContext);
			factory.setVerifyHostname(false);
			websocket = factory.createSocket(websocketUrl);
			websocket.addListener(new OpenViduWebSocketListener(this));
			websocket.connect();
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException | WebSocketException e) {
			log.error("Error establishing websocket to {}: {}", websocketUrl, e.getMessage());
			throw e;
		}
	}

	void connectWebSocketToSession(String sessionId, String token) {
		Map<String, String> params = new HashMap<>();
		params.put("metadata", "");
		params.put("secret", App.OPENVIDU_SECRET);
		params.put("session", sessionId);
		params.put("platform", "SERVER");
		params.put("token", token);
		final int rpcId = this.sendMethod(RpcConstants.JOINROOM_METHOD, params);
		this.IDS_JOIN_ROOM.put(rpcId, sessionId);
	}

	public void applyFilter(String streamId, String type, String options) throws MyException {
		Map<String, String> params = new HashMap<>();
		params.put("streamId", streamId);
		params.put("type", type);
		params.put("options", options);
		final int rpcId = this.sendMethod(RpcConstants.APPLYFILTER_METHOD, params);

		CancellableCountDownLatch latch = new CancellableCountDownLatch(1);
		this.IDS_APPLY_FILTER.put(rpcId, latch);
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new MyException(RpcConstants.APPLYFILTER_METHOD, latch.getErrorMessage());
		}
	}

	public void removeFilter(String streamId) throws MyException {
		Map<String, String> params = new HashMap<>();
		params.put("streamId", streamId);
		final int rpcId = this.sendMethod(RpcConstants.REMOVEFILTER_METHOD, params);

		CancellableCountDownLatch latch = new CancellableCountDownLatch(1);
		this.IDS_REMOVE_FILTER.put(rpcId, latch);
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new MyException(RpcConstants.REMOVEFILTER_METHOD, latch.getErrorMessage());
		}
	}

	public void execFilterMethod(String streamId, String filterMethod, String filterMethodParams) throws MyException {
		Map<String, String> params = new HashMap<>();
		params.put("streamId", streamId);
		params.put("method", filterMethod);
		params.put("params", filterMethodParams);
		final int rpcId = this.sendMethod(RpcConstants.EXECFILTERMETHOD_METHOD, params);

		CancellableCountDownLatch latch = new CancellableCountDownLatch(1);
		this.IDS_EXEC_FILTER_METHOD.put(rpcId, latch);
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new MyException(RpcConstants.EXECFILTERMETHOD_METHOD, latch.getErrorMessage());
		}
	}

	public void addFilterEventListener(String streamId, String eventType) throws MyException {
		Map<String, String> params = new HashMap<>();
		params.put("streamId", streamId);
		params.put("eventType", eventType);
		final int rpcId = this.sendMethod(RpcConstants.ADDFILTEREVENTLISTENER_METHOD, params);

		CancellableCountDownLatch latch = new CancellableCountDownLatch(1);
		this.IDS_ADD_FILTER_EVENT_LISTENER.put(rpcId, latch);
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new MyException(RpcConstants.ADDFILTEREVENTLISTENER_METHOD, latch.getErrorMessage());
		}
	}

	public void removeFilterEventListener(String streamId, String eventType) throws MyException {
		Map<String, String> params = new HashMap<>();
		params.put("streamId", streamId);
		params.put("eventType", eventType);
		final int rpcId = this.sendMethod(RpcConstants.REMOVEFILTEREVENTLISTENER_METHOD, params);

		CancellableCountDownLatch latch = new CancellableCountDownLatch(1);
		this.IDS_REMOVE_FILTER_EVENT_LISTENER.put(rpcId, latch);
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new MyException(RpcConstants.REMOVEFILTEREVENTLISTENER_METHOD, latch.getErrorMessage());
		}
	}

	public synchronized int sendMethod(String method, Map<String, String> params) {
		final int id = RPC_ID.get();
		JSONObject jsonObject = new JSONObject();
		try {
			JSONObject paramsJson = new JSONObject();
			for (Map.Entry<String, String> param : params.entrySet()) {
				paramsJson.put(param.getKey(), param.getValue());
			}
			jsonObject.put(RpcConstants.JSON_RPC, RpcConstants.JSON_RPCVERSION);
			jsonObject.put(RpcConstants.METHOD_PROP_METHOD, method);
			jsonObject.put(RpcConstants.METHOD_PROP_PARAMS, paramsJson);
			jsonObject.put(RpcConstants.METHOD_PROP_ID, id);
		} catch (JSONException e) {
			log.error("JSONException raised on sendJson: {}", e.getMessage());
			return -1;
		}
		OpenViduWebSocket.websocket.sendText(jsonObject.toString());
		RPC_ID.incrementAndGet();
		return id;
	}

	public void handleServerResponse(RpcResponse response) {
		final int rpcId = response.getId();
		if (response.isSuccess() && response.getResultValue("value") != null
				&& response.getResultValue("value").equals("pong")) {
			// Response to "ping"
			log.debug("pong");

		} else if (this.IDS_JOIN_ROOM.containsKey(rpcId)) {
			// Response to "joinRoom". Now we are able to call any other RPC method
			final String sessionId = this.IDS_JOIN_ROOM.remove(rpcId);
			final CancellableCountDownLatch latch = joinRoomLatches.remove(sessionId);
			if (response.isSuccess()) {
				log.info("Joined to session {}", sessionId);
				latch.countDown();
			} else {
				latch.cancel(response.toStringError());
			}

		} else if (this.IDS_APPLY_FILTER.containsKey(rpcId)) {
			// Response to "applyFilter"
			final CancellableCountDownLatch latch = this.IDS_APPLY_FILTER.remove(rpcId);
			if (response.isSuccess()) {
				log.info("Filter applied successfully");
				latch.countDown();
			} else {
				latch.cancel(response.toStringError());
			}

		} else if (this.IDS_REMOVE_FILTER.containsKey(rpcId)) {
			// Response to "removeFilter"
			final CancellableCountDownLatch latch = this.IDS_REMOVE_FILTER.remove(rpcId);
			if (response.isSuccess()) {
				log.info("Filter removed successfully");
				latch.countDown();
			} else {
				latch.cancel(response.toStringError());
			}

		} else if (this.IDS_EXEC_FILTER_METHOD.containsKey(rpcId)) {
			// Response to "execFilterMethod"
			final CancellableCountDownLatch latch = this.IDS_EXEC_FILTER_METHOD.remove(rpcId);
			if (response.isSuccess()) {
				log.info("Filter method executed successfully");
				latch.countDown();
			} else {
				latch.cancel(response.toStringError());
			}

		} else if (this.IDS_ADD_FILTER_EVENT_LISTENER.containsKey(rpcId)) {
			// Response to "addFilterEventListener"
			final CancellableCountDownLatch latch = this.IDS_ADD_FILTER_EVENT_LISTENER.remove(rpcId);
			if (response.isSuccess()) {
				log.info("Filter event listener added successfully");
				latch.countDown();
			} else {
				latch.cancel(response.toStringError());
			}

		} else if (this.IDS_REMOVE_FILTER_EVENT_LISTENER.containsKey(rpcId)) {
			// Response to "removeFilterEventListener"
			final CancellableCountDownLatch latch = this.IDS_REMOVE_FILTER_EVENT_LISTENER.remove(rpcId);
			if (response.isSuccess()) {
				log.info("Filter event listener removed successfully");
				latch.countDown();
			} else {
				latch.cancel(response.toStringError());
			}

		} else {
			log.error("Unknown RPC id ({}). Response received: {}", rpcId, response.toString());
		}
	}

	public void handleServerEvent(RpcEvent event) {
		if (RpcConstants.FILTEREVENTDISPATCHED_EVENT.equals(event.getMethod())) {
			log.info("Filter event dispatched for filter \"{}\". Event: \"{}\". Data: {}]", event.getParam("filterType"),
					event.getParam("eventType"), event.getParam("data"));
		}
	}

	public void initPingPong() {
		long initialDelay = 0L;
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		executor.scheduleWithFixedDelay(() -> {
			Map<String, String> pingParams = new HashMap<>();
			if (ID_PING.get() == -1) {
				// First ping call
				pingParams.put("interval", "5000");
			}
			ID_PING.set(this.sendMethod(RpcConstants.PING_METHOD, pingParams));
		}, initialDelay, RpcConstants.PING_MESSAGE_INTERVAL, TimeUnit.SECONDS);
	}

	/*
	 * Connects the application's websocket to the required OpenVidu session,
	 * waiting for the response to "joinRoom" method to be returned. After calling
	 * this method, any other RPC method may be successfully called
	 */
	public void syncConnectToSession(String sessionId) throws MyException {
		if (sessionId.equals(connectedSession)) {
			log.info("Already connected to session {}", sessionId);
		} else {
			final CancellableCountDownLatch latch = new CancellableCountDownLatch(1);
			if (joinRoomLatches.putIfAbsent(sessionId, latch) == null) {
				// Construct the token with the secret (we'll be a superuser in this session)
				final String token = "wss://" + App.OPENVIDU_URL + ":4443?sessionId=" + sessionId + "&secret="
						+ App.OPENVIDU_SECRET;
				this.connectWebSocketToSession(sessionId, token);
				try {
					latch.await();
					connectedSession = sessionId;
				} catch (InterruptedException e) {
					throw new MyException(RpcConstants.JOINROOM_METHOD, latch.getErrorMessage());
				}
			}
		}
	}

	private TrustManager[] acceptAllTrustManagers() {
		return new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			@Override
			public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
			}

			@Override
			public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
			}
		} };
	}

}
