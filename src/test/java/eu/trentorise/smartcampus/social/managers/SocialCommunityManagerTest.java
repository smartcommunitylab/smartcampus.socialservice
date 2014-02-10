package eu.trentorise.smartcampus.social.managers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.engine.beans.Community;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.model.SocialCommunity;
import eu.trentorise.smartcampus.social.engine.repo.CommunityRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class SocialCommunityManagerTest {

	@Autowired
	private SocialCommunityManager communityManager;

	@Autowired
	private CommunityRepository repository;

	private static final String NAME = "SC community";
	private static final String NOT_EXISTING_ID = "1000000";

	@After
	public void cleanDb() {
		repository.deleteAll();
	}

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

		community = communityManager.readCommunity(community.getId());
		Assert.assertEquals(3, community.getTotalMembers());
		Assert.assertTrue(community.getMemberIds().contains("1"));
		Assert.assertTrue(community.getMemberIds().contains("2"));
		Assert.assertTrue(community.getMemberIds().contains("5"));
		Set<String> unsubscribed = new HashSet<String>() {
			{
				add("1");
				add("25");
			}
		};

		Assert.assertTrue(communityManager.removeMembers(community.getId(),
				unsubscribed));
		community = communityManager.readCommunity(community.getId());
		Assert.assertEquals(2, community.getTotalMembers());
		Assert.assertFalse(community.getMemberIds().contains("1"));
		Assert.assertTrue(community.getMemberIds().contains("5"));
	}

	/**
	 * populate DB with communities with past creationTime
	 */
	private void initTestEnv() {
		try {
			SocialCommunity community = new SocialCommunity("community 1");
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			community.setCreationTime(formatter.parse("22-10-2012").getTime());
			repository.save(community);

			community = new SocialCommunity("community 2");
			community.setCreationTime(formatter.parse("01-04-2013").getTime());
			repository.save(community);

			community = new SocialCommunity("community 3");
			community.setCreationTime(formatter.parse("01-01-2014").getTime());
			repository.save(community);

			community = new SocialCommunity("community 4");
			community.setCreationTime(formatter.parse("01-02-2014").getTime());
			repository.save(community);
		} catch (ParseException e) {
			Assert.fail("Exception in initTestEnv");
		}
	}

	@Test
	public void reads() {
		initTestEnv();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

			// no limit setted
			Assert.assertEquals(4, communityManager.readCommunities(null)
					.size());

			Limit limit = new Limit();
			limit.setPage(0);
			limit.setPageSize(2);
			Assert.assertEquals(2, communityManager.readCommunities(limit)
					.size());

			// setted only fromDate
			limit = new Limit();
			limit.setFromDate(formatter.parse("02-01-2014").getTime());
			Assert.assertEquals(1, communityManager.readCommunities(limit)
					.size());
			limit.setFromDate(formatter.parse("31-03-2013").getTime());
			Assert.assertEquals(3, communityManager.readCommunities(limit)
					.size());

			// test result pagination
			limit.setPageSize(3);
			limit.setPage(0);
			Assert.assertEquals(3, communityManager.readCommunities(limit)
					.size());
			limit.setPageSize(2);
			limit.setPage(0);
			Assert.assertEquals(2, communityManager.readCommunities(limit)
					.size());
			// page not exist
			limit.setPageSize(3);
			limit.setPage(15);
			Assert.assertEquals(0, communityManager.readCommunities(limit)
					.size());

			// setted only todate
			limit = new Limit();
			limit.setToDate(formatter.parse("20-06-2014").getTime());
			Assert.assertEquals(4, communityManager.readCommunities(limit)
					.size());
			limit = new Limit();
			limit.setToDate(formatter.parse("22-11-2012").getTime());
			Assert.assertEquals(1, communityManager.readCommunities(limit)
					.size());
			limit = new Limit();
			limit.setToDate(formatter.parse("05-01-2014").getTime());
			Assert.assertEquals(3, communityManager.readCommunities(limit)
					.size());

			// setted fromDate and toDate
			limit = new Limit();
			limit.setFromDate(formatter.parse("31-03-2013").getTime());
			limit.setToDate(formatter.parse("20-06-2014").getTime());
			Assert.assertEquals(3, communityManager.readCommunities(limit)
					.size());
			limit = new Limit();
			limit.setFromDate(formatter.parse("31-03-2013").getTime());
			limit.setToDate(formatter.parse("03-01-2014").getTime());
			Assert.assertEquals(2, communityManager.readCommunities(limit)
					.size());

		} catch (ParseException e) {
			Assert.fail("Exception parsing limit dates");
		}
	}
}
