package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.hamcrest.Matchers.allOf;
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
	private static final String COMMENTID_TEST1 = "53187aa644ae237d0fd6845d";
	private static final String COMMENTID_TEST2 = "53187aa644ae237d0fd6845e";
	private static final String NAME_SURNAME = "name surname";

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
	}
	
	//@Test
	public void test1_createComments() throws Exception {
		Comment newComment = new Comment();
		String entityId = "1";
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
	}
	
	@Test
	public void test2_readComments() throws Exception {
		RequestBuilder request = setDefaultRequest(get("/user/comment"), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
	}
	
	@Test
	public void test21_readComment() throws Exception {
		String id = COMMENTID_TEST1;
		RequestBuilder request = setDefaultRequest(get("/user/comment/{commentId}", id), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(COMMENT_TEST2)));
	}
	
	@Test
	public void test22_readCommentsByEntity() throws Exception {
		String entityId = "1";
		RequestBuilder request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
	}	
	
	@Test
	public void test23_readCommentsByAuthor() throws Exception {
		RequestBuilder request = setDefaultRequest(get("/user/comment"), Scope.USER).param("author", NAME_SURNAME);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
		
		// add pagination
		request = setDefaultRequest(get("/user/comment"), Scope.USER).param("author", NAME_SURNAME).param("pageNum","2").param("pageSize","5");
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
		
		// add sort
		request = setDefaultRequest(get("/user/comment"), Scope.USER).param("author", NAME_SURNAME).param("pageNum","2").param("pageSize","5").param("sortList", "creationTime");
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
	}
	
	@Test
	public void test22_readCommentsByEntityAndAuthor() throws Exception {
		String entityId = "1";
		RequestBuilder request = setDefaultRequest(get("/user/entity/{entityId}/comment", entityId), Scope.USER).param("author", NAME_SURNAME);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(COMMENT_TEST1), containsString(COMMENT_TEST2), containsString(COMMENT_TEST3))));
	}
	
	@Test
	public void test3_deleteComment() throws Exception {
		String entityId = "1";
		String commentId = COMMENTID_TEST2;
		RequestBuilder request = setDefaultRequest(delete("/user/entity/{entityId}/comment/{commentId}", entityId, commentId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(" has delete the comment")));
	}
	
	
}
