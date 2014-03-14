package eu.trentorise.smartcampus.social.engine;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;

import eu.trentorise.smartcampus.social.engine.controllers.rest.SCControllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


import eu.trentorise.smartcampus.social.engine.beans.Comment;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:/spring/applicationContext.xml", "classpath:/spring/spring-security.xml"})
public class CommentsPerformaceTest extends SCControllerTest {
	
	private static final String COMMENTID_TEST1 = "5322bb0744aed84bd269d1ec";
	
	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
	}
	
	//@Test
	public void createComments() throws Exception{
		Comment newComment = new Comment();
		String entityId = "1";
		RequestBuilder request = null;
		ResultActions response = null;
		for(int i = 0; i < 100; i++){
			// Create and Post comments
			newComment.setText(String.format("Test comment %s", i));
			request = setDefaultRequest(post("/user/entity/{entityId}/comment", entityId), Scope.USER).content(convertObjectToJsonString(newComment));
			response = mockMvc.perform(request);
			setDefaultResult_NoPrint(response);
		}
	}
	
	@Test
	public void findTest_MongoRepo() throws Exception {
		Long fromTime = System.currentTimeMillis() - 18000000;	// 5 hours of rounding
		Long toTime = System.currentTimeMillis() + 18000000;	// 5 hours of rounding
		String entityId = "1";
		RequestBuilder request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER).param("findType", "0").param("pageNum", "4").param("pageSize", "100").param("sortList", "creationTime").param("sortDirection", "1").param("fromDate", fromTime.toString()).param("toDate", toTime.toString());
		Long startTime = System.currentTimeMillis();
		ResultActions response = mockMvc.perform(request);
		Long endTime = System.currentTimeMillis();
		System.out.println(String.format("MongoRepo multiple find usage time : '%s' millis", endTime-startTime));
		setDefaultResult_NoPrint(response);
	}
	
	@Test
	public void singlefindTest_MongoRepo() throws Exception {
		String id = COMMENTID_TEST1;
		RequestBuilder request = setDefaultRequest(get("/user/comment/{commentId}", id), Scope.USER).param("findType", "0");
		Long startTime = System.currentTimeMillis();
		ResultActions response = mockMvc.perform(request);
		Long endTime = System.currentTimeMillis();
		System.out.println(String.format("MongoRepo single find usage time : '%s' millis", endTime-startTime));
		setDefaultResult_NoPrint(response);
	}
	
	@Test
	public void singlefindTestMultiParams_MongoRepo() throws Exception {
		String entityId = "1";
		RequestBuilder request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER).param("findType", "0").param("author", "name surname").param("commentText", "Test comment 499");
		Long startTime = System.currentTimeMillis();
		ResultActions response = mockMvc.perform(request);
		Long endTime = System.currentTimeMillis();
		System.out.println(String.format("MongoRepo single find multi params usage time : '%s' millis", endTime-startTime));
		setDefaultResult_NoPrint(response);
	}	
	
	@Test
	public void findTest_MongoRest() throws Exception {
		Long fromTime = System.currentTimeMillis() - 18000000;	// 5 hours of rounding
		Long toTime = System.currentTimeMillis() + 18000000;	// 5 hours of rounding
		String entityId = "1";
		RequestBuilder request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER).param("findType", "1").param("pageNum", "4").param("pageSize", "100").param("sortList", "creationTime").param("sortDirection", "1").param("fromDate", fromTime.toString()).param("toDate", toTime.toString());
		Long startTime = System.currentTimeMillis();
		ResultActions response = mockMvc.perform(request);
		Long endTime = System.currentTimeMillis();
		System.out.println(String.format("MongoRest multiple find usage time : '%s' millis", endTime-startTime));
		setDefaultResult_NoPrint(response);
	}
	
	@Test
	public void singlefindTest_MongoRest() throws Exception {
		String id = COMMENTID_TEST1;
		RequestBuilder request = setDefaultRequest(get("/user/comment/{commentId}", id), Scope.USER).param("findType", "1");
		Long startTime = System.currentTimeMillis();
		ResultActions response = mockMvc.perform(request);
		Long endTime = System.currentTimeMillis();
		System.out.println(String.format("MongoRest single find usage time : '%s' millis", endTime-startTime));
		setDefaultResult_NoPrint(response);
	}
	
	@Test
	public void singlefindTestMultiParams_MongoRest() throws Exception {
		String entityId = "1";
		RequestBuilder request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER).param("findType", "1").param("author", "name surname").param("commentText", "Test comment 499");
		Long startTime = System.currentTimeMillis();
		ResultActions response = mockMvc.perform(request);
		Long endTime = System.currentTimeMillis();
		System.out.println(String.format("MongoRest single find multi params usage time : '%s' millis", endTime-startTime));
		setDefaultResult_NoPrint(response);
	}		
	
	@Test
	public void findTest_MongoDriver() throws Exception {
		Long fromTime = System.currentTimeMillis() - 18000000;	// 5 hours of rounding
		Long toTime = System.currentTimeMillis() + 18000000;	// 5 hours of rounding
		String entityId = "1";
		RequestBuilder request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER).param("findType", "2").param("pageNum", "4").param("pageSize", "100").param("sortList", "creationTime").param("sortDirection", "1").param("fromDate", fromTime.toString()).param("toDate", toTime.toString());
		Long startTime = System.currentTimeMillis();
		ResultActions response = mockMvc.perform(request);
		Long endTime = System.currentTimeMillis();
		System.out.println(String.format("MongoDriver multiple find usage time : '%s' millis", endTime-startTime));
		setDefaultResult_NoPrint(response);	
	}
	
	@Test
	public void singlefindTest_MongoDriver() throws Exception {
		String id = COMMENTID_TEST1;
		RequestBuilder request = setDefaultRequest(get("/user/comment/{commentId}", id), Scope.USER).param("findType", "0");
		Long startTime = System.currentTimeMillis();
		ResultActions response = mockMvc.perform(request);
		Long endTime = System.currentTimeMillis();
		System.out.println(String.format("MongoDriver single find usage time : '%s' millis", endTime-startTime));
		setDefaultResult_NoPrint(response);
	}
	
	@Test
	public void singlefindTestMultiParams_MongoDriver() throws Exception {
		String entityId = "1";
		RequestBuilder request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER).param("findType", "2").param("author", "name surname").param("commentText", "Test comment 499");
		Long startTime = System.currentTimeMillis();
		ResultActions response = mockMvc.perform(request);
		Long endTime = System.currentTimeMillis();
		System.out.println(String.format("MongoDriver single find multi params usage time : '%s' millis", endTime-startTime));
		setDefaultResult_NoPrint(response);
	}	

}
