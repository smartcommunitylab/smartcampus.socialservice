package eu.trentorise.smartcampus.social.managers;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.engine.beans.Community;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class SocialCommunityManagerTest {

	@Autowired
	private SocialCommunityManager communityManager;

	private static final String NAME = "SC community";
	private static final String NOT_EXISTING_ID = "1000000";

	@Test
	public void crud() {

		Community community = communityManager.create(NAME);
		Assert.assertTrue(community.getId() != null);

		Assert.assertEquals(NAME,
				communityManager.readCommunity(community.getId()).getName());
		Assert.assertNull(communityManager.readCommunity(NOT_EXISTING_ID));

		Assert.assertTrue(communityManager.delete(community.getId()));
	}

	@Test
	public void members() {
		Community community = communityManager.create(NAME);
		Set<String> subscribe = new HashSet<String>() {
			{
				add("1");
				add("2");
				add("5");
			}
		};
		Assert.assertTrue(communityManager.addMembers(community.getId(),
				subscribe));

		Set<String> unsubscribed = new HashSet<String>() {
			{
				add("1");
				add("25");
			}
		};

		Assert.assertTrue(communityManager.removeMembers(community.getId(),
				unsubscribed));
	}

}
