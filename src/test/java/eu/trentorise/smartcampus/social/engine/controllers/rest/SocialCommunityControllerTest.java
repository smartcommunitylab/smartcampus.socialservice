package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

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

import eu.trentorise.smartcampus.social.engine.beans.Community;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml",
		"classpath:/spring/spring-security.xml" })
public class SocialCommunityControllerTest extends SCControllerTest {

	private static final String APPID = "testCom";

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(
				springSecurityFilterChain).build();
	}

	@Test
	public void create() throws Exception {
		Community community = new Community();
		community.setName("SC Smartcampus");

		RequestBuilder request = setDefaultRequest(
				post("/app/{appId}/community", APPID), Scope.CLIENT).content(
				convertObjectToJsonString(community));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.name").value("SC Smartcampus"));
	}

	@Test
	public void deleteCommunity() throws Exception {
		Community community = new Community();
		community.setName("SC Smartcampus");

		RequestBuilder request = setDefaultRequest(
				post("/app/{appId}/community", APPID), Scope.CLIENT).content(
				convertObjectToJsonString(community));
		ResultActions response = mockMvc.perform(request);
		MvcResult result = setDefaultResult(response).andReturn();
		community = convertJsonToObject(result.getResponse()
				.getContentAsString(), Community.class);

		request = setDefaultRequest(
				delete("/app/{appId}/community/{communityId}", APPID,
						community.getId()), Scope.CLIENT);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(content().string("true"));
	}
}
