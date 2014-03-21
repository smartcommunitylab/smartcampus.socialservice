/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
