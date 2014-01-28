package eu.trentorise.smartcampus.social.managers;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.engine.beans.Group;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class SocialGroupManagerTest {

	@Autowired
	SocialGroupManager groupManager;

	private static final String USER = "30";

	private static final String GROUP_NAME_1 = "friends";
	private static final String GROUP_NAME_2 = "collegues";

	private boolean checkGroupCreation(String name, Group group) {

		return group != null && group.getId() != null
				&& group.getCreationTime() > 0
				&& group.getLastModifiedTime() > 0
				&& group.getName().equals(name);
	}

	@Test
	public void crudOnGroup() {
		// create
		Group group = groupManager.create(USER, GROUP_NAME_1);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_1, group));

		// read
		Group readedGroup = groupManager.readGroup(group.getId());
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_1, readedGroup)
				&& group.getId().equals(readedGroup.getId()));

		// update
		readedGroup = groupManager.update(group.getId(), GROUP_NAME_2);
		Assert.assertTrue(checkGroupCreation(GROUP_NAME_2, readedGroup));
		// delete

		Assert.assertTrue(groupManager.delete(group.getId()));
	}

}
