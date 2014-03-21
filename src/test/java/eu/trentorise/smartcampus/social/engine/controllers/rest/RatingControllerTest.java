package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import junit.framework.Assert;

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

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Rating;
import eu.trentorise.smartcampus.social.engine.beans.Result;
import eu.trentorise.smartcampus.social.engine.beans.Visibility;
import eu.trentorise.smartcampus.social.managers.EntityManager;
import eu.trentorise.smartcampus.social.managers.EntityTypeManager;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml",
		"classpath:/spring/spring-security.xml" })
public class RatingControllerTest extends SCControllerTest {

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private EntityTypeManager typeManager;

	@Autowired
	private EntityManager entityManager;

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(
				springSecurityFilterChain).build();
	}

	private static final String APPID = "test";

	@Test
	public void rate() throws Exception {
		EntityType type = typeManager.create("image", "image/jpg");
		Entity entity = new Entity();
		entity.setLocalId("3455");
		entity.setName("entity share");
		entity.setType(type.getId());
		entity.setOwner("234");
		entity = entityManager.saveOrUpdate(APPID, entity);
		Assert.assertEquals(0d, entity.getRating());

		RequestBuilder request = setDefaultRequest(
				post("/user/{appId}/rating/{localId}", APPID, "3455"),
				Scope.USER).content("{\"rating\":2}");
		ResultActions response = mockMvc.perform(request);
		setForbiddenExceptionResult(response);

		entity.setVisibility(new Visibility(true));
		entity = entityManager.saveOrUpdate(APPID, entity);

		request = setDefaultRequest(
				post("/user/{appId}/rating/{localId}", APPID, "3455"),
				Scope.USER).content("{\"rating\":2}");
		response = mockMvc.perform(request);
		setDefaultResult(response);

		request = setDefaultRequest(
				get("/user/{appId}/rating/{localId}", APPID, "3455"),
				Scope.USER);
		response = mockMvc.perform(request);
		MvcResult result = setDefaultResult(response).andReturn();
		String json = result.getResponse().getContentAsString();
		Result data = convertJsonToObject(json, Result.class);
		Rating rating = convertObject(data.getData(), Rating.class);
		Assert.assertEquals(2d, rating.getRating());
	}
}
