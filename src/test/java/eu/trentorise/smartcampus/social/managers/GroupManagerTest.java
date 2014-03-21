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

package eu.trentorise.smartcampus.social.managers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.beans.User;
import eu.trentorise.smartcampus.social.engine.repo.GroupRepository;
import eu.trentorise.smartcampus.social.engine.repo.UserRepository;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml",
		"classpath:/spring/spring-security.xml" })
public class GroupManagerTest {

	@Autowired
	GroupManager groupManager;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	UserRepository userRepository;

	private static final String USER_ID = "30";
	private static final String USER_ID_2 = "40";
	private static final String USER_ID_3 = "50";
	private static final String MEMBER_ID_1 = "31";
	private static final String MEMBER_ID_2 = "32";
	private static final String MEMBER_ID_3 = "33";
	private static final String MEMBER_ID_4 = "34";
	private static final String MEMBER_ID_5 = "35";
	private static final String MEMBER_ID_6 = "36";

	private static final String GROUP_NAME_1 = "friends";
	private static final String GROUP_NAME_2 = "group2";
	private static final String GROUP_NAME_3 = "collaborators";
	private static final String GROUP_NAME_4 = "group4";
	private static final String GROUP_NAME_5 = "group5";
	private static final String GROUP_NAME_6 = "group6";
	private static final String GROUP_NAME_7 = "group7";
	private static final String GROUP_NAME_8 = "group8";
	private static final String GROUP_NAME_9 = "group9";
	private static final String GROUP_NAME_10 = "group10";
	private static final String GROUP_NAME_11 = "group11";

	private static final String GROUP_NAME_21 = "party";
	private static final String GROUP_NAME_22 = "sport";

	private static final String GROUP_RENAME = "collegues";
	private static final String GROUP_RENAME_2 = "players";

	private static final String GROUP_NEX_ID = "123456";
	private static final long THREE_MONTHS = 7889231490L;
	private static final long TWO_MONTHS = 5259487660L;
	private static final long ONE_MONTH = 2629743830L;

	private Group group, group2, group3, group4, group5, group6, group7,
			group8, group9, group10, group11, group21, group22;
	private Limit limit = null;
	private Group readedGroup = null;
	List<Group> readedGroups = null;
	private long now = 0L;

	private boolean checkGroupCreation(String name, Group group) {

		return group != null && group.getId() != null
				&& group.getCreationTime() > 0
				&& group.getLastModifiedTime() > 0
				&& RepositoryUtils.normalizeCompare(group.getName(), name);
	}

	public void changeCreationTime() {
		group = groupManager.update(group.getId(),
				now - THREE_MONTHS);
		group2 = groupManager.update(group2.getId(),
				now - TWO_MONTHS);
		group3 = groupManager.update(group3.getId(),
				now - ONE_MONTH);
		group9 = groupManager.update(group9.getId(),
				now + ONE_MONTH);
		group10 = groupManager.update(group10.getId(),
				now + TWO_MONTHS);
		group11 = groupManager.update(group11.getId(),
				now + THREE_MONTHS);
	}

	@Before
	public void createAll() {
		now = System.currentTimeMillis();
		group = groupManager.create(USER_ID, GROUP_NAME_1);
		group2 = groupManager.create(USER_ID, GROUP_NAME_3);
		group3 = groupManager.create(USER_ID, GROUP_NAME_2);
		group4 = groupManager.create(USER_ID, GROUP_NAME_4);
		group5 = groupManager.create(USER_ID, GROUP_NAME_5);
		group6 = groupManager.create(USER_ID, GROUP_NAME_6);
		group7 = groupManager.create(USER_ID, GROUP_NAME_7);
		group8 = groupManager.create(USER_ID, GROUP_NAME_8);
		group9 = groupManager.create(USER_ID, GROUP_NAME_9);
		group10 = groupManager.create(USER_ID, GROUP_NAME_10);
		group11 = groupManager.create(USER_ID, GROUP_NAME_11);

		group21 = groupManager.create(USER_ID_2, GROUP_NAME_21);
		group22 = groupManager.create(USER_ID_2, GROUP_NAME_22);
	}
	
	@After
	public void cleanup() {
		groupRepository.deleteAll();
		// userRepository.deleteAll();
	}	

