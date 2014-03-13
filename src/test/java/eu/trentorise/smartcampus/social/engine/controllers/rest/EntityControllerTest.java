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
import eu.trentorise.smartcampus.social.engine.beans.Result;
import eu.trentorise.smartcampus.social.engine.beans.Visibility;
import eu.trentorise.smartcampus.social.engine.repo.CommunityRepository;
import eu.trentorise.smartcampus.social.engine.repo.EntityRepository;
import eu.trentorise.smartcampus.social.engine.repo.SocialTypeRepository;
import eu.trentorise.smartcampus.social.managers.SocialCommunityManager;
import eu.trentorise.smartcampus.social.managers.SocialTypeManager;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml",
		"classpath:/spring/spring-security.xml" })
public class EntityControllerTest extends SCControllerTest {

	private static final String APPID_1 = "space1";
	private static final String APPID_2 = "space2";

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

	@Autowired
	CommunityRepository communityRepo;

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(
				springSecurityFilterChain).build();
	}

	@After
	public void cleanup() {
		entityRepo.deleteAll();
		typeRepo.deleteAll();
		communityRepo.deleteAll();
	}

	@Test
	public void userEntities() throws Exception {
		EntityType type = typeManager.create("image", "image/jpg");
		Entity entity = new Entity();
		entity.setLocalId("3455");
		entity.setName("entity share");
		entity.setType(type.getId());

		RequestBuilder request = setDefaultRequest(get("/user/entity"),
				Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data", Matchers.hasSize(0)));

		request = setDefaultRequest(post("/user/{appId}/entity", APPID_1),
				Scope.USER).content(convertObjectToJsonString(entity));
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data.uri").value(
						APPID_1 + "." + entity.getLocalId())).andExpect(
				jsonPath("$.data.owner").value("1"));

		request = setDefaultRequest(get("/user/entity"), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data", Matchers.hasSize(1)));

		request = setDefaultRequest(
				get("/user/{appId}/entity/{localId}", APPID_1,
						entity.getLocalId()), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data.name").value("entity share"));

		request = setDefaultRequest(
				get("/user/{appId}/entity/{localId}", APPID_1,
						entity.getLocalId()), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data.name").value("entity share"));
	}

	@Test
	public void userSharing() throws Exception {
		EntityType type = typeManager.create("image", "image/jpg");
		Community community = communityManager.create("SC lab", APPID_1);
		Entity entity = new Entity();
		entity.setLocalId("3455");
		entity.setName("entity share");
		entity.setType(type.getId());
		entity.setVisibility(new Visibility(true));

		RequestBuilder request = setDefaultRequest(
				post("/app/{appId}/community/{communityId}/entity", APPID_1,
						community.getId()), Scope.CLIENT).content(
				convertObjectToJsonString(entity));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response);
		request = setDefaultRequest(get("/user/shared"), Scope.USER);
		response = mockMvc.perform(request);
		response.andExpect(jsonPath("$.data", Matchers.hasSize(1)));

		request = setDefaultRequest(
				get("/user/{appId}/shared/{localId}", APPID_1,
						entity.getLocalId()), Scope.USER);
		response = mockMvc.perform(request);
		response.andExpect(jsonPath("$.data.visibility.publicShared").value(
				true));

	}

	@Test
	public void communityEntities() throws Exception {
		EntityType type = typeManager.create("image", "image/jpg");
		Community community = communityManager.create("SC lab", APPID_1);
		Entity entity = new Entity();
		entity.setLocalId("3455");
		entity.setName("entity share");
		entity.setType(type.getId());

		RequestBuilder request = setDefaultRequest(
				get("/app/{appId}/community/{communityId}/entity", APPID_1,
						community.getId()), Scope.CLIENT).content(
				convertObjectToJsonString(entity));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data", Matchers.hasSize(0)));

		request = setDefaultRequest(
				post("/app/{appId}/community/{communityId}/entity", APPID_1,
						community.getId()), Scope.CLIENT).content(
				convertObjectToJsonString(entity));
		response = mockMvc.perform(request);
		MvcResult result = setDefaultResult(response)
				.andExpect(
						jsonPath("$.data.uri").value(
								APPID_1 + "." + entity.getLocalId()))
				.andExpect(
						jsonPath("$.data.communityOwner").value(
								community.getId())).andReturn();
		Result r = convertJsonToObject(result.getResponse()
				.getContentAsString(), Result.class);
		entity = convertObject(r.getData(), Entity.class);

		request = setDefaultRequest(
				get("/app/{appId}/community/{communityId}/entity", APPID_1,
						community.getId()), Scope.CLIENT);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data", Matchers.hasSize(1)));

		request = setDefaultRequest(
				get("/app/{appId}/community/{communityId}/entity/{localId}",
						APPID_1, community.getId(), entity.getLocalId()),
				Scope.CLIENT);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data.name").value("entity share"));

	}

	@Test
	public void communitySecurityCases() throws Exception {
		EntityType type = typeManager.create("image", "image/jpg");
		Community community = communityManager.create("SC lab", APPID_1);
		Community community1 = communityManager.create("Coders", APPID_1);
		Entity entity = new Entity();
		entity.setLocalId("3455");
		entity.setName("entity share");
		entity.setType(type.getId());

		/**
		 * create entity from not existing community
		 */
		RequestBuilder request = setDefaultRequest(
				post("/app/{appId}/community/{communityId}/entity", APPID_1,
						"111111"), Scope.CLIENT).content(
				convertObjectToJsonString(entity));
		ResultActions response = mockMvc.perform(request);
		setForbiddenExceptionResult(response);

		/**
		 * try to edit not owned entity
		 */
		request = setDefaultRequest(
				post("/app/{appId}/community/{communityId}/entity", APPID_1,
						community.getId()), Scope.CLIENT).content(
				convertObjectToJsonString(entity));
		mockMvc.perform(request);

		entity.setName("CHANGED");
		request = setDefaultRequest(
				post("/app/{appId}/community/{communityId}/entity", APPID_1,
						community1.getId()), Scope.CLIENT).content(
				convertObjectToJsonString(entity));
		response = mockMvc.perform(request);
		setForbiddenExceptionResult(response);

		/**
		 * try to read not owned entity
		 */
		request = setDefaultRequest(
				get("/app/{appId}/community/{communityId}/entity/{localId}",
						APPID_1, community1.getId(), entity.getLocalId()),
				Scope.CLIENT);
		response = mockMvc.perform(request);
		setForbiddenExceptionResult(response);
	}

	@Test
	public void readByAppId() throws Exception {

		EntityType type = typeManager.create("image", "image/jpg");
		for (int i = 0; i < 5; i++) {
			Entity entity = new Entity();
			entity.setLocalId("3455" + i);
			entity.setName("entity share");
			entity.setType(type.getId());

			RequestBuilder request = setDefaultRequest(
					post("/user/{appId}/entity", APPID_1), Scope.USER).content(
					convertObjectToJsonString(entity));
			ResultActions response = mockMvc.perform(request);
		}

		for (int i = 0; i < 3; i++) {
			Entity entity = new Entity();
			entity.setLocalId("3455" + i);
			entity.setName("entity share");
			entity.setType(type.getId());
			entity.setVisibility(new Visibility(true));
			RequestBuilder request = setDefaultRequest(
					post("/user/{appId}/entity", APPID_2), Scope.USER).content(
					convertObjectToJsonString(entity));
			ResultActions response = mockMvc.perform(request);
		}

		RequestBuilder request = setDefaultRequest(
				get("/user/entity").param("sortDirection", "1").param(
						"sortList", "uri"), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data", Matchers.hasSize(8))).andExpect(
				jsonPath("$.data[0].uri").value("space2.34552"));

		request = setDefaultRequest(get("/user/{appId}/entity", APPID_1),
				Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data", Matchers.hasSize(5)));

		request = setDefaultRequest(get("/user/{appId}/entity", APPID_2),
				Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data", Matchers.hasSize(3)));

		request = setDefaultRequest(get("/user/{appId}/shared", APPID_2),
				Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data", Matchers.hasSize(0)));

	}

	@Test
	public void communitySharing() throws Exception {
		EntityType type = typeManager.create("image", "image/jpg");
		Community community = communityManager.create("SC lab", APPID_1);
		Entity entity = new Entity();
		entity.setLocalId("3455");
		entity.setName("entity share");
		entity.setType(type.getId());
		entity.setVisibility(new Visibility(true));

		RequestBuilder request = setDefaultRequest(
				post("/user/{appId}/entity", APPID_1), Scope.USER).content(
				convertObjectToJsonString(entity));
		ResultActions response = mockMvc.perform(request);

		request = setDefaultRequest(
				get("/app/{appId}/community/{communityId}/shared", APPID_1,
						community.getId()), Scope.CLIENT);
		response = mockMvc.perform(request);
		response.andExpect(jsonPath("$.data", Matchers.hasSize(1)));

		request = setDefaultRequest(
				get("/app/{appId}/community/{communityId}/shared/{localId}",
						APPID_1, community.getId(), entity.getLocalId()),
				Scope.CLIENT);
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				jsonPath("$.data.visibility.publicShared").value(true));

		entity.setVisibility(new Visibility());
		request = setDefaultRequest(post("/user/{appId}/entity", APPID_1),
				Scope.USER).content(convertObjectToJsonString(entity));
		response = mockMvc.perform(request);

		request = setDefaultRequest(
				get("/app/{appId}/community/{communityId}/shared", APPID_1,
						community.getId()), Scope.CLIENT);
		response = mockMvc.perform(request);
		response.andExpect(jsonPath("$.data", Matchers.hasSize(0)));

	}
}
