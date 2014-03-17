package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class SCControllerTest {

	public static enum Scope {
		USER, CLIENT
	};

	private static final String USER_AUTH_TOKEN = "";
	private static final String CLIENT_AUTH_TOKEN = "";

	/** Controller utilities **/
	private static final String RH_AUTH_TOKEN = "Authorization";

	private static final String CHAR_ENCODING = "UTF-8";
	private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

	private ObjectMapper mapper;

	public SCControllerTest() {
		mapper = new ObjectMapper();
	}

	protected static String bearer(String token) {
		return "Bearer " + token;
	}

	protected byte[] convertObjectToJsonBytes(Object object) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Inclusion.NON_NULL); // JsonInclude.Include.NON_NULL
		return mapper.writeValueAsBytes(object);
	}

	protected String convertObjectToJsonString(Object object)
			throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter jsonOut = mapper.writer().withDefaultPrettyPrinter();
		return jsonOut.writeValueAsString(object);
	}

	protected <T> T convertJsonToObject(String json, Class<T> classType) {
		try {
			if (json != null && classType != null) {
				return mapper.readValue(json, classType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected MockHttpServletRequestBuilder setDefaultRequest(
			MockHttpServletRequestBuilder request, Scope scope) {
		String token = null;
		if (scope == Scope.USER) {
			token = bearer(USER_AUTH_TOKEN);
		} else {
			token = bearer(CLIENT_AUTH_TOKEN);
		}
		return request.header(RH_AUTH_TOKEN, token)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(CHAR_ENCODING);
	}

	protected ResultActions setDefaultResult(ResultActions result)
			throws Exception {
		return result.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(CONTENT_TYPE));
	}
	
	protected ResultActions setDefaultResult_NoPrint(ResultActions result)
			throws Exception {
		return result.andExpect(status().isOk())
				.andExpect(content().contentType(CONTENT_TYPE));
	}

	protected ResultActions setIllegalArgumentExceptionResult(
			ResultActions result) throws Exception {
		return result.andDo(print()).andExpect(status().isBadRequest());
	}

	protected ResultActions setNullResult(ResultActions result)
			throws Exception {
		return result.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("null"));
	}
	
	protected ResultActions setVoidResult(ResultActions result)
			throws Exception {
		return result.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("[]"));
	}

	protected ResultActions setForbiddenExceptionResult(ResultActions result)
			throws Exception {
		return result.andDo(print()).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.error").value("access_denied"));
	}

	protected ResultActions setMethodNotSupportedResult(ResultActions result)
			throws Exception {
		return result
				.andDo(print())
				.andExpect(status().isMethodNotAllowed())
				.andExpect(
						jsonPath("$.errorCode").value(
								HttpStatus.METHOD_NOT_ALLOWED.toString()));
	}

}
