package eu.trentorise.smartcampus.social.managers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import eu.trentorise.smartcampus.social.engine.model.SocialUser;
import eu.trentorise.smartcampus.social.engine.repo.GroupRepository;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class SocialGroupManagerTest {
	
	@Autowired
	SocialGroupManager groupManager;
	
	@Autowired
	GroupRepository groupRepository;

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
	
	private Group group, group2, group3, group4, group5, group6, group7, group8, group9, group10, group11, group21, group22, group_exist;
	private Limit limit = null;
	private Group readedGroup = null;

	private boolean checkGroupCreation(String name, Group group) {

		return group != null && group.getId() != null
				&& group.getCreationTime() > 0
				&& group.getLastModifiedTime() > 0
				&& RepositoryUtils.normalizeCompare(group.getName(), name);
	}
	
	public void changeCreationTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		try {
			group = groupManager.update(group.getId(), formatter.parse("01-01-2013").getTime());
			group2 = groupManager.update(group2.getId(), formatter.parse("01-02-2013").getTime());
			group3 = groupManager.update(group3.getId(), formatter.parse("01-03-2013").getTime());
			group9 = groupManager.update(group9.getId(), formatter.parse("01-03-2014").getTime());
			group10 = groupManager.update(group10.getId(), formatter.parse("01-04-2014").getTime());
			group11 = groupManager.update(group11.getId(), formatter.parse("01-05-2014").getTime());		
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void createAll(){
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
	public void readGroups() {
		limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);
		
		// read : read group 1
		Group readedGroup = groupManager.readGroup(group.getId());
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_1, readedGroup) && group.getId().equals(readedGroup.getId()));	
		// read : read all grups created from user "30" using pagination ( page 0, page_size 5 ) => I obtain the first five groups from 0 to 4
		Assert.assertTrue(groupManager.readGroups(USER_ID, limit).size() == 5);
		
		limit.setPage(2);
		List<Group> page_readed_group = groupManager.readGroups(USER_ID, limit);
		// read : read all grups created from user "30" using pagination ( page 2, page_size 5 ) => I obtain the last group from 10 to 10
		Assert.assertTrue(page_readed_group.size() == 1 && page_readed_group.get(0).getName().compareTo(GROUP_NAME_11) == 0);
		
		// read with time pagination
		changeCreationTime();
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");	
		try {
			Long fromDate = formatter.parse("01-03-2013").getTime();
			Long toDate = formatter.parse("15-02-2014").getTime();
			limit.setPage(1);
			limit.setFromDate(fromDate);
			limit.setToDate(toDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		page_readed_group = groupManager.readGroups(USER_ID, limit);
		// read : read all grups created from user "30" using pagination ( page 1, page_size 5 ) created between 01-03-2013 and 15-02-2014 => I obtain only the group "group8"
		Assert.assertTrue(page_readed_group.size() == 1 && page_readed_group.get(0).getName().compareTo(GROUP_NAME_8) == 0);
		
		try {
			Long fromDate = formatter.parse("01-02-2014").getTime();
			Long toDate = 0L;
			limit.setPage(1);
			limit.setFromDate(fromDate);
			limit.setToDate(toDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		page_readed_group = groupManager.readGroups(USER_ID, limit);
		String group_name_1 = page_readed_group.get(0).getName();
		// read : read all grups created from user "30" using pagination ( page 1, page_size 5 ) created after 01-02-2014 => I obtain three groups from 8 to 10
		Assert.assertTrue(page_readed_group.size() == 3 && group_name_1.compareTo(GROUP_NAME_9) == 0);
		
		try {
			Long fromDate = 0L;
			Long toDate = formatter.parse("10-02-2014").getTime();
			limit.setPage(0);
			limit.setFromDate(fromDate);
			limit.setToDate(toDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		page_readed_group = groupManager.readGroups(USER_ID, limit);
		group_name_1 = page_readed_group.get(2).getName();
		// read : read all grups created from user "30" using pagination ( page 0, page_size 5 ) created before 10-02-2014 => I obtain three groups from 0 to 2
		Assert.assertTrue(page_readed_group.size() == 3 && group_name_1.compareTo(GROUP_NAME_2) == 0);
		
	}
	
	@Test
	public void groupMembers() {
		limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);
		
		// add members
		Set<String> members = new HashSet<String>();
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
		Assert.assertTrue("Size list error: " + group_members.size(), group_members.size() == 5); // && group_members.containsAll(members_inserted)
		
		limit.setPage(1);
		group_members = groupManager.readMembers(group.getId(), limit);
		Assert.assertTrue(group_members.size() == 1 && group_members.get(0).getId().compareTo(MEMBER_ID_6) == 0); 
		
		// read members as String
		limit.setPage(0);
		List<String> group_members_string = new ArrayList<String>();
		List<String> members_inserted_string = new ArrayList<String>();
		
		group_members_string = groupManager.readMembersAsString(group.getId(), limit);
		for (String s : members) {
			members_inserted_string.add(s);
		}
		Assert.assertTrue("Size list error: " + group_members_string.size(), group_members_string.size() == 5); // && group_members.containsAll(members_inserted)
		
		limit.setPage(1);
		group_members_string = groupManager.readMembersAsString(group.getId(), limit);
		Assert.assertTrue(group_members_string.size() == 1 && group_members_string.get(0).compareTo(MEMBER_ID_6) == 0); 		

		// check members
		readedGroup = groupManager.readGroup(group.getId());
		Assert.assertTrue(members.containsAll(readedGroup.getMembers()));

		// delete members
		Set<String> delete_members = new HashSet<String>();
		delete_members.add(MEMBER_ID_1);
		delete_members.add(MEMBER_ID_2);
		Assert.assertTrue(groupManager.removeMembers(group.getId(), delete_members));
		
		limit.setPage(0);
		group_members = groupManager.readMembers(group.getId(), limit);
		Assert.assertTrue(group_members.size() == 4 && group_members.get(0).getId().compareTo(MEMBER_ID_3) == 0);
		
		group_members_string = groupManager.readMembersAsString(group.getId(), limit);
		Assert.assertTrue(group_members_string.size() == 4 && group_members_string.get(0).compareTo(MEMBER_ID_3) == 0);
	}
	
	@Test
	public void updateGroups() {
		// update
		readedGroup = groupManager.update(group.getId(), GROUP_RENAME);
		Assert.assertTrue(checkGroupCreation(GROUP_RENAME, readedGroup));
		
		readedGroup = groupManager.update(group2.getId(), GROUP_RENAME_2);
		Assert.assertTrue(checkGroupCreation(GROUP_RENAME_2, readedGroup));
	}
	
	@Test
	public void extremeCases() {
		limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);
		
		// Create a group that already exist - now it allows user to create a group that already exist 
		group_exist = groupManager.create(USER_ID, GROUP_NAME_1);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_1, group_exist));
		Assert.assertTrue(group.getId().compareTo(group_exist.getId()) == 0);	//Ask to Raman
		//Assert.assertFalse(group.getId().compareTo(group_exist.getId()) == 0);	//Ask to Raman
		Assert.assertTrue(groupManager.delete(group_exist.getId()));
		
		
		// Read groups from a creator that not exists
		List<Group> page_readed_group  = groupManager.readGroups(USER_ID_3, limit);
		Assert.assertTrue(page_readed_group == null);
		
		// Read a group that not exists
		readedGroup = groupManager.readGroup(GROUP_NEX_ID);
		Assert.assertTrue(readedGroup == null);
		
		// Read members from a non existing group
		List<User> members = groupManager.readMembers(GROUP_NEX_ID, limit);
		Assert.assertTrue(members == null);
		
		// Read members from a group that has no members
		members = groupManager.readMembers(group4.getId(), limit);
		Assert.assertTrue(members == null);
		
		// Update a group that not exists
		readedGroup = groupManager.update(group_exist.getId(), GROUP_NAME_2);
		Assert.assertTrue(readedGroup == null);
		
		// Add more times the same member
		Set<String> add_members = new HashSet<String>();
		add_members.add(MEMBER_ID_1);
		add_members.add(MEMBER_ID_2);
		add_members.add(MEMBER_ID_3);
		add_members.add(MEMBER_ID_1);
		add_members.add(MEMBER_ID_2);
		add_members.add(MEMBER_ID_3);
		Assert.assertTrue(groupManager.addMembers(group5.getId(), add_members));
		Assert.assertTrue(groupManager.readMembersAsString(group5.getId(), limit).size() == 3);
		
		// Add the same member to another group
		Set<String> old_members = new HashSet<String>();
		old_members.add(MEMBER_ID_1);
		old_members.add(MEMBER_ID_2);
		Assert.assertTrue(groupManager.addMembers(group2.getId(), old_members));
		Assert.assertTrue(groupManager.readMembersAsString(group2.getId(), limit).size() == 2);
		
		// Remove a non existing member
		// delete members
		Set<String> delete_members = new HashSet<String>();
		delete_members.add(MEMBER_ID_1);
		delete_members.add(MEMBER_ID_4);
		Assert.assertTrue(groupManager.removeMembers(group5.getId(), delete_members));
		Assert.assertTrue(groupManager.readMembersAsString(group5.getId(), limit).size() == 2);
			
		// Delete a non existing group
		Assert.assertTrue(groupManager.delete(group_exist.getId()));
	}
	
	@After
	public void deleteGroups() {
		
		// delete
		Assert.assertTrue(groupManager.delete(group.getId()));
		Assert.assertTrue(groupManager.delete(group2.getId()));
		Assert.assertTrue(groupManager.delete(group3.getId()));
		Assert.assertTrue(groupManager.delete(group4.getId()));
		Assert.assertTrue(groupManager.delete(group5.getId()));
		Assert.assertTrue(groupManager.delete(group6.getId()));
		Assert.assertTrue(groupManager.delete(group7.getId()));
		Assert.assertTrue(groupManager.delete(group8.getId()));
		Assert.assertTrue(groupManager.delete(group9.getId()));
		Assert.assertTrue(groupManager.delete(group10.getId()));
		Assert.assertTrue(groupManager.delete(group11.getId()));
		
		Assert.assertTrue(groupManager.delete(group21.getId()));
		Assert.assertTrue(groupManager.delete(group22.getId()));
	}

}
