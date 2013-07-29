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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.model.Community;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class CommunityManagerTest {

	@Autowired
	private CommunityManager communityManager;

	@Autowired
	private SocialEngineOperation socialOperation;

	@Before
	public void cleanUp() throws SocialServiceException {
		communityManager.delete("cid");
	}
	
	@Test
	public void create() throws SocialServiceException {
		int before = communityManager.getCommunities().size();
		Community community = new Community();
		community.setName("SC_TEST_COMMUNITY_" + System.currentTimeMillis());
		community = communityManager.create(community, "cid");
		Assert.assertTrue(community != null);
		
		community = communityManager.getCommunity("cid");
		Assert.assertTrue(community != null);

		community = communityManager.getCommunityBySocialId(community.getSocialId());
		Assert.assertTrue(community != null);

		Assert.assertEquals(communityManager.getCommunities().size(), before + 1);
		
		Assert.assertTrue(communityManager.delete("cid"));
		Assert.assertEquals(communityManager.getCommunities().size(), before);
	}

	/**
	 * Test on community user subscription
	 * 
	 * @throws SocialServiceException
	 * @throws WebApiException
	 */
	@Test
	public void getUserCommunities() throws SocialServiceException,
			WebApiException {
		User user1 = socialOperation.createUser();

		// creation of a community
		Community community = new Community();
		community.setName("SC_TEST_COMMUNITY_" + System.currentTimeMillis());
		community = communityManager.create(community, "cid");
		Assert.assertTrue(community != null);

		Assert.assertEquals(0,
				communityManager.getCommunities(user1.getId().toString()).size());
		// user1 is added to community
		Assert.assertTrue(communityManager.addUser(user1.getId().toString(), "cid"));
		Assert.assertEquals(1,
				communityManager.getCommunities(user1.getId().toString()).size());
		// user1 is removed from community
		Assert.assertTrue(communityManager.removeUser(user1.getId().toString(), "cid"));
		Assert.assertEquals(0,
				communityManager.getCommunities(user1.getId().toString()).size());

		communityManager.delete("cid");
		socialOperation.deleteUser(user1.getId());
	}

}
