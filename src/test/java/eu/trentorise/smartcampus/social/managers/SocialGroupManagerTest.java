package eu.trentorise.smartcampus.social.managers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class SocialGroupManagerTest {
	
	@Autowired
	SocialGroupManager groupManager;

	private static final String USER_ID = "30";
	private static final String MEMBER_ID_1 = "31";
	private static final String MEMBER_ID_2 = "32";
	private static final String MEMBER_ID_3 = "33";
	private static final String MEMBER_ID_4 = "34";
	private static final String MEMBER_ID_5 = "35";
	private static final String MEMBER_ID_6 = "36";

	private static final String GROUP_NAME_1 = "friends";
	private static final String GROUP_RENAME = "collegues";
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

	private boolean checkGroupCreation(String name, Group group) {

		return group != null && group.getId() != null
				&& group.getCreationTime() > 0
				&& group.getLastModifiedTime() > 0
				&& group.getName().equals(name);
	}

	@Test
	public void crudOnGroup() {
		
		Limit limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);
		
		// create
		Group group = groupManager.create(USER_ID, GROUP_NAME_1);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_1, group));
		
		Group group2 = groupManager.create(USER_ID, GROUP_NAME_3);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_3, group2));
		
		Group group3 = groupManager.create(USER_ID, GROUP_NAME_2);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_2, group3));
		
		Group group4 = groupManager.create(USER_ID, GROUP_NAME_4);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_4, group4));
		
		Group group5 = groupManager.create(USER_ID, GROUP_NAME_5);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_5, group5));
		
		Group group6 = groupManager.create(USER_ID, GROUP_NAME_6);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_6, group6));
		
		Group group7 = groupManager.create(USER_ID, GROUP_NAME_7);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_7, group7));
		
		Group group8 = groupManager.create(USER_ID, GROUP_NAME_8);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_8, group8));
		
		Group group9 = groupManager.create(USER_ID, GROUP_NAME_9);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_9, group9));
		
		Group group10 = groupManager.create(USER_ID, GROUP_NAME_10);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_10, group10));
		
		Group group11 = groupManager.create(USER_ID, GROUP_NAME_11);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_11, group11));	

		// read
		Group readedGroup = groupManager.readGroup(group.getId());
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_1, readedGroup)
				&& group.getId().equals(readedGroup.getId()));
		
		Assert.assertTrue(groupManager.readGroups(USER_ID, limit).size() == 5);
		limit.setPage(2);
		List<Group> page_readed_group = groupManager.readGroups(USER_ID, limit);
		Assert.assertTrue(page_readed_group.size() == 1 && page_readed_group.get(0).getName().compareTo(GROUP_NAME_11) == 0);
		
		// add members
		Set<String> members = new HashSet<String>();
		members.add(MEMBER_ID_1);
		members.add(MEMBER_ID_2);
		members.add(MEMBER_ID_3);
		members.add(MEMBER_ID_4);
		members.add(MEMBER_ID_5);
		members.add(MEMBER_ID_6);
		Assert.assertTrue(groupManager.addMembers(group.getId(), members));
		
		// read members
		List<SocialUser> group_members = new ArrayList<SocialUser>();
		List<SocialUser> members_inserted = new ArrayList<SocialUser>();
		limit.setPage(0);
		group_members = groupManager.readMembers(group.getId(), limit);
		for(String s:members){
			members_inserted.add(new SocialUser(s));
		}
		Assert.assertTrue("Size list error: " + group_members.size(), group_members.size() == 5); //&& group_members.containsAll(members_inserted)
		limit.setPage(1);
		group_members = groupManager.readMembers(group.getId(), limit);
		Assert.assertTrue(group_members.size() == 1);	// && group_members.get(0).getId().compareTo(MEMBER_ID_6) == 0
		
		// check members
		readedGroup = groupManager.readGroup(group.getId());
		Assert.assertTrue(members.containsAll(readedGroup.getMembers()));
		
		// delete members
		Set<String> delete_members = new HashSet<String>();
		delete_members.add(MEMBER_ID_1);
		delete_members.add(MEMBER_ID_2);
		Assert.assertTrue(groupManager.removeMembers(group.getId(), delete_members));
		limit.setPage(0);
		Assert.assertTrue(groupManager.readMembers(group.getId(), limit).size() == 4); //.get(0).getId().compareTo(MEMBER_ID_3) == 0

		// update
		readedGroup = groupManager.update(group.getId(), GROUP_RENAME);
		Assert.assertTrue(checkGroupCreation(GROUP_RENAME, readedGroup));
		
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
		
	}

}
