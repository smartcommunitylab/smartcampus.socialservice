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

	private static final String GROUP_NAME_1 = "friends";
	private static final String GROUP_NAME_2 = "collegues";
	private static final String GROUP_NAME_3 = "collaborators";

	private boolean checkGroupCreation(String name, Group group) {

		return group != null && group.getId() != null
				&& group.getCreationTime() > 0
				&& group.getLastModifiedTime() > 0
				&& group.getName().equals(name);
	}

	@Test
	public void crudOnGroup() {
		
		Limit limit = new Limit();
		limit.setPosition(0);
		limit.setSize(10);
		
		// create
		Group group = groupManager.create(USER_ID, GROUP_NAME_1);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_1, group));
		
		Group group2 = groupManager.create(USER_ID, GROUP_NAME_3);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_3, group2));

		// read
		Group readedGroup = groupManager.readGroup(group.getId());
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_1, readedGroup)
				&& group.getId().equals(readedGroup.getId()));
		
		Assert.assertTrue(groupManager.readGroups(USER_ID, limit).size() == 2);
		
		// add members
		Set<String> members = new HashSet<String>();
		members.add(MEMBER_ID_1);
		members.add(MEMBER_ID_2);
		members.add(MEMBER_ID_3);
		Assert.assertTrue(groupManager.addMembers(group.getId(), members));
		
		// read members
		List<SocialUser> group_members = new ArrayList<SocialUser>();
		List<SocialUser> members_inserted = new ArrayList<SocialUser>();
		group_members = groupManager.readMembers(group.getId(), limit);
		for(String s:members){
			members_inserted.add(new SocialUser(s));
		}
		Assert.assertTrue(group_members.size() == 3 && group_members.containsAll(members_inserted));
		// && group_members.containsAll(members)
		
		// delete members
		Set<String> delete_members = new HashSet<String>();
		delete_members.add(MEMBER_ID_1);
		delete_members.add(MEMBER_ID_2);
		Assert.assertTrue(groupManager.removeMembers(group.getId(), delete_members));
		Assert.assertTrue(groupManager.readMembers(group.getId(), limit).get(0).getId().compareTo(MEMBER_ID_3) == 0);

		// update
		readedGroup = groupManager.update(group.getId(), GROUP_NAME_2);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_2, readedGroup));
		
		// delete
		Assert.assertTrue(groupManager.delete(group.getId()));
		Assert.assertTrue(groupManager.delete(group2.getId()));
		
	}

}
