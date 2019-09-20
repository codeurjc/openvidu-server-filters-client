package es.codeurjc.openvidu.filters.client.websocket;

/**
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
public class MyException extends Exception {

	private static final long serialVersionUID = 1L;

	private String method;

	public MyException(String method, String message) {
		super(message);
		this.method = method;
	}

	@Override
	public String toString() {
		return method + ": " + this.getMessage();
	}

}
