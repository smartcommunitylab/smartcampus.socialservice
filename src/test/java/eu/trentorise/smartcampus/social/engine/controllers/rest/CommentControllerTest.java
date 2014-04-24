/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

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

import eu.trentorise.smartcampus.social.engine.beans.Comment;
import eu.trentorise.smartcampus.social.managers.CommentManager;
import eu.trentorise.smartcampus.social.managers.EntityManager;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml",
		"classpath:/spring/spring-security.xml" })
public class CommentControllerTest extends SCControllerTest {

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private CommentManager commentManager;

	@Autowired
	private EntityManager entityManager;

	private MockMvc mockMvc;

	private String COMMENT_TEST1 = "Comment test 1";
	private String COMMENT_TEST2 = "Comment test 2";
	private String COMMENT_TEST3 = "Comment test 3";
	private String COMMENTID_TEST1 = "";
	private String COMMENTID_TEST2 = "";
	private static final String COMMENTID_TESTNE = "111122223333aaaabbbbcccc";
	private static final String NAME_SURNAME = "name surname";
	private static final String NAME_SURNAME_NE = "aa bb";
	private String APPID = "space1";
	private String LOCALID = "1";
	private String ENTITY_ID_NE = "9999";

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(
				springSecurityFilterChain).build();
		createComments();
	}

	public void createComments() throws Exception {
		Comment newComment = new Comment();
		// Create and Post first comment
		newComment.setText(COMMENT_TEST1);
		RequestBuilder request = setDefaultRequest(
				post("/user/{appI}/comment/{localId}", APPID, LOCALID),
				Scope.USER).content(convertObjectToJsonString(newComment));
		ResultActions response = mockMvc.perform(request);
		MvcResult result = setDefaultResult(response).andExpect(
				content().string(containsString(COMMENT_TEST1))).andReturn();
		String content = result.getResponse().getContentAsString();
		COMMENTID_TEST1 = extractIdFromResult(content);

		// Create and Post second comment
		newComment.setText(COMMENT_TEST2);
		request = setDefaultRequest(
				post("/user/{appI}/comment/{localId}", APPID, LOCALID),
				Scope.USER).content(convertObjectToJsonString(newComment));
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString(COMMENT_TEST2)));

		// Create and Post third comment
		newComment.setText(COMMENT_TEST3);
		request = setDefaultRequest(
				post("/user/{appI}/comment/{localId}", APPID, LOCALID),
				Scope.USER).content(convertObjectToJsonString(newComment));
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString(COMMENT_TEST3)));

		// Create and Post comment 4
		newComment.setText(COMMENT_TEST1);
		request = setDefaultRequest(
				post("/user/{appI}/comment/{localId}", APPID, LOCALID),
				Scope.USER).content(convertObjectToJsonString(newComment));
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString(COMMENT_TEST1)));

		// Create and Post comment 5
		newComment.setText(COMMENT_TEST2);
		request = setDefaultRequest(
				post("/user/{appI}/comment/{localId}", APPID, LOCALID),
				Scope.USER).content(convertObjectToJsonString(newComment));
		response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString(COMMENT_TEST2)));

		// Create and Post comment 6
		newComment.setText(COMMENT_TEST3);
		request = setDefaultRequest(
				post("/user/{appI}/comment/{localId}", APPID, LOCALID),
				Scope.USER).content(convertObjectToJsonString(newComment));
		response = mockMvc.perform(request);
		result = setDefaultResult(response).andExpect(
				content().string(containsString(COMMENT_TEST3))).andReturn();
		content = result.getResponse().getContentAsString();
		COMMENTID_TEST2 = extractIdFromResult(content);

	}

	@Test
	public void createCommentNoText() throws Exception {
		Comment newComment = new Comment();
		// Create and Post first comment
		// newComment.setText(COMMENT_TEST1);
		RequestBuilder request = setDefaultRequest(
				post("/user/{appI}/comment/{localId}", APPID, LOCALID),
				Scope.USER).content(convertObjectToJsonString(newComment));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString("null")));
	}

	@Test
	public void createCommentNoEntityId() throws Exception {
		Comment newComment = new Comment();
		String entityURI = "";
		// Create and Post first comment
		newComment.setText(COMMENT_TEST1);
		RequestBuilder request = setDefaultRequest(
				post("/user/entity/{entityURI}/comment", entityURI), Scope.USER)
				.content(convertObjectToJsonString(newComment));
		ResultActions response = mockMvc.perform(request);
		setForbiddenException(response);
	}

	@Test
	public void readCommentById() throws Exception {
		String id = COMMENTID_TEST1;
		RequestBuilder request = setDefaultRequest(
				get("/user/comment/{commentId}", id), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString(COMMENT_TEST1)));
	}

	@Test
	public void readCommentsByEntity() throws Exception {
		RequestBuilder request = setDefaultRequest(
				get("/user/{appId}/comment/{localId}", APPID, LOCALID),
				Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(
						allOf(containsString(COMMENT_TEST1),
								containsString(COMMENT_TEST2),
								containsString(COMMENT_TEST3))));

		// entityURI not exist
		request = setDefaultRequest(
				get("/user/{appId}/comment/{localId}", APPID, ENTITY_ID_NE),
				Scope.USER);
		response = mockMvc.perform(request);
		setVoidResult(response);

		// entityURI null
		request = setDefaultRequest(
				get("/user/{appId}/comment/{localId}", null, ENTITY_ID_NE),
				Scope.USER);
		response = mockMvc.perform(request);
		setForbiddenException(response);
	}

	@Test
	public void readCommentsByEntityNoParamPassed() throws Exception {
		RequestBuilder request = setDefaultRequest(
				get("/user/{appId}/comment/{localId}", null, null), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setForbiddenException(response);
	}

	@Test
	public void readCommentsByEntityAndAuthor() throws Exception {
		RequestBuilder request = setDefaultRequest(
				get("/user/{appId}/comment/{localId}", APPID, LOCALID),
				Scope.USER).param("author", NAME_SURNAME);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(
						allOf(containsString(COMMENT_TEST1),
								containsString(COMMENT_TEST2),
								containsString(COMMENT_TEST3))));

		// author not exists
		request = setDefaultRequest(
				get("/user/{appId}/comment/{localId}", APPID, LOCALID),
				Scope.USER).param("author", NAME_SURNAME_NE);
		response = mockMvc.perform(request);
		setVoidResult(response);

		// author null
		String nullAuthor = null;
		request = setDefaultRequest(
				get("/user/{appId}/comment/{localId}", APPID, LOCALID),
				Scope.USER).param("author", nullAuthor);
		response = mockMvc.perform(request);
		setDefaultResult(response);

		// entityURI not exists
		request = setDefaultRequest(
				get("/user/{appId}/comment/{localId}", APPID, "9999"),
				Scope.USER).param("author", NAME_SURNAME);
		response = mockMvc.perform(request);
		setVoidResult(response);

		// entityURI null
		request = setDefaultRequest(
				get("/user/{appId}/comment/{localId}", APPID, null), Scope.USER)
				.param("author", NAME_SURNAME);
		response = mockMvc.perform(request);
		setForbiddenException(response);
	}

	@Test
	public void deleteComment() throws Exception {
		String commentId = COMMENTID_TEST2;
		RequestBuilder request = setDefaultRequest(
				delete("/user/comment/{commentId}", commentId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString(" has delete the comment")));
	}

	@Test
	public void deleteCommentNoCommentId() throws Exception {
		String commentId = null;
		RequestBuilder request = setDefaultRequest(
				delete("/user/comment/{commentId}", commentId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setForbiddenException(response);
	}

	@Test
	public void deleteCommentNotPermitted() throws Exception {
		// here the manager is used to create a comment from another user
		Comment comment = commentManager.create("Comment Text other user",
				"pinco pallo", entityManager.defineUri(APPID, LOCALID));
		String commentId = comment.getId();
		RequestBuilder request = setDefaultRequest(
				delete("/user/comment/{commentId}", commentId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
	}

	@Test
	public void deleteNotExistingComment() throws Exception {
		String commentId = COMMENTID_TESTNE;
		RequestBuilder request = setDefaultRequest(
				delete("/user/comment/{commentId}", commentId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setNullResult(response);
	}

	@After
	public void removeComments() throws Exception {
		RequestBuilder request = setDefaultRequest(
				delete("/user/{appId}/comment/{localId}", APPID, LOCALID),
				Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response).andExpect(
				content().string(containsString("true")));
	}

	// Used in this tests. I can not intercept the exception because it is
	// launched from
	// permission manager. So I check only the response http status (403)
	protected ResultActions setForbiddenException(ResultActions result)
			throws Exception {
		return result.andDo(print()).andExpect(status().isForbidden());
	}

}
