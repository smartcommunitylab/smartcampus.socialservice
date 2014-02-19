package eu.trentorise.smartcampus.social.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.Visibility;
import eu.trentorise.smartcampus.social.engine.repo.CommunityRepository;
import eu.trentorise.smartcampus.social.engine.repo.EntityRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class EntityManagerTest {

	@Autowired
	private EntityManager manager;

	@Autowired
	private SocialTypeManager typeManager;

	@Autowired
	private SocialGroupManager groupManager;

	@Autowired
	private SocialCommunityManager communityManager;

	@Autowired
	EntityRepository entityRepo;

	@Autowired
	CommunityRepository communityRepo;

	private Map<String, List<String>> env = new HashMap<String, List<String>>();
	private List<String> envCommunities = new ArrayList<String>();
	private static final String USERID_1 = "1";
	private static final String USERID_2 = "2";

	@Test
	public void read() {
		final String NO_ENTITY = "fakeUri";
		Assert.assertNull(manager.readEntity(NO_ENTITY));

		Assert.assertEquals(0, manager.readEntities(USERID_1, null, null)
				.size());
		create();
		Assert.assertEquals(1, manager.readEntities(USERID_1, null, null)
				.size());
		Assert.assertEquals(0,
				manager.readEntities(null, envCommunities.get(0), null).size());

		Entity entity = new Entity();
		entity.setName("welcome all");
		entity.setLocalId("AAEAC00");
		entity.setVisibility(new Visibility(true));
		entity.setCommunityOwner(envCommunities.get(0));
		entity.setType(typeManager.readTypeByNameAndMimeType("photo",
				"image/jpg").getId());
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1,
				manager.readEntities(null, envCommunities.get(0), null).size());

		Assert.assertEquals("AAEAC00", manager.readEntity(entity.getUri())
				.getLocalId());

	}

	private void initEnv() {
		typeManager.create("photo", "image/jpg");
		envCommunities.add(communityManager.create("Smartcampus", "APPID")
				.getId());
		envCommunities.add(communityManager.create("La Palazzina 2.0", "APPID")
				.getId());

		env.put(USERID_1, Arrays.asList(groupManager
				.create(USERID_1, "friends").getId(),
				groupManager.create(USERID_1, "collegues").getId()));
		env.put(USERID_2,
				Arrays.asList(groupManager.create(USERID_2, "lab").getId()));

	}

	@After
	public void cleanup() {
		entityRepo.deleteAll();
		communityRepo.deleteAll();
	}

	@Test
	public void create() {
		initEnv();

		Entity entity = new Entity();
		entity.setName("my sunny sunday");
		entity.setLocalId("1EAC00");
		entity.setVisibility(new Visibility(true));
		entity.setOwner(USERID_1);
		entity.setType(typeManager.readTypeByNameAndMimeType("photo",
				"image/jpg").getId());

		Assert.assertNull(entity.getUri());
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertNotNull(entity.getUri());
	}

	@Test
	public void update() {
		create();
		Entity entity = new Entity();
		entity.setLocalId("1EAC00");
		entity.setVisibility(new Visibility(Arrays.asList("10", "11", "22"),
				null, null));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(3, entity.getVisibility().getUsers().size());
		Assert.assertFalse(entity.getVisibility().isPublicShared());
		Assert.assertEquals("my sunny sunday", entity.getName());

		entity.setName("sunday is ended :(");
		entity.setExternalUri("http://www.smartcampuslab.eu/1EAC00");
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals("sunday is ended :(", entity.getName());
		Assert.assertEquals("http://www.smartcampuslab.eu/1EAC00",
				entity.getExternalUri());
	}

	@Test(expected = IllegalArgumentException.class)
	public void createFailLocalId() {
		initEnv();
		Entity entity = new Entity();
		entity.setName("my sunny sunday");
		entity.setVisibility(new Visibility(true));
		entity.setOwner(USERID_1);
		entity.setType(typeManager.readTypeByNameAndMimeType("photo",
				"image/jpg").getId());

		entity = manager.saveOrUpdate("testSpace", entity);
	}

	@Test
	public void sharing() {
		Assert.assertEquals(0, manager.readShared(USERID_1, null).size());

		initEnv();
		Entity entity = new Entity();
		entity.setName("my sunny sunday");
		entity.setLocalId("1EAC00");
		entity.setVisibility(new Visibility(Arrays.asList(USERID_1), null, null));
		entity.setOwner(USERID_2);
		entity.setType(typeManager.readTypeByNameAndMimeType("photo",
				"image/jpg").getId());
		entity = manager.saveOrUpdate("testSpace", entity);

		Assert.assertEquals(1, manager.readShared(USERID_1, null).size());

		entity.setVisibility(new Visibility());
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(0, manager.readShared(USERID_1, null).size());

		entity.setVisibility(new Visibility(true));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1, manager.readShared(USERID_1, null).size());

		Assert.assertEquals(0, manager.readShared(USERID_2, null).size());

		/* ************************ community test ***************************** */
		// USER_1 subscribes to Smartcampus community
		communityManager.addMembers(envCommunities.get(0), new HashSet<String>(
				Arrays.asList(USERID_1)));

		entity.setVisibility(new Visibility());
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(0, manager.readShared(USERID_1, null).size());

		entity.setVisibility(new Visibility(null, Arrays.asList(envCommunities
				.get(1)), null));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(0, manager.readShared(USERID_1, null).size());

		entity.setVisibility(new Visibility(null, Arrays.asList(envCommunities
				.get(0)), null));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1, manager.readShared(USERID_1, null).size());

		entity.setVisibility(new Visibility(Arrays.asList(USERID_1), Arrays
				.asList(envCommunities.get(0)), null));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1, manager.readShared(USERID_1, null).size());

		// USER_1 unsubscribes from Smartcampus community
		communityManager.removeMembers(envCommunities.get(0),
				new HashSet<String>(Arrays.asList(USERID_1)));
		Assert.assertEquals(1, manager.readShared(USERID_1, null).size());
		entity.setVisibility(new Visibility(true));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1, manager.readShared(USERID_1, null).size());
		entity.setVisibility(new Visibility());
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(0, manager.readShared(USERID_1, null).size());

		/* ************************ group test ***************************** */

		groupManager.addMembers(env.get(USERID_2).get(0), new HashSet<String>(
				Arrays.asList(USERID_1)));
		Assert.assertEquals(0, manager.readShared(USERID_1, null).size());
		entity.setVisibility(new Visibility(null, null, Arrays.asList(env.get(
				USERID_2).get(0))));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1, manager.readShared(USERID_1, null).size());
		groupManager.removeMembers(env.get(USERID_2).get(0),
				new HashSet<String>(Arrays.asList(USERID_1)));
		Assert.assertEquals(0, manager.readShared(USERID_1, null).size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void createFailNamespace() {
		initEnv();
		Entity entity = new Entity();
		entity.setName("my sunny sunday");
		entity.setLocalId("1EAC00");
		entity.setVisibility(new Visibility(true));
		entity.setOwner(USERID_1);
		entity.setType(typeManager.readTypeByNameAndMimeType("photo",
				"image/jpg").getId());

		entity = manager.saveOrUpdate(null, entity);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createFailType() {
		initEnv();
		Entity entity = new Entity();
		entity.setName("my sunny sunday");
		entity.setLocalId("1EAC00");
		entity.setVisibility(new Visibility(true));
		entity.setOwner(USERID_1);

		entity = manager.saveOrUpdate("testSpace", entity);
	}

	@Test
	public void visibility() {
		initEnv();
		Entity entity = new Entity();
		entity.setName("my sunny sunday");
		entity.setLocalId("1EAC00");
		List<String> users = Arrays.asList("10", "11", "22");
		List<String> communities = Arrays.asList(envCommunities.get(0),
				"1000000");
		List<String> groups = Arrays
				.asList(env.get(USERID_1).get(0), "1000000");
		entity.setVisibility(new Visibility(users, communities, groups));
		entity.setOwner(USERID_1);
		entity.setType(typeManager.readTypeByNameAndMimeType("photo",
				"image/jpg").getId());
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(3, entity.getVisibility().getUsers().size());
		Assert.assertEquals(1, entity.getVisibility().getCommunities().size());
		Assert.assertEquals(1, entity.getVisibility().getGroups().size());
	}

}
