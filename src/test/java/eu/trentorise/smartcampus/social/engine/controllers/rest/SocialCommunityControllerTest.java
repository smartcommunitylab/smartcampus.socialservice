package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.hamcrest.Matchers;
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

	@Test
	public void readCommunities() throws Exception {
		RequestBuilder request = setDefaultRequest(get("/community"),
				Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(content().string("[]"));

		Community community = new Community();
		community.setName("SC Smartcampus");
		request = setDefaultRequest(post("/app/{appId}/community", APPID),
				Scope.CLIENT).content(convertObjectToJsonString(community));
		mockMvc.perform(request);
		request = setDefaultRequest(get("/community"), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response)
				.andExpect(jsonPath("$", Matchers.hasSize(1)));

		community = new Community();
		community.setName("Coders");
		request = setDefaultRequest(post("/app/{appId}/community", APPID),
				Scope.CLIENT).content(convertObjectToJsonString(community));
		MvcResult result = mockMvc.perform(request).andReturn();
		community = convertJsonToObject(result.getResponse()
				.getContentAsString(), Community.class);
		request = setDefaultRequest(get("/community"), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response)
				.andExpect(jsonPath("$", Matchers.hasSize(2)));

		request = setDefaultRequest(
				get("/community/{communityId}", community.getId()), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response)
				.andExpect(jsonPath("$", Matchers.hasSize(1)));
	}
}
