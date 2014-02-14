package eu.trentorise.smartcampus.social.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

	private Map<String, List<String>> env = new HashMap<String, List<String>>();
	private List<String> envCommunities = new ArrayList<String>();
	private static final String USERID = "1";

	@Test
	public void read() {
		final String NO_ENTITY = "fakeUri";
		Assert.assertNull(manager.readEntity(NO_ENTITY));
	}

	private void initEnv() {
		typeManager.create("photo", "image/jpg");
		envCommunities.add(communityManager.create("Smartcampus", null).getId());
		envCommunities.add(communityManager.create("La Palazzina 2.0", null).getId());

		env.put(USERID, Arrays.asList(groupManager.create(USERID, "friends")
				.getId(), groupManager.create(USERID, "collegues").getId()));

	}

	@After
	public void cleanup() {
		entityRepo.deleteAll();
	}

	@Test
	public void create() {
		initEnv();

		Entity entity = new Entity();
		entity.setName("my sunny sunday");
		entity.setLocalId("1EAC00");
		entity.setVisibility(new Visibility(true));
		entity.setOwner(USERID);
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
		entity.setOwner(USERID);
		entity.setType(typeManager.readTypeByNameAndMimeType("photo",
				"image/jpg").getId());

		entity = manager.saveOrUpdate("testSpace", entity);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createFailNamespace() {
		initEnv();
		Entity entity = new Entity();
		entity.setName("my sunny sunday");
		entity.setLocalId("1EAC00");
		entity.setVisibility(new Visibility(true));
		entity.setOwner(USERID);
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
		entity.setOwner(USERID);

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
		List<String> groups = Arrays.asList(env.get(USERID).get(0), "1000000");
		entity.setVisibility(new Visibility(users, communities, groups));
		entity.setOwner(USERID);
		entity.setType(typeManager.readTypeByNameAndMimeType("photo",
				"image/jpg").getId());
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(3, entity.getVisibility().getUsers().size());
		Assert.assertEquals(1, entity.getVisibility().getCommunities().size());
		Assert.assertEquals(1, entity.getVisibility().getGroups().size());
	}

}
