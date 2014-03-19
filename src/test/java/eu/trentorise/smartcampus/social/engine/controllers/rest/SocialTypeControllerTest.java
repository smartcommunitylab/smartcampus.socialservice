package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import eu.trentorise.smartcampus.social.managers.SocialTypeManager;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml",
		"classpath:/spring/spring-security.xml" })
public class SocialTypeControllerTest extends SCControllerTest {

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private WebApplicationContext wac;
	
	@Autowired
	private SocialTypeManager typeManager;

	private MockMvc mockMvc;

	private static final String NEWTYPE_1 = "image1";
	private static final String NEWTYPE_2 = "audio1";
	private static final String NEWTYPE_3 = "video1";
	private static final String NEWTYPE_4 = "text1";
	private static final String NEWTYPE_5 = "image2";
	private static final String NEWTYPE_6 = "audio2";
	private static final String NEWTYPE_7 = "video2";
	private static final String NEWTYPE_8 = "text2";

	private static final String MIME_TYPE_1 = "image/png";
	private static final String MIME_TYPE_2 = "audio/mp3";
	private static final String MIME_TYPE_3 = "video/mp4";
	private static final String MIME_TYPE_4 = "text/txt";
	private static final String MIME_TYPE_5 = "image/jpg";
	private static final String MIME_TYPE_6 = "audio/wav";
	private static final String MIME_TYPE_7 = "video/avi";
	private static final String MIME_TYPE_8 = "text/rtf";
	private static final String MIME_TYPE_ERR = "audio/mpx";

