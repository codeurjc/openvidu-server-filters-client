package es.codeurjc.openvidu.filters.client.rpc;

/**
 * Constants for OpenVidu Serve RPC WebSocket protocol
 * (https://openvidu.io/docs/developing/rpc/)
 * 
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
public class RpcConstants {

	// RPC outgoing methods
	public static final String APPLYFILTER_METHOD = "applyFilter";
	public static final String REMOVEFILTER_METHOD = "removeFilter";
	public static final String EXECFILTERMETHOD_METHOD = "execFilterMethod";
	public static final String ADDFILTEREVENTLISTENER_METHOD = "addFilterEventListener";
	public static final String REMOVEFILTEREVENTLISTENER_METHOD = "removeFilterEventListener";
	public static final String PING_METHOD = "ping";
	public static final String JOINROOM_METHOD = "joinRoom";

	// RPC outgoing method properties
	public static final String METHOD_PROP_METHOD = "method";
	public static final String METHOD_PROP_PARAMS = "params";
	public static final String METHOD_PROP_ID = "id";

	// RPC incoming response properties
	public static final String RESPONSE_PROP_ID = "id";
	public static final String RESPONSE_PROP_RESULT = "result";
	public static final String RESPONSE_PROP_ERROR = "error";

	// RPC incoming response error properties
	public static final String RESPONSE_ERROR_PROP_CODE = "code";
	public static final String RESPONSE_ERROR_PROP_MESSAGE = "message";

	// RPC incoming events
	public static final String FILTEREVENTDISPATCHED_EVENT = "filterEventDispatched";

	// RPC incoming event properties
	public static final String EVENT_PROP_METHOD = "method";
	public static final String EVENT_PROP_PARAMS = "params";

	// Constant values
	public static final String JSON_RPC = "jsonrpc";
	public static final String JSON_RPCVERSION = "2.0";
	public static final int PING_MESSAGE_INTERVAL = 5;

}
