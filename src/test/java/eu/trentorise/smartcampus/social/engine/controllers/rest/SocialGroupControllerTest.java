package eu.trentorise.smartcampus.social.engine.controllers.rest;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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

import eu.trentorise.smartcampus.social.engine.beans.Group;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:/spring/applicationContext.xml", "classpath:/spring/spring-security.xml"})
public class SocialGroupControllerTest extends SCControllerTest{
	
	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	protected static final String RH_AUTH_TOKEN = "Authorization";

	private static final String NEWGROUP_1 = "my_friends";
	private static final String NEWGROUP_2 = "my_family";
	private static final String NEWGROUP_3 = "my_collegue";
	private static final String NEWGROUP_4 = "my_group4";
	private static final String NEWGROUP_5 = "my_group5";
	private static final String NEWGROUP_6 = "my_group6";
	private static final String NEWGROUP_7 = "my_group7";
	private static final String NEWGROUP_8 = "my_group8";
	private static final String NEWGROUP_9 = "my_group9";
	private static final String NEWGROUP_10 = "my_group10";
	private static final String NEWGROUP_11 = "my_group11";
	
	private static final String EDITGROUP_1 = "my_city";
	private static final String EDITGROUP_2 = "my_friends";
	private static final String GROUP_NEX_ID = "123456";
	
	private static final String GROUP_MEMB_1 = "30";
	private static final String GROUP_MEMB_2 = "31";
	private static final String GROUP_MEMB_3 = "44";
	private static final String GROUP_MEMB_4 = "55";

	
	private static final long GROUP_ID_1 = 1L;
	private static final long GROUP_ID_2 = 2L;
	private static final long GROUP_ID_3 = 3L;
	