	private static final String NEXT_TYPE_ID = "123456";
	private String TYPE_ID_1 = "1";
	private String TYPE_ID_2 = "2";
	private String TYPE_ID_3 = "3";
	private String TYPE_ID_4 = "4";
	private String TYPE_ID_5 = "5";
	private String TYPE_ID_6 = "6";
	private String TYPE_ID_7 = "7";
	private String TYPE_ID_8 = "8";
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(
				springSecurityFilterChain).build();
		setTypes();
	}
	
	public void setTypes() throws Exception {
		RequestBuilder request = setDefaultRequest(post("/app/type"),
				Scope.USER).param("name", NEWTYPE_1).param("mimeType",
				MIME_TYPE_1);
		ResultActions response = mockMvc.perform(request);
		MvcResult result = setDefaultResult(response).andExpect(
				content().string(containsString(NEWTYPE_1))).andReturn();
		String content = result.getResponse().getContentAsString();
		TYPE_ID_1 = extractIdFromResult(content);

		request = setDefaultRequest(post("/app/type"), Scope.USER).param(
				"name", NEWTYPE_2).param("mimeType", MIME_TYPE_2);
		response = mockMvc.perform(request);
		result = setDefaultResult(response).andExpect(
				content().string(containsString(NEWTYPE_2))).andReturn();
		content = result.getResponse().getContentAsString();
		TYPE_ID_2 = extractIdFromResult(content);

		request = setDefaultRequest(post("/app/type"), Scope.USER).param(
				"name", NEWTYPE_3).param("mimeType", MIME_TYPE_3);
		response = mockMvc.perform(request);
		result = setDefaultResult(response).andExpect(
				content().string(containsString(NEWTYPE_3))).andReturn();
		content = result.getResponse().getContentAsString();
		TYPE_ID_3 = extractIdFromResult(content);

		request = setDefaultRequest(post("/app/type"), Scope.USER).param(
				"name", NEWTYPE_4).param("mimeType", MIME_TYPE_4);
		response = mockMvc.perform(request);
		result = setDefaultResult(response).andExpect(
				content().string(containsString(NEWTYPE_4))).andReturn();
		content = result.getResponse().getContentAsString();
		TYPE_ID_4 = extractIdFromResult(content);

		request = setDefaultRequest(post("/app/type"), Scope.USER).param(
				"name", NEWTYPE_5).param("mimeType", MIME_TYPE_5);
		response = mockMvc.perform(request);
		result = setDefaultResult(response).andExpect(
				content().string(containsString(NEWTYPE_5))).andReturn();
		content = result.getResponse().getContentAsString();
		TYPE_ID_5 = extractIdFromResult(content);

		request = setDefaultRequest(post("/app/type"), Scope.USER).param(
				"name", NEWTYPE_6).param("mimeType", MIME_TYPE_6);
		response = mockMvc.perform(request);
		result = setDefaultResult(response).andExpect(
				content().string(containsString(NEWTYPE_6))).andReturn();
		content = result.getResponse().getContentAsString();
		TYPE_ID_6 = extractIdFromResult(content);

		request = setDefaultRequest(post("/app/type"), Scope.USER).param(
				"name", NEWTYPE_7).param("mimeType", MIME_TYPE_7);
		response = mockMvc.perform(request);
		result = setDefaultResult(response).andExpect(
				content().string(containsString(NEWTYPE_7))).andReturn();
		content = result.getResponse().getContentAsString();
		TYPE_ID_7 = extractIdFromResult(content);

		request = setDefaultRequest(post("/app/type"), Scope.USER).param(
				"name", NEWTYPE_8).param("mimeType", MIME_TYPE_8);
		response = mockMvc.perform(request);
		result = setDefaultResult(response).andExpect(
				content().string(containsString(NEWTYPE_8))).andReturn();
		content = result.getResponse().getContentAsString();
		TYPE_ID_8 = extractIdFromResult(content);
	}

	@Test
	public void getTypesVoidDB() throws Exception {
		removeTypes();
		RequestBuilder request = setDefaultRequest(get("/user/type"),
				Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data", Matchers.hasSize(0))); // empty list
	}

	@Test
	public void setTypesSpecialCases() throws Exception {
		
		// try to create an already exist type
		RequestBuilder request = setDefaultRequest(post("/app/type"),
				Scope.USER).param("name", NEWTYPE_1).param("mimeType",
				MIME_TYPE_1);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString(NEWTYPE_1)));

		// try to set a new type with null name
		String typeName = null;
		request = setDefaultRequest(post("/app/type"), Scope.USER).param(
				"name", typeName).param("mimeType", MIME_TYPE_1);
		response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);

		// try to set a new type with null mimeType
		String mimeTypeName = null;
		request = setDefaultRequest(post("/app/type"), Scope.USER).param(
				"name", NEWTYPE_1).param("mimeType", mimeTypeName);
		response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);

		// try to set a new type with mimeType not allowed
		String errCode = "400";
		request = setDefaultRequest(post("/app/type"), Scope.USER).param(
				"name", NEWTYPE_1).param("mimeType", MIME_TYPE_ERR);
		response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response).andExpect(
				content().string(containsString(errCode)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getTypes() throws Exception {
		// with limit
		RequestBuilder request = setDefaultRequest(get("/user/type"),
				Scope.USER).param("pageNum", "1").param("pageSize", "5");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(
						allOf(containsString(NEWTYPE_6),
								containsString(NEWTYPE_7),
								containsString(NEWTYPE_8))));

		// by mimeType
		String my_newType = "My_" + NEWTYPE_1;
		request = setDefaultRequest(post("/app/type"), Scope.USER).param(
				"name", my_newType).param("mimeType", MIME_TYPE_1);
		response = mockMvc.perform(request);
		MvcResult result = setDefaultResult(response).andExpect(
				content().string(containsString(my_newType))).andReturn();
		String content = result.getResponse().getContentAsString();
		String typeID_9 = extractIdFromResult(content);

		request = setDefaultRequest(get("/user/type"), Scope.USER).param(
				"mimeType", MIME_TYPE_1);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString(my_newType)));

		// with limit and sort
		request = setDefaultRequest(get("/user/type"), Scope.USER)
				.param("pageNum", "0").param("pageSize", "5")
				.param("sortDirection", "1").param("sortList", "name");
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(
						allOf(containsString(NEWTYPE_7),
								containsString(NEWTYPE_3),
								containsString(NEWTYPE_8),
								containsString(NEWTYPE_4),
								containsString(my_newType))));

		request = setDefaultRequest(get("/user/type"), Scope.USER)
				.param("pageNum", "1").param("pageSize", "5")
				.param("sortDirection", "0").param("sortList", "name")
				.param("sortList", "mimeType");
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(
						allOf(containsString(NEWTYPE_4),
								containsString(NEWTYPE_8),
								containsString(NEWTYPE_3),
								containsString(NEWTYPE_7))));

		request = setDefaultRequest(get("/user/type"), Scope.USER)
				.param("pageNum", "0").param("pageSize", "10")
				.param("sortDirection", "1").param("sortList", "id");
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(
						allOf(containsString(NEWTYPE_8),
								containsString(NEWTYPE_7),
								containsString(NEWTYPE_6),
								containsString(NEWTYPE_5),
								containsString(NEWTYPE_4),
								containsString(NEWTYPE_3),
								containsString(NEWTYPE_2),
								containsString(NEWTYPE_1))));

		// with limit and sort err parameter
		request = setDefaultRequest(get("/user/type"), Scope.USER)
				.param("pageNum", "0").param("pageSize", "5")
				.param("sortDirection", "1").param("sortList", "nameMime");
		response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
		
		// remove the new type9 created
		typeManager.deleteType(typeID_9);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getSingleType() throws Exception {
		String typeId;
		RequestBuilder request = setDefaultRequest(
				get("/user/type/{typeId}", TYPE_ID_3), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString(NEWTYPE_3)));
		
		request = setDefaultRequest(
				get("/user/type/{typeId}", TYPE_ID_6), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString(NEWTYPE_6)));

		// null parameters
		typeId = null;
		request = setDefaultRequest(get("/user/type/{typeId}", typeId),
				Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(
						allOf(containsString(NEWTYPE_1),
								containsString(NEWTYPE_2),
								containsString(NEWTYPE_3),
								containsString(NEWTYPE_4),
								containsString(NEWTYPE_5),
								containsString(NEWTYPE_6),
								containsString(NEWTYPE_7),
								containsString(NEWTYPE_8))));

		// type not exists
		request = setDefaultRequest(get("/user/type/{typeId}", NEXT_TYPE_ID),
				Scope.USER);
		response = mockMvc.perform(request);
		setNullResult(response);
	}
	
	@After
	public void removeTypes() throws Exception {
		typeManager.deleteType(TYPE_ID_1);
		typeManager.deleteType(TYPE_ID_2);
		typeManager.deleteType(TYPE_ID_3);
		typeManager.deleteType(TYPE_ID_4);
		typeManager.deleteType(TYPE_ID_5);
		typeManager.deleteType(TYPE_ID_6);
		typeManager.deleteType(TYPE_ID_7);
		typeManager.deleteType(TYPE_ID_8);
	}

}
