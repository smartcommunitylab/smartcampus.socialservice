package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import eu.trentorise.smartcampus.social.engine.beans.Community;
import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.repo.EntityRepository;
import eu.trentorise.smartcampus.social.engine.repo.SocialTypeRepository;
import eu.trentorise.smartcampus.social.managers.SocialCommunityManager;
import eu.trentorise.smartcampus.social.managers.SocialTypeManager;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml",
		"classpath:/spring/spring-security.xml" })
public class EntityControllerTest extends SCControllerTest {

	private static final String APPID = "testCom";
	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	SocialTypeManager typeManager;

	@Autowired
	SocialCommunityManager communityManager;

	@Autowired
	SocialTypeRepository typeRepo;

	@Autowired
	EntityRepository entityRepo;

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(
				springSecurityFilterChain).build();
	}

	@After
	public void cleanup() {
		entityRepo.deleteAll();
		typeRepo.deleteAll();
	}

	@Test
	public void userEntities() throws Exception {
		EntityType type = typeManager.create("image", "image/jpg");
		Entity entity = new Entity();
		entity.setLocalId("3455");
		entity.setName("entity share");
		entity.setType(type.getId());

		RequestBuilder request = setDefaultRequest(get("/user/entity"),
				Scope.USER).content(convertObjectToJsonString(entity));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
				.andExpect(jsonPath("$", Matchers.hasSize(0)));

		request = setDefaultRequest(post("/user/{appId}/entity", APPID),
				Scope.USER).content(convertObjectToJsonString(entity));
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.uri").value(APPID + "." + entity.getLocalId()))
				.andExpect(jsonPath("$.owner").value("1"));

		request = setDefaultRequest(get("/user/entity"), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response)
				.andExpect(jsonPath("$", Matchers.hasSize(1)));

		request = setDefaultRequest(
				get("/user/{appId}/entity/{localId}", APPID,
						entity.getLocalId()), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.name").value("entity share"));

		request = setDefaultRequest(
				get("/user/{appId}/entity/{localId}", APPID,
						entity.getLocalId()), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.name").value("entity share"));
	}

	@Test
	public void communityEntities() throws Exception {
		EntityType type = typeManager.create("image", "image/jpg");
		Community community = communityManager.create("SC lab", APPID);
		Entity entity = new Entity();
		entity.setLocalId("3455");
		entity.setName("entity share");
		entity.setType(type.getId());

		RequestBuilder request = setDefaultRequest(
				get("/app/{appId}/community/{communityId}/entity", APPID,
						community.getId()), Scope.CLIENT).content(
				convertObjectToJsonString(entity));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
				.andExpect(jsonPath("$", Matchers.hasSize(0)));

		request = setDefaultRequest(
				post("/app/{appId}/community/{communityId}/entity", APPID,
						community.getId()), Scope.CLIENT).content(
				convertObjectToJsonString(entity));
		response = mockMvc.perform(request);
		MvcResult result = setDefaultResult(response)
				.andExpect(
						jsonPath("$.uri").value(
								APPID + "." + entity.getLocalId()))
				.andExpect(
						jsonPath("$.communityOwner").value(community.getId()))
				.andReturn();
		entity = convertJsonToObject(result.getResponse().getContentAsString(),
				Entity.class);

		request = setDefaultRequest(
				get("/app/{appId}/community/{communityId}/entity", APPID,
						community.getId()), Scope.CLIENT);
		response = mockMvc.perform(request);
		setDefaultResult(response)
				.andExpect(jsonPath("$", Matchers.hasSize(1)));

		request = setDefaultRequest(
				get("/app/{appId}/community/{communityId}/entity/{localId}",
						APPID, community.getId(), entity.getLocalId()),
				Scope.CLIENT);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.name").value("entity share"));

		// TODO check when SecurityException is manage
		// request = setDefaultRequest(
		// post("/app/{appId}/community/{communityId}/entity", APPID,
		// "111111"), Scope.CLIENT).content(
		// convertObjectToJsonString(entity));
		// response = mockMvc.perform(request);
	}
}
