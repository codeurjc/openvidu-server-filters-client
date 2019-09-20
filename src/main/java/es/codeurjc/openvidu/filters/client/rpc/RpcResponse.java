package es.codeurjc.openvidu.filters.client.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.json.JSONObject;

/**
 * Represents a response from OpenVidu Server to a method call from this
 * application (https://openvidu.io/docs/developing/rpc/#client-server-methods)
 * 
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
public class RpcResponse {

	private int id;
	private Map<String, Object> result;
	private Integer errorCode;
	private String errorMessage;
	private JSONObject originalJson;

	public RpcResponse(JSONObject json) {
		this.originalJson = json;
		this.id = json.getInt(RpcConstants.RESPONSE_PROP_ID);
		if (json.has(RpcConstants.RESPONSE_PROP_RESULT)) {
			// Success response from openvidu-server
			result = new HashMap<>();
			JSONObject resultJson = json.getJSONObject(RpcConstants.RESPONSE_PROP_RESULT);
			resultJson.keySet().forEach(key -> {
				result.put(key, resultJson.get(key));
			});
		} else if (json.has(RpcConstants.RESPONSE_PROP_ERROR)) {
			// Error response from openvidu-server
			JSONObject errorJson = json.getJSONObject(RpcConstants.RESPONSE_PROP_ERROR);
			errorCode = errorJson.getInt(RpcConstants.RESPONSE_ERROR_PROP_CODE);
			errorMessage = errorJson.getString(RpcConstants.RESPONSE_ERROR_PROP_MESSAGE);
		}
	}

	public int getId() {
		return id;
	}

	public Map<String, Object> getResult() {
		return result;
	}

	public Object getResultValue(String key) throws NoSuchElementException {
		if (result == null) {
			throw new NoSuchElementException("Response is an error");
		}
		return result.get(key);
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean isSuccess() {
		return this.result != null;
	}

	public String toStringError() {
		if (this.isSuccess()) {
			return null;
		} else {
			return this.errorMessage;
		}
	}

	@Override
	public String toString() {
		return this.originalJson.toString();
	}

}
