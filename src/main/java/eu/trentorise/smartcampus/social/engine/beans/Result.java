package eu.trentorise.smartcampus.social.engine.beans;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class Result {
	private Object data;
	private String errorCode;
	private String errorMessage;

	public Result() {

	}

	public Result(String data) {
		super();
		this.data = data;
	}

	public Result(Exception ex, int errCode) {
		super();
		this.errorMessage = ex.getMessage();
		this.setErrorCode(String.valueOf(errCode));
	}

	public Result(Object toJson) {
		super();
		this.data = toJson;
	}

	public Object getData() {
		return data;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public static String resultToJsonString(Result result) throws Exception {
		ObjectWriter ow = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		return ow.writeValueAsString(result);
	}

}
