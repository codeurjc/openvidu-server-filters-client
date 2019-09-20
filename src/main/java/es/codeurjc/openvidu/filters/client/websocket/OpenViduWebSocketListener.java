package es.codeurjc.openvidu.filters.client.websocket;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

import es.codeurjc.openvidu.filters.client.rpc.RpcConstants;
import es.codeurjc.openvidu.filters.client.rpc.RpcEvent;
import es.codeurjc.openvidu.filters.client.rpc.RpcResponse;

/**
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
public class OpenViduWebSocketListener implements WebSocketListener {

	private static final Logger log = LoggerFactory.getLogger(OpenViduWebSocketListener.class);

	private OpenViduWebSocket ws;

	public OpenViduWebSocketListener(OpenViduWebSocket ws) {
		this.ws = ws;
	}

	@Override
	public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
		log.info("WebSocket connected");
		// Init ping pong mechanism
		this.ws.initPingPong();
	}

	@Override
	public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
			boolean closedByServer) throws Exception {
		log.info("WebSocket disconnected");
	}

	@Override
	public void onTextMessage(WebSocket websocket, String text) throws Exception {
		log.debug("Message received from OpenVidu Server: {}", text);
		JSONObject json = new JSONObject(text);
		if (json.has(RpcConstants.RESPONSE_PROP_ID)) {
			// Message is a server response to a method call initiated from this application
			this.ws.handleServerResponse(new RpcResponse(json));
		} else {
			// Message is a server event
			this.ws.handleServerEvent(new RpcEvent(json));
		}
	}

	/** UNIMPLEMENTED INHERITED METHODS **/

	@Override
	public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextMessage(WebSocket websocket, byte[] data) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {
		// TODO Auto-generated method stub

	}

}