	private static final long GROUP_ID_9 = 9L;
	private static final long GROUP_ID_10 = 10L;
	private static final long GROUP_ID_11 = 11L;
	
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
	}
	
	@Test
	public void test1_getGroups() throws Exception {  
		RequestBuilder request = setDefaultRequest(get("/user/group"), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("[ ]")));	// void list
	}	
	
	@Test
	public void test11_setGroup() throws Exception {
		Group newGroup = new Group();
		// Create and Post first group
		newGroup.setName(NEWGROUP_1);
		RequestBuilder request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_1)));
		
		// Create and Post second group
		newGroup.setName(NEWGROUP_2);
		request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_2)));
		
		// Create and Post second group
		newGroup.setName(NEWGROUP_3);
		request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_3)));	
	}
	
	@Test
	public void test12_setGroupAlreadyExists() throws Exception {
		ResultActions response = null;
		Group newGroup = new Group();
		// Create and Post an already existing group (first group)
		newGroup.setName(NEWGROUP_1);
		RequestBuilder request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));	
		response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);	
	}
	
	@Test
	public void test13_setGroupVoidParam() throws Exception {
		ResultActions response = null;
		Group newGroup = new Group();
		// Create and Post a group with no name
		RequestBuilder request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));	
		response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);	
	}	
	
	@Test
	public void test2_getGroups() throws Exception {  
		RequestBuilder request = setDefaultRequest(get("/user/group"), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(NEWGROUP_1), containsString(NEWGROUP_2), containsString(NEWGROUP_3))));
	}
	
	@Test
	public void test21_readGroupsWithLimitPage() throws Exception {
		Group newGroup = new Group();
		
		newGroup.setName(NEWGROUP_4);
		RequestBuilder request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_4)));
		
		newGroup.setName(NEWGROUP_5);
		request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_5)));
		
		newGroup.setName(NEWGROUP_6);
		request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_6)));	
		
		newGroup.setName(NEWGROUP_7);
		request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_7)));
		
		newGroup.setName(NEWGROUP_8);
		request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_8)));
		
		newGroup.setName(NEWGROUP_9);
		request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_9)));
		
		newGroup.setName(NEWGROUP_10);
		request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_10)));
		
		newGroup.setName(NEWGROUP_11);
		request = setDefaultRequest(post("/user/group"), Scope.USER).content(convertObjectToJsonString(newGroup));
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_11)));		
		
		request = setDefaultRequest(get("/user/group"), Scope.USER).param("pageNum", "1").param("pageSize", "5");
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(NEWGROUP_6), containsString(NEWGROUP_7), containsString(NEWGROUP_8), containsString(NEWGROUP_9), containsString(NEWGROUP_10))));
	}
	
	@Test
	public void test22_readGroupsWithLimitPage() throws Exception {  
		RequestBuilder request = setDefaultRequest(get("/user/group"), Scope.USER).param("pageNum", "2").param("pageSize", "5");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_11)));
	}
	
	@Test
	public void test23_readGroupsWithLimitDate() throws Exception {
		updateGroupsDate();
		Long fromDate = 0L;
		Long toDate = 0L;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");	
		try {
			fromDate = formatter.parse("01-03-2013").getTime();
			toDate = formatter.parse("10-03-2014").getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		RequestBuilder request = setDefaultRequest(get("/user/group"), Scope.USER).param("fromDate", String.valueOf(fromDate)).param("toDate", String.valueOf(toDate));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(NEWGROUP_3), containsString(NEWGROUP_4), containsString(NEWGROUP_5), containsString(NEWGROUP_6), containsString(NEWGROUP_7), containsString(NEWGROUP_8))));
	}
	
	@Test
	public void test24_readGroupsWithLimitDateAndPage() throws Exception {
		Long fromDate = 0L;
		Long toDate = 0L;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");	
		try {
			fromDate = formatter.parse("01-03-2013").getTime();
			toDate = formatter.parse("10-03-2014").getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		RequestBuilder request = setDefaultRequest(get("/user/group"), Scope.USER).param("fromDate", String.valueOf(fromDate)).param("toDate", String.valueOf(toDate)).param("pageNum", "1").param("pageSize", "5");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_8)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test25_readGroupsWithLimitDateFrom() throws Exception {
		Long fromDate = 0L;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");	
		try {
			fromDate = formatter.parse("01-03-2013").getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		RequestBuilder request = setDefaultRequest(get("/user/group"), Scope.USER).param("fromDate", String.valueOf(fromDate));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(NEWGROUP_4), containsString(NEWGROUP_5), containsString(NEWGROUP_6), containsString(NEWGROUP_7), containsString(NEWGROUP_8), containsString(NEWGROUP_9), containsString(NEWGROUP_10), containsString(NEWGROUP_11))));
	}
	
	@Test
	public void test25_readGroupsWithLimitDateTo() throws Exception {
		Long toDate = 0L;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");	
		try {
			toDate = formatter.parse("10-03-2014").getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		RequestBuilder request = setDefaultRequest(get("/user/group"), Scope.USER).param("toDate", String.valueOf(toDate)).param("pageSize", "5");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(NEWGROUP_1), containsString(NEWGROUP_2), containsString(NEWGROUP_3), containsString(NEWGROUP_4), containsString(NEWGROUP_5))));
	}
	
	@Test
	public void test26_readGroupsWithLimitAndSort() throws Exception {
		// sort by name and creationTime
		RequestBuilder request = setDefaultRequest(get("/user/group"), Scope.USER).param("pageNum", "1").param("pageSize", "5").param("sortList", "name").param("sortList", "creationTime");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(NEWGROUP_4), containsString(NEWGROUP_5), containsString(NEWGROUP_6), containsString(NEWGROUP_7), containsString(NEWGROUP_8))));
		
		// sort by lastModifiedTime desc (+ pagination toDate)
		Long toDate = 0L;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");	
		try {
			toDate = formatter.parse("10-03-2014").getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		request = setDefaultRequest(get("/user/group"), Scope.USER).param("toDate", String.valueOf(toDate)).param("pageSize", "5").param("sortList", "lastModifiedTime").param("sortDirection", "1");
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(NEWGROUP_3), containsString(NEWGROUP_2), containsString(NEWGROUP_1), containsString(NEWGROUP_8), containsString(NEWGROUP_7))));
	
		// sort by name desc (+ pagination date in range) 
		updateGroupsDate();
		Long fromDate = 0L;
		toDate = 0L;
		try {
			fromDate = formatter.parse("01-03-2013").getTime();
			toDate = formatter.parse("10-03-2014").getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		request = setDefaultRequest(get("/user/group"), Scope.USER).param("fromDate", String.valueOf(fromDate)).param("toDate", String.valueOf(toDate)).param("sortList", "name").param("sortDirection", "1");
		//request = setDefaultRequest(get("/user/group"), Scope.USER).param("fromDate", String.valueOf(fromDate)).param("toDate", String.valueOf(toDate)).param("sortList", "creatorId").param("sortDirection", "1");
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(NEWGROUP_8), containsString(NEWGROUP_7), containsString(NEWGROUP_6), containsString(NEWGROUP_5), containsString(NEWGROUP_4), containsString(NEWGROUP_3))));
	}
	
	@Test
	public void test27_readGroupsWithLimitAndSortErrParam() throws Exception {
		// sort by name and creationTime
		RequestBuilder request = setDefaultRequest(get("/user/group"), Scope.USER).param("pageNum", "1").param("pageSize", "5").param("sortList", "name").param("sortList", "creation_time");
		ResultActions response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
	}
	
	@Test
	public void test3_readGroup() throws Exception {  
		RequestBuilder request = setDefaultRequest(get("/user/group/{id}", GROUP_ID_1), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(NEWGROUP_1)));
	}
	
	@Test
	public void test31_readGroupVoidParams() throws Exception {  
		String groupId = null;
		RequestBuilder request = setDefaultRequest(get("/user/group/{id}", groupId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response);
	}
	
	@Test
	public void test32_readGroupNotExists() throws Exception {  
		RequestBuilder request = setDefaultRequest(get("/user/group/{id}", GROUP_NEX_ID), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setNullResult(response);
	}
	
	@Test
	public void test4_updateGroup() throws Exception { 
		Group editGroup = new Group();
		editGroup.setName(EDITGROUP_1);
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}", GROUP_ID_2), Scope.USER).content(convertObjectToJsonString(editGroup));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString(EDITGROUP_1)));
	}
	
	@Test
	public void test41_updateGroupVoidParam() throws Exception {
		String groupId = null;
		Group editGroup = new Group();
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}", groupId), Scope.USER).content(convertObjectToJsonString(editGroup));
		ResultActions response = mockMvc.perform(request);
		setForbiddenExceptionResult(response);
	}
	
	@Test
	public void test42_updateGroupNotExists() throws Exception {
		Group editGroup = new Group();
		editGroup.setName(EDITGROUP_1);
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}", GROUP_NEX_ID), Scope.USER).content(convertObjectToJsonString(editGroup));
		ResultActions response = mockMvc.perform(request);
		setNullResult(response);
	}
	
	@Test
	public void test43_updateGroupAlreadyExists() throws Exception {
		Group editGroup = new Group();
		editGroup.setName(EDITGROUP_2);
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}", GROUP_ID_2), Scope.USER).content(convertObjectToJsonString(editGroup));
		ResultActions response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
	}	
	
	@Test
	public void test5_addGroupMembers() throws Exception { 
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}/members", GROUP_ID_2), Scope.USER).param("userIds", GROUP_MEMB_1).param("userIds", GROUP_MEMB_2).param("userIds", GROUP_MEMB_3);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
	}
	
	@Test
	public void test51_addGroupMembersNullList() throws Exception { 
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}/members", GROUP_ID_2), Scope.USER).param("userIds", "").param("userIds", "");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
	}
	
	@Test
	public void test52_addGroupMembersNullGroup() throws Exception {
		String groupId = null;
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}/members", groupId), Scope.USER).param("userIds", GROUP_MEMB_1).param("userIds", GROUP_MEMB_2).param("userIds", GROUP_MEMB_3);
		ResultActions response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
	}
	
	@Test
	public void test53_addGroupMembersNotExistGroup() throws Exception {
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}/members", GROUP_NEX_ID), Scope.USER).param("userIds", GROUP_MEMB_1).param("userIds", GROUP_MEMB_2).param("userIds", GROUP_MEMB_3);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
	}
	
	@Test
	public void test6_readGroupMembers() throws Exception { 
		RequestBuilder request = setDefaultRequest(get("/user/group/{id}/members", GROUP_ID_2), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(GROUP_MEMB_1), containsString(GROUP_MEMB_2), containsString(GROUP_MEMB_3))));
	}
	
	@Test
	public void test61_readGroupMembersVoidList() throws Exception { 
		RequestBuilder request = setDefaultRequest(get("/user/group/{id}/members", GROUP_ID_3), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("[ ]")));
	}	

	@Test
	public void test62_readGroupMembersNullParam() throws Exception { 
		String groupId = null;
		RequestBuilder request = setDefaultRequest(get("/user/group/{id}/members", groupId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
	}
	
	@Test
	public void test63_readGroupMembersNoGroupExist() throws Exception { 
		RequestBuilder request = setDefaultRequest(get("/user/group/{id}/members", GROUP_NEX_ID), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("[ ]")));
	}
	
	@Test
	public void test64_readGroupMembersPageLimitAndSort() throws Exception {
		//Add 3 other members to group2
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}/members", GROUP_ID_2), Scope.USER).param("userIds", "45").param("userIds", "46").param("userIds", "47");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
		
		request = setDefaultRequest(get("/user/group/{id}/members", GROUP_ID_2), Scope.USER).param("pageSize", "5");
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(GROUP_MEMB_1), containsString(GROUP_MEMB_2), containsString(GROUP_MEMB_3), containsString("45"), containsString("46"))));
		
		request = setDefaultRequest(get("/user/group/{id}/members", GROUP_ID_2), Scope.USER).param("pageNum", "1").param("pageSize", "5");
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("47")));
		
		// sort test - asc members order
		request = setDefaultRequest(get("/user/group/{id}/members", GROUP_ID_2), Scope.USER).param("pageSize", "5").param("sortList", "userId");
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString(GROUP_MEMB_1), containsString(GROUP_MEMB_2), containsString(GROUP_MEMB_3), containsString("45"), containsString("46"))));
		
		// sort test - desc members order
		request = setDefaultRequest(get("/user/group/{id}/members", GROUP_ID_2), Scope.USER).param("pageSize", "5").param("sortList", "userId").param("sortDirection", "1");
		response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(allOf(containsString("47"), containsString("46"), containsString("45"), containsString(GROUP_MEMB_3), containsString(GROUP_MEMB_2))));
		
		// sort test - param error
		request = setDefaultRequest(get("/user/group/{id}/members", GROUP_ID_2), Scope.USER).param("pageSize", "5").param("sortList", "memberId");
		response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
	}	
	
	@Test
	public void test7_deleteGroupMembers() throws Exception { 
		RequestBuilder request = setDefaultRequest(delete("/user/group/{id}/members", GROUP_ID_2), Scope.USER).param("userIds", GROUP_MEMB_1).param("userIds", GROUP_MEMB_2);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
	}
	
	@Test
	public void test71_deleteGroupMembersNullList() throws Exception {
		RequestBuilder request = setDefaultRequest(delete("/user/group/{id}/members", GROUP_ID_2), Scope.USER).param("userIds", "").param("userIds", "");
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
	}	
	
	@Test
	public void test72_deleteGroupMembersNullGroup() throws Exception { 
		String groupId = null;
		RequestBuilder request = setDefaultRequest(delete("/user/group/{id}/members", groupId), Scope.USER).param("userIds", GROUP_MEMB_1).param("userIds", GROUP_MEMB_2);
		ResultActions response = mockMvc.perform(request);
		setIllegalArgumentExceptionResult(response);
	}
	
	@Test
	public void test73_deleteGroupMembersNotExistGroup() throws Exception { 
		RequestBuilder request = setDefaultRequest(delete("/user/group/{id}/members", GROUP_NEX_ID), Scope.USER).param("userIds", GROUP_MEMB_1).param("userIds", GROUP_MEMB_2);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
	}
	
	@Test
	public void test74_deleteGroupMembersNotExistUser() throws Exception { 
		RequestBuilder request = setDefaultRequest(delete("/user/group/{id}/members", GROUP_NEX_ID), Scope.USER).param("userIds", GROUP_MEMB_4);
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
	}		
	
	@Test
	public void test8_deleteGroup() throws Exception {
		// Delete first group
		RequestBuilder request = setDefaultRequest(delete("/user/group/{id}", GROUP_ID_1), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		response = setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
		
		// Delete first group
		request = setDefaultRequest(delete("/user/group/{id}", GROUP_ID_2), Scope.USER);
		response = mockMvc.perform(request);
		response = setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
		
		// Delete first group
		request = setDefaultRequest(delete("/user/group/{id}", GROUP_ID_3), Scope.USER);
		response = mockMvc.perform(request);
		response = setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
		
		// Delete all the other groups
		for (int i = 4; i < 11; i++){
			request = setDefaultRequest(delete("/user/group/{id}", String.valueOf(i)), Scope.USER);
			response = mockMvc.perform(request);
			response = setDefaultResult(response)
					.andExpect(content().string(containsString("true")));
		}
	}	
	
	@Test
	public void test81_deleteGroupNotExists() throws Exception {
		// Delete not existing group
		RequestBuilder request = setDefaultRequest(delete("/user/group/{id}", GROUP_ID_1), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		response = setDefaultResult(response)
		.andExpect(content().string(containsString("true")));
	}
	
	@Test
	public void test82_deleteGroupNullParam() throws Exception {
		String groupId = null;
		RequestBuilder request = setDefaultRequest(delete("/user/group/{id}", groupId), Scope.USER);
		ResultActions response = mockMvc.perform(request);
		setForbiddenExceptionResult(response);
	}
	
	private boolean updateGroupsDate()  throws Exception{
		boolean updated = true;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		
		RequestBuilder request = setDefaultRequest(put("/user/group/{id}/test", GROUP_ID_1), Scope.USER).param("updateTime", String.valueOf(formatter.parse("01-01-2013").getTime()));
		ResultActions response = mockMvc.perform(request);
		setDefaultResult(response);
		
		request = setDefaultRequest(put("/user/group/{id}/test", GROUP_ID_2), Scope.USER).param("updateTime", String.valueOf(formatter.parse("01-02-2013").getTime()));
		response = mockMvc.perform(request);
		setDefaultResult(response);
		
		request = setDefaultRequest(put("/user/group/{id}/test", GROUP_ID_3), Scope.USER).param("updateTime", String.valueOf(formatter.parse("01-03-2013").getTime()));
		response = mockMvc.perform(request);
		setDefaultResult(response);
		
		request = setDefaultRequest(put("/user/group/{id}/test", GROUP_ID_9), Scope.USER).param("updateTime", String.valueOf(formatter.parse("01-05-2014").getTime()));
		response = mockMvc.perform(request);
		setDefaultResult(response);
		
		request = setDefaultRequest(put("/user/group/{id}/test", GROUP_ID_10), Scope.USER).param("updateTime", String.valueOf(formatter.parse("01-06-2014").getTime()));
		response = mockMvc.perform(request);
		setDefaultResult(response);
		
		request = setDefaultRequest(put("/user/group/{id}/test", GROUP_ID_11), Scope.USER).param("updateTime", String.valueOf(formatter.parse("01-07-2014").getTime()));
		response = mockMvc.perform(request);
		setDefaultResult(response);
		
		return updated;
	}
	
	

}
