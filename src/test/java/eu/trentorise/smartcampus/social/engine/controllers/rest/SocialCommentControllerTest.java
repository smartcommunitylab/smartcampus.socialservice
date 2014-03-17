package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import eu.trentorise.smartcampus.social.engine.beans.Comment;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:/spring/applicationContext.xml", "classpath:/spring/spring-security.xml"})
public class SocialCommentControllerTest extends SCControllerTest {
	
	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	private String COMMENT_TEST1 = "Comment test 1";
	private String COMMENT_TEST2 = "Comment test 2";
	private String COMMENT_TEST3 = "Comment test 3";
	private static final String COMMENTID_TEST1 = "5327152d44ae86e52a18bbbf";
	private static final String COMMENTID_TEST2 = "5327155544ae0ed53c379f95";
	private static final String COMMENTID_TEST3 = "53271c0644ae8d37069ea225";
	private static final String COMMENTID_TESTNE = "111122223333aaaabbbbcccc";
	private static final String NAME_SURNAME = "name surname";
	private static final String NAME_SURNAME_NE = "aa bb";
	private String ENTITY_ID = "1";
	private String ENTITY_ID_NE = "9999";
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
	}
	
	// @Test
	public void test1_createComments() throws Exception {
		Comment newComment = new Comment();
		String entityId = ENTITY_ID;
		// Create and Post first comment
		newComment.setText(COMMENT_TEST1);
		RequestBuilder request = setDefaultRequest(post("/user/entity/{entityId}/comment", entityId), Scope.USER).content(convertObjectToJsonString(newComment));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(COMMENT_TEST1)));
		
		// Create and Post second comment
		newComment.setText(COMMENT_TEST2);
		request = setDefaultRequest(post("/user/entity/{entityId}/comment", entityId), Scope.USER).content(convertObjectToJsonString(newComment));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(COMMENT_TEST2)));
		
		// Create and Post third comment
		newComment.setText(COMMENT_TEST3);
		request = setDefaultRequest(post("/user/entity/{entityId}/comment", entityId), Scope.USER).content(convertObjectToJsonString(newComment));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(COMMENT_TEST3)));
		
		// Create and Post 4 comment
		newComment.setText(COMMENT_TEST1);
		request = setDefaultRequest(post("/user/entity/{entityId}/comment", entityId), Scope.USER).content(convertObjectToJsonString(newComment));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(COMMENT_TEST1)));
				
		// Create and Post 5 comment
		newComment.setText(COMMENT_TEST2);
		request = setDefaultRequest(post("/user/entity/{entityId}/comment", entityId), Scope.USER).content(convertObjectToJsonString(newComment));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(COMMENT_TEST2)));
		
		// Create and Post 6 comment
		newComment.setText(COMMENT_TEST2);
		request = setDefaultRequest(post("/user/entity/{entityId}/comment", entityId), Scope.USER).content(convertObjectToJsonString(newComment));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(COMMENT_TEST3)));
	}
	
	// @Test
	public void test11_createCommentNoText() throws Exception {
		Comment newComment = new Comment();
		String entityId = ENTITY_ID;
		// Create and Post first comment
		//newComment.setText(COMMENT_TEST1);
		RequestBuilder request = setDefaultRequest(post("/user/entity/{entityId}/comment", entityId), Scope.USER).content(convertObjectToJsonString(newComment));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("null")));
	}
	
	@Test
	public void test12_createCommentNoEntityId() throws Exception {
		Comment newComment = new Comment();
		String entityId = "";
		// Create and Post first comment
		newComment.setText(COMMENT_TEST1);
		RequestBuilder request = setDefaultRequest(post("/user/entity/{entityId}/comment", entityId), Scope.USER).content(convertObjectToJsonString(newComment));
		ResultActions response = mockMvc.perform(request);
		setForbiddenExceptionResult(response);
	}
	
	@Test
	public void test2_readComments() throws Exception {
		RequestBuilder request = setDefaultRequest(get("/user/comment"), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
	}
	
	/*
	 * NB: retrieve ad existing 'comment id' from db before launching the test 
	 */
	@Test
	public void test21_readCommentById() throws Exception {
		String id = COMMENTID_TEST1;
		RequestBuilder request = setDefaultRequest(get("/user/comment/{commentId}", id), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(COMMENT_TEST2)));
		
		// null passed
		id = null;
		request = setDefaultRequest(get("/user/comment/{commentId}", id), Scope.USER);
		response = mockMvc.perform(request);
		setDefaultResult(response);
	}
	
	@Test
	public void test22_readCommentsByEntity() throws Exception {
		String entityId = ENTITY_ID;
		RequestBuilder request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
	
		// entityId not exist
		request = setDefaultRequest(get("/user/entity/{entityId}/comment", ENTITY_ID_NE), Scope.USER);
		response = mockMvc.perform(request);
		setVoidResult(response);
	
		// entityId null
		String nullEntityId = null;
		request = setDefaultRequest(get("/user/entity/{entityId}/comment", nullEntityId), Scope.USER);
		response = mockMvc.perform(request);
		setForbiddenExceptionResult(response);	
	}
	
	@Test
	public void test221_readCommentsByEntityNoParamPassed() throws Exception {
		String entityId = null;
		RequestBuilder request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setForbiddenExceptionResult(response);
	}	
	
	@Test
	public void test23_readCommentsByAuthor() throws Exception {
		RequestBuilder request = setDefaultRequest(get("/user/comment"), Scope.USER).param("author", NAME_SURNAME);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
		
		// add pagination
		request = setDefaultRequest(get("/user/comment"), Scope.USER).param("author", NAME_SURNAME).param("pageNum","1").param("pageSize","5");
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(anyOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
		
		// add sort
		request = setDefaultRequest(get("/user/comment"), Scope.USER).param("author", NAME_SURNAME).param("pageNum","1").param("pageSize","5").param("sortList", "creationTime").param("sortDirection", "-1");
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
		
		// add time limit
		Long now = System.currentTimeMillis();
		Long yesterday = now-86400000;
		Long tomorrow = now+86400000;
		request = setDefaultRequest(get("/user/comment"), Scope.USER).param("author", NAME_SURNAME).param("fromDate", yesterday.toString()).param("toDate", tomorrow.toString());
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
	
		// author not exists
		request = setDefaultRequest(get("/user/comment"), Scope.USER).param("author", NAME_SURNAME_NE);
		response = mockMvc.perform(request);
		setVoidResult(response);
		
		// author null
		String nullAuthor = null;
		request = setDefaultRequest(get("/user/comment"), Scope.USER).param("author", nullAuthor);
		response = mockMvc.perform(request);
		setDefaultResult(response);
	}
	
	@Test
	public void test22_readCommentsByEntityAndAuthor() throws Exception {
		String entityId = ENTITY_ID;
		RequestBuilder request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER).param("author", NAME_SURNAME);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
	
		// author not exists
		request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER).param("author", NAME_SURNAME_NE);
		response = mockMvc.perform(request);
		setVoidResult(response);
				
		// author null
		String nullAuthor = null;
		request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER).param("author", nullAuthor);
		response = mockMvc.perform(request);
		setDefaultResult(response);
		
		// entityId not exists
		request = setDefaultRequest(get("/user/entity/{entityId}/comment", ENTITY_ID_NE), Scope.USER).param("author", NAME_SURNAME);
		response = mockMvc.perform(request);
		setVoidResult(response);
						
		// entityId null
		String nullEntityId = null;
		request = setDefaultRequest(get("/user/entity/{entityId}/comment", nullEntityId), Scope.USER).param("author", NAME_SURNAME);
		response = mockMvc.perform(request);
		setForbiddenExceptionResult(response);
	}
	
	/*
	 * NB: retrieve ad existing 'comment id' from db before launching the test 
	 */
	@Test
	public void test3_deleteComment() throws Exception {
		String commentId = COMMENTID_TEST2;
		RequestBuilder request = setDefaultRequest(delete("/user/comment/{commentId}", commentId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(" has delete the comment")));
	}
	
	@Test
	public void test31_deleteCommentNoCommentId() throws Exception {
		String commentId = null;
		RequestBuilder request = setDefaultRequest(delete("/user/comment/{commentId}", commentId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setForbiddenExceptionResult(response);
	}
	
//	@Test
//	public void test32_deleteCommentNoEntityId() throws Exception {
//		String entityId = null;
//		String commentId = COMMENTID_TEST2;
//		RequestBuilder request = setDefaultRequest(delete("/user/comment/{commentId}", entityId, commentId), Scope.USER);
//		ResultActions response = mockMvc.perform(request);
//		setForbiddenExceptionResult(response);
//	}
	
	/*
	 * NB: retrieve ad existing 'comment id' from db before launching the test 
	 */
	@Test
	public void test33_deleteCommentNotPermitted() throws Exception {
		String commentId = COMMENTID_TEST3;
		RequestBuilder request = setDefaultRequest(delete("/user/comment/{commentId}", commentId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
	}
	
	@Test
	public void test34_deleteNotExistingComment() throws Exception {
		String entityId = ENTITY_ID;
		String commentId = COMMENTID_TESTNE;
		RequestBuilder request = setDefaultRequest(delete("/user/comment/{commentId}", entityId, commentId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setNullResult(response);
	}
	
	
}