	private void createSame() {
		long now = System.currentTimeMillis();
		Group g = groupManager.create(USER_ID, "group1");
		groupManager.update(g.getId(), now - TWO_MONTHS);

		g = groupManager.create(USER_ID, "group2");
		groupManager.update(g.getId(), now - ONE_MONTH);

		g = groupManager.create(USER_ID, "group3");
		groupManager.update(g.getId(), now - ONE_MONTH);

		g = groupManager.create(USER_ID, "group4");

		g = groupManager.create(USER_ID, "group5");
		groupManager.update(g.getId(), now + TWO_MONTHS);
	}

	@Test
	public void createGroup() {
		// create
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_1, group));
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_3, group2));
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_2, group3));
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_4, group4));
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_5, group5));
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_6, group6));
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_7, group7));
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_8, group8));
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_9, group9));
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_10, group10));
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_11, group11));

		Assert.assertTrue(checkGroupCreation(GROUP_NAME_21, group21));
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_22, group22));
	}

	@Test
	public void readGroups() throws ParseException {
		cleanup();
		createSame();

		limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);
		Assert.assertEquals(5, groupManager.readGroups(USER_ID, limit).size());

		limit = new Limit();
		limit.setPageSize(2);
		limit.setPage(2);
		readedGroups = groupManager.readGroups(USER_ID, limit);
		Assert.assertEquals(1, readedGroups.size());
		Assert.assertEquals("group5", readedGroups.get(0).getName());

		limit = new Limit();
		limit.setPage(1);
		limit.setPageSize(2);
		limit.setFromDate(now - ONE_MONTH);
		limit.setToDate(now + ONE_MONTH);
		readedGroups = groupManager.readGroups(USER_ID, limit);
		Assert.assertEquals(1, readedGroups.size());

		limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);
		limit.setFromDate(now);
		readedGroups = groupManager.readGroups(USER_ID, limit);
		Assert.assertEquals(2, readedGroups.size());

		limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(3);
		limit.setToDate(now + ONE_MONTH);
		readedGroups = groupManager.readGroups(USER_ID, limit);
		Assert.assertEquals(3, readedGroups.size());

	}

	@Test
	public void readGroupsSort() throws ParseException {
		changeCreationTime();
		limit = new Limit();
		// with sort (by name and creation time asc)
		limit.setDirection(0);
		limit.setSortList(Arrays.asList("name", "creationTime"));

		readedGroups = groupManager.readGroups(USER_ID, limit);
		Assert.assertEquals(11, readedGroups.size());
		Assert.assertEquals("collaborators", readedGroups.get(0).getName());

		limit = new Limit();
		limit.setPageSize(5);
		limit.setPage(2);
		limit.setDirection(1);
		limit.setSortList(Arrays.asList("name", "creationTime"));
		readedGroups = groupManager.readGroups(USER_ID, limit);
		Assert.assertEquals(1, readedGroups.size());
		Assert.assertEquals("collaborators", readedGroups.get(0).getName());

		limit = new Limit();
		limit.setPageSize(5);
		limit.setPage(1);
		limit.setFromDate(now);
		limit.setToDate(now + THREE_MONTHS);
		// with sort (by name desc)
		limit.setDirection(1);
		limit.setSortList(Arrays.asList("name"));

		readedGroups = groupManager.readGroups(USER_ID, limit);
		Assert.assertEquals(3, readedGroups.size());
		Assert.assertEquals("group4", readedGroups.get(0).getName());

		limit = new Limit();
		limit.setPageSize(5);
		limit.setPage(0);
		limit.setToDate(now);
		// with sort (by creationTime desc)
		limit.setDirection(1);
		limit.setSortList(Arrays.asList("creationTime"));

		readedGroups = groupManager.readGroups(USER_ID, limit);
		Assert.assertEquals(3, readedGroups.size());
		Assert.assertEquals("group2", readedGroups.get(0).getName());
	}

	@Test
	public void groupMembers() {
		limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);

		// test add/remove/add same user
		Set<String> members = new HashSet<String>();
		members.add(MEMBER_ID_1);
		Assert.assertTrue(groupManager.addMembers(group.getId(), members));
		Assert.assertTrue(groupManager.removeMembers(group.getId(), members));
		Assert.assertTrue(groupManager.addMembers(group.getId(), members));

		// add members
		members.clear();
		members.add(MEMBER_ID_1);
		members.add(MEMBER_ID_2);
		members.add(MEMBER_ID_3);
		members.add(MEMBER_ID_4);
		members.add(MEMBER_ID_5);
		members.add(MEMBER_ID_6);
		Assert.assertTrue(groupManager.addMembers(group.getId(), members));

		// read members as Users
		List<User> group_members = new ArrayList<User>();
		List<User> members_inserted = new ArrayList<User>();

		group_members = groupManager.readMembers(group.getId(), limit);
		for (String s : members) {
			members_inserted.add(new User(s));
		}
		Assert.assertTrue("Size list error: " + group_members.size(),
				group_members.size() == 5);

		limit.setPage(1);
		group_members = groupManager.readMembers(group.getId(), limit);
		Assert.assertTrue(group_members.size() == 1
				&& group_members.get(0).getId().compareTo(MEMBER_ID_4) == 0);

		// read members as String
		limit.setPage(0);
		List<String> group_members_string = new ArrayList<String>();
		List<String> members_inserted_string = new ArrayList<String>();

		group_members_string = groupManager.readMembersAsString(group.getId(),
				limit);
		for (String s : members) {
			members_inserted_string.add(s);
		}
		Assert.assertTrue("Size list error: " + group_members_string.size(),
				group_members_string.size() == 5); // &&
													// group_members.containsAll(members_inserted)

		limit.setPage(1);
		group_members_string = groupManager.readMembersAsString(group.getId(),
				limit);
		Assert.assertTrue(group_members_string.size() == 1
				&& group_members_string.get(0).compareTo(MEMBER_ID_4) == 0);

		// check members
		readedGroup = groupManager.readGroup(group.getId());
		Assert.assertTrue(members.containsAll(readedGroup.getMembers()));

		// delete members
		Set<String> delete_members = new HashSet<String>();
		delete_members.add(MEMBER_ID_1);
		delete_members.add(MEMBER_ID_2);
		Assert.assertTrue(groupManager.removeMembers(group.getId(),
				delete_members));

		// sort list
		limit.setPage(0);
		List<String> parameters = new ArrayList<String>();
		parameters.add("id");
		limit.setSortList(parameters);
		group_members = groupManager.readMembers(group.getId(), limit);
		Assert.assertTrue(group_members.size() == 4
				&& group_members.get(0).getId().compareTo(MEMBER_ID_3) == 0);

		// reverse sort
		limit = new Limit();
		limit.setDirection(1);
		limit.setSortList(Collections.<String> emptyList()); // to sort a
																// primitive
																// type class
																// sortlist must
																// be a empty
																// array value
		group_members_string = groupManager.readMembersAsString(group.getId(),
				limit);
		Assert.assertTrue(group_members_string.size() == 4
				&& group_members_string.get(3).compareTo(MEMBER_ID_3) == 0);
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void groupMembersSortErrParam() {
		// set the limit error
		limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);
		List<String> parameters = new ArrayList<String>();
		parameters.add("member"); // add a wrong parameter name
		limit.setSortList(parameters);

		// add members
		Set<String> members = new HashSet<String>();
		members.add(MEMBER_ID_1);
		members.add(MEMBER_ID_2);
		members.add(MEMBER_ID_3);
		members.add(MEMBER_ID_4);
		members.add(MEMBER_ID_5);
		members.add(MEMBER_ID_6);
		Assert.assertTrue(groupManager.addMembers(group.getId(), members));

		groupManager.readMembers(group.getId(), limit);
	}

	@Test
	public void updateGroups() {
		// update
		readedGroup = groupManager.update(group.getId(), GROUP_RENAME);
		Assert.assertTrue(checkGroupCreation(GROUP_RENAME, readedGroup));

		readedGroup = groupManager.update(group2.getId(), GROUP_RENAME_2);
		Assert.assertTrue(checkGroupCreation(GROUP_RENAME_2, readedGroup));

	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void updateGroupsSameNameException() {
		// update a group with the same name of another group of the same user
		readedGroup = groupManager.update(group2.getId(), GROUP_NAME_1);
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void createGroupAlreadyExistException() {
		// Create a group that already exist
		readedGroup = groupManager.create(USER_ID, GROUP_NAME_1);
	}

	@Test
	public void updateGroupNotExist() {
		// Update a group that not exists
		readedGroup = groupManager.update(GROUP_NEX_ID, GROUP_NAME_2);
		Assert.assertTrue(readedGroup == null);
	}

	@Test
	public void deleteGroupNotExist() {
		// Delete a not existing group
		Assert.assertTrue(groupManager.delete(GROUP_NEX_ID));
	}

	@Test
	public void addMemberToGroupNotExist() {
		Set<String> add_members = new HashSet<String>();
		add_members.add(MEMBER_ID_1);
		// Add a member to a not existing group
		Assert.assertTrue(groupManager.addMembers(GROUP_NEX_ID, add_members));

	}

	@Test
	public void removeMemberToGroupNotExist() {
		Set<String> rem_members = new HashSet<String>();
		rem_members.add(MEMBER_ID_1);
		// Remove a member from a not existing group
		Assert.assertTrue(groupManager.addMembers(GROUP_NEX_ID, rem_members));
	}

	@Test
	public void extremeCases() {
		limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);

		// Update the same group with the same name
		readedGroup = groupManager.update(group.getId(), GROUP_NAME_1);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_1, readedGroup));

		// Read groups from a creator that not exists
		readedGroups = groupManager.readGroups(USER_ID_3, limit);
		Assert.assertTrue(readedGroups.size() == 0);

		// Read a group that not exists
		readedGroup = groupManager.readGroup(GROUP_NEX_ID);
		Assert.assertTrue(readedGroup == null);

		// Read members from a non existing group
		List<User> members = groupManager.readMembers(GROUP_NEX_ID, limit);
		Assert.assertTrue(members.isEmpty());

		// Read members from a group that has no members
		members = groupManager.readMembers(group4.getId(), limit);
		Assert.assertTrue(members.isEmpty());

		// Add more times the same member
		Set<String> add_members = new HashSet<String>();
		add_members.add(MEMBER_ID_1);
		add_members.add(MEMBER_ID_2);
		add_members.add(MEMBER_ID_3);
		add_members.add(MEMBER_ID_1);
		add_members.add(MEMBER_ID_2);
		add_members.add(MEMBER_ID_3);
		Assert.assertTrue(groupManager.addMembers(group5.getId(), add_members));
		Assert.assertTrue(groupManager.readMembersAsString(group5.getId(),
				limit).size() == 3);

		// Add the same member to another group
		Set<String> old_members = new HashSet<String>();
		old_members.add(MEMBER_ID_1);
		old_members.add(MEMBER_ID_2);
		Assert.assertTrue(groupManager.addMembers(group2.getId(), old_members));
		Assert.assertTrue(groupManager.readMembersAsString(group2.getId(),
				limit).size() == 2);

		// Remove a non existing member
		Set<String> delete_members = new HashSet<String>();
		delete_members.add(MEMBER_ID_1);
		delete_members.add(MEMBER_ID_4);
		Assert.assertTrue(groupManager.removeMembers(group5.getId(),
				delete_members));
		Assert.assertTrue(groupManager.readMembersAsString(group5.getId(),
				limit).size() == 2);

	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void createGroupErrParamsException() {
		readedGroup = groupManager.create(null, "");
	}

	@Test
	public void readGroupErrParams() {
		readedGroup = groupManager.readGroup(null);
		Assert.assertTrue(readedGroup == null);
	}

	@Test
	public void readGroupsErrParams() {
		readedGroups = groupManager.readGroups("", limit);
		Assert.assertTrue(readedGroups.isEmpty());
	}

	@Test
	public void readGroupMembersErrParams() {
		List<User> members = null;
		members = groupManager.readMembers(null, limit);
		Assert.assertTrue(members.isEmpty());
	}

	@Test
	public void readGroupMembersAsStringErrParams() {
		List<String> members = null;
		members = groupManager.readMembersAsString("", limit);
		Assert.assertTrue(members.isEmpty());
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void updateGroupErrParamsException() {
		groupManager.update(null, "");
	}

	@Test
	public void addGroupMembersErrParams() {
		Set<String> addMembers = new HashSet<String>();
		Assert.assertTrue(groupManager.addMembers(null, addMembers));
	}

	@Test
	public void removeGroupMembersErrParamsException() {
		Assert.assertTrue(groupManager.removeMembers("", null));
	}

	@Test
	public void deleteGroupErrParamsException() {
		Assert.assertTrue(groupManager.delete(null));
	}

}
