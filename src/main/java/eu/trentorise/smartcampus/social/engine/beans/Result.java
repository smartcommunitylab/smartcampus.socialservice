package eu.trentorise.smartcampus.social.engine.beans;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class Result {
	private String data;
	private String errorCode;
	private String errorMessage;
	
	public Result(String data) {
		super();
		this.data = data;
	}
	
	public Result(Object toJson) throws JsonGenerationException, JsonMappingException, IOException{
		super();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		this.data = ow.writeValueAsString(toJson);
	}
	
	public String getData() {
		return data;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setData(String data) {
		this.data = data;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	
}
