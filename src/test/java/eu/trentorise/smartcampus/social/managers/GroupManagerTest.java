/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
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
 ******************************************************************************/
package eu.trentorise.smartcampus.social.managers;

import it.unitn.disi.sweb.webapi.client.WebApiException;
import it.unitn.disi.sweb.webapi.model.smartcampus.social.User;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.model.Group;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class GroupManagerTest {

	@Autowired
	private GroupManager groupManager;

	@Autowired
	private SocialEngineOperation socialOperation;

	@Test
	public void create() throws SocialServiceException, WebApiException {
		User user1 = socialOperation.createUser();
		String groupName = "SC_TEST_GROUP_" + System.currentTimeMillis();
		String groupId = groupManager.create(user1.getId().toString(), groupName);
		Assert.assertTrue(groupId != null);
		
		Group g = groupManager.getGroup(groupId);
		Assert.assertNotNull(g);

		g.setName("newName");
		Assert.assertTrue(groupManager.update(g));
		
		Assert.assertEquals(groupManager.getGroups(user1.getId().toString()).size(),2);

		
		Assert.assertTrue(groupManager.delete(groupId));
		socialOperation.deleteUser(user1.getId());
	}

	/**
	 * Test on delete an user from the default system group
	 * 
	 * @throws Exception
	 */
	@Test
	public void addToGroup() throws Exception {
		User user1 = socialOperation.createUser();
		User user2 = socialOperation.createUser();

		// user1 creates group1 adding user2 in
		String groupId = groupManager.create(user1.getId().toString(), "SC_TEST_GROUP_"
				+ System.currentTimeMillis());
		Assert.assertTrue(groupId != null);

		Assert.assertTrue(groupManager.addUser(user1.getId().toString(),
				groupId, Collections.singletonList(user2.getId().toString())));
		
		Assert.assertTrue(groupManager.removeUser(Collections.singletonList(user2.getId().toString()),groupId));

		groupManager.delete(groupId);
		socialOperation.deleteUser(user1.getId());
		socialOperation.deleteUser(user2.getId());

	}

}
