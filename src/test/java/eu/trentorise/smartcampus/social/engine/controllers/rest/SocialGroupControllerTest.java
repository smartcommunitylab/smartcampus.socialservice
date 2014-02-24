package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import eu.trentorise.smartcampus.social.engine.beans.Group;



@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:/spring/applicationContext.xml", "classpath:/spring/spring-security.xml"}) //"classpath:/resourceList.xml"
public class SocialGroupControllerTest {
	
	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	protected static final String RH_AUTH_TOKEN = "Authorization";
	private static final String token = "ebc32140-3234-4202-b43d-8680f537d96a";
	private static final String NEWGROUP_1 = "my_friends";
	private static final String NEWGROUP_2 = "my_family";
	private static final String NEWGROUP_3 = "my_collegue";
	private static final String EDITGROUP_1 = "my_city";
	
	private static final long GROUP_ID_1 = 1L;
	private static final long GROUP_ID_2 = 2L;
	private static final long GROUP_ID_3 = 3L;
	
	private static final String CHAR_ENCODING = "UTF-8";
	private static final String CONTENT_TYPE = "application/json;charset=UTF-8";
	
	
	/**
	 * @param token
	 * @return
	 */
	protected static String bearer(String token) {
		return "Bearer " + token;
	}
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
	}
	
	@Test
	public void test0_getGroups() throws Exception {  
		RequestBuilder request = setDefaultRequest(get("/user/group"));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string("[]"));	// void list
	}	
	
	@Test
	public void test1_setGroup() throws Exception {
		Group newGroup = new Group();
		// Create and Post first group
		newGroup.setName(NEWGROUP_1);
		RequestBuilder request = setDefaultRequest(post("/user/group")).content(convertObjectToJsonString(newGroup));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(jsonPath("$.name").value(NEWGROUP_1));
		
		// Create and Post second group
		newGroup.setName(NEWGROUP_2);
		request = setDefaultRequest(post("/user/group")).content(convertObjectToJsonString(newGroup));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(jsonPath("$.name").value(NEWGROUP_2));
		
		// Create and Post second group
		newGroup.setName(NEWGROUP_3);
		request = setDefaultRequest(post("/user/group")).content(convertObjectToJsonString(newGroup));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(jsonPath("$.name").value(NEWGROUP_3));	
	}
	
	@Test
	public void test11_setGroupAlreadyExists() throws Exception {
		ResultActions response = null;
		Group newGroup = new Group();
		// Create and Post an already existing group (first group)
		newGroup.setName(NEWGROUP_1);
		RequestBuilder request = setDefaultRequest(post("/user/group")).content(convertObjectToJsonString(newGroup));	
		response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);	
	}
	
	@Test
	public void test12_setGroupVoidParam() throws Exception {
		ResultActions response = null;
		Group newGroup = new Group();
		// Create and Post a group with no name
		RequestBuilder request = setDefaultRequest(post("/user/group")).content(convertObjectToJsonString(newGroup));	
		response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);	
	}	
	
	@Test
	public void test2_getGroups() throws Exception {  
		RequestBuilder request = setDefaultRequest(get("/user/group"));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(jsonPath("$[0].name").value(NEWGROUP_1));
	}
	
	@Test
	public void test3_readGroup() throws Exception {  
		RequestBuilder request = setDefaultRequest(get("/user/group/{id}", GROUP_ID_1));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(jsonPath("$.name").value(NEWGROUP_1));
	}
	
	@Test
	public void test4_updateGroup() throws Exception { 
		Group editGroup = new Group();
		editGroup.setName(EDITGROUP_1);
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}", GROUP_ID_2)).content(convertObjectToJsonString(editGroup));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(jsonPath("$.name").value(EDITGROUP_1));
	}
	
	@Test
	public void test5_addGroupMembers() throws Exception { 
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}/members", GROUP_ID_2)).param("userIds", "30").param("userIds", "31").param("userIds", "44");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string("true"));
	}
	
	@Test
	public void test6_deleteGroupMembers() throws Exception { 
		RequestBuilder request = setDefaultRequest(delete("/user/group/{id}/members", GROUP_ID_2)).param("userIds", "30").param("userIds", "31").param("userIds", "55");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string("true"));
	}	
	
	@Test
	public void test7_deleteGroup() throws Exception {
		// Delete first group
		RequestBuilder request = setDefaultRequest(delete("/user/group/{id}", GROUP_ID_1));
		ResultActions response = mockMvc.perform(request);
		response = setDefaultResult(response)
		.andExpect(content().string("true"));
		
		// Delete first group
		request = setDefaultRequest(delete("/user/group/{id}", GROUP_ID_2));
		response = mockMvc.perform(request);
		response = setDefaultResult(response)
		.andExpect(content().string("true"));
		
		// Delete first group
		request = setDefaultRequest(delete("/user/group/{id}", GROUP_ID_3));
		response = mockMvc.perform(request);
		response = setDefaultResult(response)
		.andExpect(content().string("true"));
	}	

	
	@SuppressWarnings("unused")
	private static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Inclusion.NON_NULL); //JsonInclude.Include.NON_NULL
        return mapper.writeValueAsBytes(object);
    }
	
	private static String convertObjectToJsonString(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter jsonOut = mapper.writer().withDefaultPrettyPrinter();
        return jsonOut.writeValueAsString(object);
    }
	
	private MockHttpServletRequestBuilder setDefaultRequest(MockHttpServletRequestBuilder request){
		return request.header(RH_AUTH_TOKEN, bearer(token)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).characterEncoding(CHAR_ENCODING);
	}
	
	private ResultActions setDefaultResult(ResultActions result) throws Exception{
		return result.andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(CONTENT_TYPE));
	}
	
	private ResultActions setIllegalArgumentExceptionResult(ResultActions result) throws Exception{
		return result.andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string(""));
	}
	

}
