package eu.trentorise.smartcampus.social.managers;

import java.text.SimpleDateFormat;
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
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.beans.Visibility;
import eu.trentorise.smartcampus.social.engine.model.SocialEntity;
import eu.trentorise.smartcampus.social.engine.model.SocialType;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;
import eu.trentorise.smartcampus.social.engine.repo.CommunityRepository;
import eu.trentorise.smartcampus.social.engine.repo.EntityRepository;
import eu.trentorise.smartcampus.social.engine.repo.GroupRepository;
import eu.trentorise.smartcampus.social.engine.repo.SocialTypeRepository;

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
	SocialTypeRepository typeRepo;

	@Autowired
	CommunityRepository communityRepo;

	@Autowired
	GroupRepository groupRepo;

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
		Assert.assertEquals(0, manager.readEntities(null, null, null).size());

		Assert.assertEquals("AAEAC00", manager.readEntity(entity.getUri())
				.getLocalId());

	}

	private void initFilterCaseDb() throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		SocialType type = new SocialType("image", "image/png");
		type = typeRepo.save(type);

		SocialEntity entity = new SocialEntity();
		String ns = "testSpace";
		String localId = "RJRJ0";
		String creationTime = "2014-02-28";
		entity.setUri(ns + "." + localId);
		entity.setOwner(new SocialUser(USERID_2));
		entity.setName("entity");
		entity.setPublicShared(true);
		entity.setType(type);
		entity.setCreationTime(formatter.parse(creationTime).getTime());
		entity.setNamespace(ns);
		entity.setLocalId(localId);
		entityRepo.save(entity);

		entity = new SocialEntity();
		ns = "testSpace";
		localId = "RJRJ1";
		creationTime = "2014-03-04";
		entity.setUri(ns + "." + localId);
		entity.setOwner(new SocialUser(USERID_2));
		entity.setName("entity");
		entity.setPublicShared(true);
		entity.setType(type);
		entity.setCreationTime(formatter.parse(creationTime).getTime());
		entity.setNamespace(ns);
		entity.setLocalId(localId);
		entityRepo.save(entity);

		entity = new SocialEntity();
		ns = "testSpace";
		localId = "RJRJ2";
		creationTime = "2013-03-04";
		entity.setUri(ns + "." + localId);
		entity.setOwner(new SocialUser(USERID_2));
		entity.setName("entity");
		entity.setPublicShared(true);
		entity.setType(type);
		entity.setCreationTime(formatter.parse(creationTime).getTime());
		entity.setNamespace(ns);
		entity.setLocalId(localId);
		entityRepo.save(entity);

		entity = new SocialEntity();
		ns = "testSpace";
		localId = "RJRJ3";
		creationTime = "2012-02-02";
		entity.setUri(ns + "." + localId);
		entity.setOwner(new SocialUser(USERID_2));
		entity.setName("entity");
		entity.setPublicShared(true);
		entity.setType(type);
		entity.setCreationTime(formatter.parse(creationTime).getTime());
		entity.setNamespace(ns);
		entity.setLocalId(localId);
		entityRepo.save(entity);
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
		groupRepo.deleteAll();
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
	public void communitySharing() {
		initEnv();
		Limit limit = null;
		Entity entity = new Entity();
		entity.setName("my sunny sunday");
		entity.setLocalId("1EAC00");
		entity.setVisibility(new Visibility(null, Arrays.asList(envCommunities
				.get(0)), null));
		entity.setOwner(USERID_1);
		entity.setType(typeManager.readTypeByNameAndMimeType("photo",
				"image/jpg").getId());
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1,
				manager.readShared(envCommunities.get(0), true, limit).size());
		Assert.assertNotNull(manager.readShared(envCommunities.get(0), true,
				manager.defineUri("testSpace", entity.getLocalId())));
		Assert.assertEquals(0,
				manager.readShared(envCommunities.get(1), true, limit).size());

		entity = new Entity();
		entity.setLocalId("1EAC00");
		entity.setVisibility(new Visibility(true));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1,
				manager.readShared(envCommunities.get(0), true, limit).size());
		Assert.assertEquals(1,
				manager.readShared(envCommunities.get(1), true, limit).size());
		Assert.assertNotNull(manager.readShared(envCommunities.get(1), true,
				manager.defineUri("testSpace", entity.getLocalId())));
	}

	@Test
	public void sharing() {
		Limit limit = null;
		Assert.assertEquals(0, manager.readShared(USERID_1, false, limit)
				.size());

		initEnv();
		Entity entity = new Entity();
		entity.setName("my sunny sunday");
		entity.setLocalId("1EAC00");
		entity.setVisibility(new Visibility(Arrays.asList(USERID_1), null, null));
		entity.setOwner(USERID_2);
		entity.setType(typeManager.readTypeByNameAndMimeType("photo",
				"image/jpg").getId());
		entity = manager.saveOrUpdate("testSpace", entity);

		Assert.assertEquals(1, manager.readShared(USERID_1, false, limit)
				.size());

		entity.setVisibility(new Visibility());
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(0, manager.readShared(USERID_1, false, limit)
				.size());

		entity.setVisibility(new Visibility(true));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1, manager.readShared(USERID_1, false, limit)
				.size());
		Assert.assertNotNull(manager.readShared(USERID_1, false,
				manager.defineUri("testSpace", "1EAC00")));
		Assert.assertNull(manager.readShared(USERID_1, false,
				manager.defineUri("testSpace", "1EAC03")));
		Assert.assertEquals(0, manager.readShared(USERID_2, false, limit)
				.size());

		/* ************************ community test ***************************** */
		// USER_1 subscribes to Smartcampus community
		communityManager.addMembers(envCommunities.get(0), new HashSet<String>(
				Arrays.asList(USERID_1)));

		entity.setVisibility(new Visibility());
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(0, manager.readShared(USERID_1, false, limit)
				.size());

		entity.setVisibility(new Visibility(null, Arrays.asList(envCommunities
				.get(1)), null));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(0, manager.readShared(USERID_1, false, limit)
				.size());

		entity.setVisibility(new Visibility(null, Arrays.asList(envCommunities
				.get(0)), null));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1, manager.readShared(USERID_1, false, limit)
				.size());

		entity.setVisibility(new Visibility(Arrays.asList(USERID_1), Arrays
				.asList(envCommunities.get(0)), null));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1, manager.readShared(USERID_1, false, limit)
				.size());

		// USER_1 unsubscribes from Smartcampus community
		communityManager.removeMembers(envCommunities.get(0),
				new HashSet<String>(Arrays.asList(USERID_1)));
		Assert.assertEquals(1, manager.readShared(USERID_1, false, limit)
				.size());
		entity.setVisibility(new Visibility(true));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1, manager.readShared(USERID_1, false, limit)
				.size());
		entity.setVisibility(new Visibility());
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(0, manager.readShared(USERID_1, false, limit)
				.size());

		/* ************************ group test ***************************** */

		groupManager.addMembers(env.get(USERID_2).get(0), new HashSet<String>(
				Arrays.asList(USERID_1)));
		Assert.assertEquals(0, manager.readShared(USERID_1, false, limit)
				.size());
		entity.setVisibility(new Visibility(null, null, Arrays.asList(env.get(
				USERID_2).get(0))));
		entity = manager.saveOrUpdate("testSpace", entity);
		Assert.assertEquals(1, manager.readShared(USERID_1, false, limit)
				.size());
		groupManager.removeMembers(env.get(USERID_2).get(0),
				new HashSet<String>(Arrays.asList(USERID_1)));
		Assert.assertEquals(0, manager.readShared(USERID_1, false, limit)
				.size());
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

	@Test
	public void filterByDate() throws Exception {
		initFilterCaseDb();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Limit limit = null;
		Assert.assertEquals(4, manager.readEntities(USERID_2, null, limit)
				.size());

		limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(50);
		limit.setFromDate(formatter.parse("2014-03-05").getTime());
		Assert.assertEquals(0, manager.readEntities(USERID_2, null, limit)
				.size());

		limit.setFromDate(formatter.parse("2011-03-05").getTime());
		Assert.assertEquals(4, manager.readEntities(USERID_2, null, limit)
				.size());

		limit.setFromDate(formatter.parse("2014-03-04").getTime());
		Assert.assertEquals(1, manager.readEntities(USERID_2, null, limit)
				.size());

		limit.setFromDate(formatter.parse("2011-03-05").getTime());
		limit.setToDate(formatter.parse("2012-09-05").getTime());
		Assert.assertEquals(1, manager.readEntities(USERID_2, null, limit)
				.size());

		limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(50);
		Assert.assertEquals(0, manager.readShared(USERID_2, false, limit)
				.size());
		Assert.assertEquals(4, manager.readShared(USERID_1, false, limit)
				.size());

		limit.setFromDate(formatter.parse("2014-03-05").getTime());
		Assert.assertEquals(0, manager.readShared(USERID_1, false, limit)
				.size());

		limit.setFromDate(formatter.parse("2011-03-05").getTime());
		Assert.assertEquals(4, manager.readShared(USERID_1, false, limit)
				.size());

		limit.setFromDate(formatter.parse("2014-03-04").getTime());
		Assert.assertEquals(1, manager.readShared(USERID_1, false, limit)
				.size());

		limit.setFromDate(formatter.parse("2011-03-05").getTime());
		limit.setToDate(formatter.parse("2012-09-05").getTime());
		Assert.assertEquals(1, manager.readShared(USERID_1, false, limit)
				.size());

	}

	@Test
	public void resultPaging() {
		initEnv();
		for (int i = 0; i < 50; i++) {
			Entity entity = new Entity();
			entity.setName("my sunny sunday");
			entity.setLocalId("1EAC00" + i);
			entity.setVisibility(new Visibility(true));
			entity.setOwner(USERID_2);
			entity.setType(typeManager.readTypeByNameAndMimeType("photo",
					"image/jpg").getId());
			entity = manager.saveOrUpdate("testSpace", entity);
		}

		for (int i = 0; i < 20; i++) {
			Entity entity = new Entity();
			entity.setName("my sunny sunday");
			entity.setLocalId("1EAC10" + i);
			entity.setVisibility(new Visibility(Arrays.asList(USERID_1), null,
					null));
			entity.setOwner(USERID_2);
			entity.setType(typeManager.readTypeByNameAndMimeType("photo",
					"image/jpg").getId());
			entity = manager.saveOrUpdate("testSpace", entity);
		}

		Limit limit = new Limit();
		limit.setPageSize(10);
		limit.setPage(0);
		Assert.assertEquals(10, manager.readShared(USERID_1, false, limit)
				.size());
		limit.setPageSize(40);
		limit.setPage(0);
		Assert.assertEquals(40, manager.readShared(USERID_1, false, limit)
				.size());
		limit.setPage(1);
		Assert.assertEquals(30, manager.readShared(USERID_1, false, limit)
				.size());

		limit.setPageSize(20);
		limit.setPage(0);
		Assert.assertEquals(20, manager.readEntities(USERID_2, null, limit)
				.size());
		limit.setPageSize(20);
		limit.setPage(7);
		Assert.assertEquals(0, manager.readEntities(USERID_2, null, limit)
				.size());
	}

}
