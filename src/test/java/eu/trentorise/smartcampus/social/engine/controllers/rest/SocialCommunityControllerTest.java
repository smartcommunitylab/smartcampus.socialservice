package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import junit.framework.Assert;

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

import eu.trentorise.smartcampus.social.engine.beans.Community;
import eu.trentorise.smartcampus.social.engine.repo.CommunityRepository;

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

	@Autowired
	CommunityRepository communityRepo;

	@After
	public void cleanup() {
		communityRepo.deleteAll();
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(
				springSecurityFilterChain).build();
	}

	@Test
	public void create() throws Exception {
		Community community = createCommunity("SC Smartcampus", APPID);

		Assert.assertEquals("SC Smartcampus", community.getName());

		RequestBuilder request = setDefaultRequest(
				post("/app/{appId}/community", APPID), Scope.CLIENT).content(
				"{\"name\": \"Palazzina\"}");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.name").value("Palazzina"));

		request = setDefaultRequest(post("/app/{appId}/community", APPID),
				Scope.CLIENT).content("{}");
		response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
	}

	@Test
	public void deleteCommunity() throws Exception {
		Community community = createCommunity("SC Smartcampus", APPID);

		RequestBuilder request = setDefaultRequest(
				delete("/app/{appId}/community/{communityId}", APPID,
						community.getId()), Scope.CLIENT);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(content().string("true"));
	}

	@Test
	public void readCommunities() throws Exception {
		RequestBuilder request = setDefaultRequest(get("/community"),
				Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(content().string("[]"));

		Community community = createCommunity("SC Smartcampus", APPID);
		request = setDefaultRequest(get("/community"), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response)
				.andExpect(jsonPath("$", Matchers.hasSize(1)));

		community = createCommunity("Coders", APPID);
		request = setDefaultRequest(get("/community"), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response)
				.andExpect(jsonPath("$", Matchers.hasSize(2)));

		request = setDefaultRequest(get("/community"), Scope.USER).param(
				"pageSize", "1");
		response = mockMvc.perform(request);
		setDefaultResult(response)
				.andExpect(jsonPath("$", Matchers.hasSize(1)));

		request = setDefaultRequest(
				get("/community/{communityId}", community.getId()), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.id").value(community.getId()));
	}

	@Test
	public void userSubscription() throws Exception {
		Community community = createCommunity("SC Smartcampus", APPID);
		RequestBuilder request = setDefaultRequest(
				put("/user/community/{communityId}/member", community.getId()),
				Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(content().string("true"));
		request = setDefaultRequest(
				get("/community/{communityId}", community.getId()), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.memberIds", Matchers.hasSize(1)));
		request = setDefaultRequest(
				delete("/user/community/{communityId}/member",
						community.getId()), Scope.USER);
		mockMvc.perform(request);
		request = setDefaultRequest(
				get("/community/{communityId}", community.getId()), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.memberIds", Matchers.hasSize(0)));
	}

	@Test
	public void membersSubscription() throws Exception {
		Community community = createCommunity("SC Smartcampus", APPID);

		RequestBuilder request = setDefaultRequest(
				put("/app/{appId}/community/{communityId}/members", APPID,
						community.getId()), Scope.CLIENT).param("userIds",
				"22", "23", "24");
		mockMvc.perform(request);

		request = setDefaultRequest(
				get("/community/{communityId}", community.getId()), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.memberIds", Matchers.hasSize(3)));
		request = setDefaultRequest(
				delete("/app/{appId}/community/{communityId}/members", APPID,
						community.getId()), Scope.CLIENT).param("userIds",
				"22", "43");
		mockMvc.perform(request);
		request = setDefaultRequest(
				get("/community/{communityId}", community.getId()), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.memberIds", Matchers.hasSize(2)));
	}

	private Community createCommunity(String name, String appId)
			throws Exception {
		Community community = new Community();
		community.setName(name);

		RequestBuilder request = setDefaultRequest(
				post("/app/{appId}/community", appId), Scope.CLIENT).content(
				convertObjectToJsonString(community));
		MvcResult result = setDefaultResult(mockMvc.perform(request))
				.andReturn();
		community = convertJsonToObject(result.getResponse()
				.getContentAsString(), Community.class);
		return community;
	}
}
