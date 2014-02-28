package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:/spring/applicationContext.xml", "classpath:/spring/spring-security.xml"})
public class SocialTypeControllerTest extends SCControllerTest{
	
	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private WebApplicationContext wac;
	
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
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
	}
	
	@Test
	public void test1_getTypes() throws Exception {  
		RequestBuilder request = setDefaultRequest(get("/user/type"), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(jsonPath("$.data").value("[ ]"));	// void list
	}
	
	@Test
	public void test2_setType() throws Exception {  
		RequestBuilder request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", NEWTYPE_1).param("mimeType", MIME_TYPE_1);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWTYPE_1)));
		
		request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", NEWTYPE_2).param("mimeType", MIME_TYPE_2);
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWTYPE_2)));

		request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", NEWTYPE_3).param("mimeType", MIME_TYPE_3);
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWTYPE_3)));
		
		request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", NEWTYPE_4).param("mimeType", MIME_TYPE_4);
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWTYPE_4)));

		request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", NEWTYPE_5).param("mimeType", MIME_TYPE_5);
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWTYPE_5)));
		
		request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", NEWTYPE_6).param("mimeType", MIME_TYPE_6);
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWTYPE_6)));

		request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", NEWTYPE_7).param("mimeType", MIME_TYPE_7);
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWTYPE_7)));
		
		request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", NEWTYPE_8).param("mimeType", MIME_TYPE_8);
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWTYPE_8)));

		//Try to create an already exist type
		request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", NEWTYPE_1).param("mimeType", MIME_TYPE_1);
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWTYPE_1)));
		
	}
	
	@Test
	public void test21_setTypeNullName() throws Exception { 
		String typeName = null;
		RequestBuilder request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", typeName).param("mimeType", MIME_TYPE_1);
		ResultActions response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
	}
	
	@Test
	public void test22_setTypeNullMimeType() throws Exception {  
		String mimeTypeName = null;
		RequestBuilder request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", NEWTYPE_1).param("mimeType", mimeTypeName);
		ResultActions response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
	}
	
	@Test
	public void test23_setTypeNotAllowedMimeType() throws Exception {
		String errCode = "400";
		RequestBuilder request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", NEWTYPE_1).param("mimeType", MIME_TYPE_ERR);
		ResultActions response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response).andExpect(content().string(containsString(errCode)));
	}
	
	@Test
	public void test3_getTypesLimit() throws Exception {  
		RequestBuilder request = setDefaultRequest(get("/user/type"), Scope.USER).param("pageNum", "1").param("pageSize", "5");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(NEWTYPE_6), containsString(NEWTYPE_7), containsString(NEWTYPE_8))));
	}
	
	@Test
	public void test31_getTypesByMimeType() throws Exception {
		String my_newType = "My_"+NEWTYPE_1;
		RequestBuilder request = setDefaultRequest(post("/app/type"), Scope.USER).param("name", my_newType).param("mimeType", MIME_TYPE_1);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(my_newType)));
		
		request = setDefaultRequest(get("/user/type"), Scope.USER).param("mimeType", MIME_TYPE_1);
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(my_newType)));
	}
	
	@Test
	public void test4_getType() throws Exception { 
		String typeId = "3";
		RequestBuilder request = setDefaultRequest(get("/user/type/{typeId}", typeId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWTYPE_3)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test41_getTypeNullParam() throws Exception { 
		String typeId = null;
		RequestBuilder request = setDefaultRequest(get("/user/type/{typeId}", typeId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(NEWTYPE_1), containsString(NEWTYPE_2), containsString(NEWTYPE_3), containsString(NEWTYPE_4), containsString(NEWTYPE_5), containsString(NEWTYPE_6), containsString(NEWTYPE_7), containsString(NEWTYPE_8))));
	}
	
	@Test
	public void test42_getTypeNotExists() throws Exception { 
		RequestBuilder request = setDefaultRequest(get("/user/type/{typeId}", NEXT_TYPE_ID), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setNullResult(response);
	}
	
	
	
	

}
