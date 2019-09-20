package es.codeurjc.openvidu.filters.client.rpc;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * Represents an event sent from OpenVidu Server
 * (https://openvidu.io/docs/developing/rpc/#server-events)
 * 
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
public class RpcEvent {

	private String method;
	private Map<String, Object> params = new HashMap<>();
	private JSONObject originalJson;

	public RpcEvent(JSONObject json) {
		this.originalJson = json;
		this.method = json.getString("method");
		JSONObject paramsJson = json.getJSONObject("params");
		paramsJson.keySet().forEach(key -> {
			params.put(key, paramsJson.get(key));
		});
	}

	public String getMethod() {
		return method;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public Object getParam(String key) {
		return this.params.get(key);
	}

	@Override
	public String toString() {
		return this.originalJson.toString();
	}

}
