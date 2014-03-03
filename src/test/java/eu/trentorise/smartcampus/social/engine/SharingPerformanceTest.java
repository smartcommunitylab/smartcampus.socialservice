package eu.trentorise.smartcampus.social.engine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.beans.Visibility;
import eu.trentorise.smartcampus.social.managers.EntityManager;
import eu.trentorise.smartcampus.social.managers.SocialCommunityManager;
import eu.trentorise.smartcampus.social.managers.SocialGroupManager;
import eu.trentorise.smartcampus.social.managers.SocialTypeManager;

/**
 * Test to emulate sharing performance in a realist environment
 * 
 * @author mirko perillo
 * 
 */

public class SharingPerformanceTest {

	private static String[] USERS = new String[100];
	private static final int ENT_SHARED = 500;
	private static final int MAX_GROUPS = 20;
	private static Map<String, List<String>> USER_INFO = new HashMap<String, List<String>>();

	private static final List<String> NS = Arrays.asList("Sc", "NS", "appNs");

	private static List<EntityType> TYPES = new ArrayList<EntityType>();
	private static List<String> COMMUNITIES = new ArrayList<String>();

	private static int index = 0;
	private static int userIndex = 0;

	private static EntityManager entityManager;
	private static SocialTypeManager typeManager;
	private static SocialGroupManager groupManager;
	private static SocialCommunityManager communityManager;

	private static Random rand = new Random();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// init Spring manager classes
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"spring/applicationContext.xml");
		entityManager = ctx.getBean(EntityManager.class);
		typeManager = ctx.getBean(SocialTypeManager.class);
		groupManager = ctx.getBean(SocialGroupManager.class);
		communityManager = ctx.getBean(SocialCommunityManager.class);

		// init users in env
		for (int i = 0; i < USERS.length; i++) {
			USERS[i] = userCreate();
		}
		initGroups();
		// init types
		initEntityTypes();

		// init communities
		initCommunities();
		initCommunityMembers();

		System.out.println(">> Sharing entities...");
		// entity sharing
		for (int i = 0; i < ENT_SHARED; i++) {
			entityManager.saveOrUpdate(NS.get(0), randomEntity());
		}

		printEnv();
		long startTime = System.currentTimeMillis();
		String user = USERS[rand.nextInt(USERS.length)];
		Limit limit = null;
		List<Entity> res = entityManager.readShared(user, false, limit);
		DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance();
		formatter.setMaximumFractionDigits(2);
		System.out.println(String.format(
				">> Readed %s shared entities for user %s in %s sec",
				res.size(),
				user,
				formatter.format(new Float(System.currentTimeMillis()
						- startTime) / 1000)));

	}

	private static void printEnv() {
		System.out.println(String.format(">> %s users", USERS.length));
		System.out.println(String.format(">> %s communities",
				COMMUNITIES.size()));
		System.out.println(String.format(">> %s entities shared", ENT_SHARED));
	}

	private static Entity randomEntity() {
		String user = USERS[rand.nextInt(USERS.length)];
		Entity e = new Entity();
		e.setOwner(user);
		e.setLocalId("" + (index++));
		e.setName("entity " + System.currentTimeMillis());
		if (System.currentTimeMillis() % 2 == 0) {
			e.setVisibility(new Visibility(true));
		} else {
			int randomUsers = rand.nextInt(USERS.length);
			int randomGroups = 0;
			try {
				randomGroups = rand.nextInt(USER_INFO.get(user).size());
			} catch (Exception ee) {
			}
			int randomCommunities = rand.nextInt(COMMUNITIES.size());
			List<String> u = new ArrayList<String>();
			List<String> g = new ArrayList<String>();
			List<String> c = new ArrayList<String>();
			for (int i = 0; i < randomUsers; i++) {
				u.add(USERS[rand.nextInt(USERS.length)]);
			}
			for (int i = 0; i < randomGroups; i++) {
				try {
					g.add(USER_INFO.get(user).get(
							rand.nextInt(USER_INFO.get(user).size())));
				} catch (Exception ee) {
				}
			}

			for (int i = 0; i < randomCommunities; i++) {
				c.add(COMMUNITIES.get(rand.nextInt(COMMUNITIES.size())));
			}
			e.setVisibility(new Visibility(u, c, g));
		}
		e.setType(TYPES.get(rand.nextInt(TYPES.size())).getId());
		return e;
	}

	private static String userCreate() {
		return "" + userIndex++;
	}

	private static void initCommunityMembers() {
		for (int j = 0; j < COMMUNITIES.size(); j++) {
			Set<String> members = new HashSet<String>();
			for (int i = 0; i < rand.nextInt(USERS.length); i++) {
				members.add(USERS[rand.nextInt(USERS.length)]);
			}
			communityManager.addMembers(
					COMMUNITIES.get(rand.nextInt(COMMUNITIES.size())), members);
		}
	}

	private static void initGroups() {
		for (int i = 0; i < USERS.length; i++) {
			if (USER_INFO.get(USERS[i]) == null) {
				USER_INFO.put(USERS[i], new ArrayList<String>());
			}
		}
		for (int i = 0; i < USERS.length; i++) {
			String user = USERS[rand.nextInt(USERS.length)];
			int groupNumber = rand.nextInt(MAX_GROUPS);
			int members = rand.nextInt(USERS.length);
			for (int j = 0; j < groupNumber; j++) {
				String group = groupManager.create(user,
						"group " + System.currentTimeMillis()).getId();
				USER_INFO.get(user).add(group);
				Set<String> memb = new HashSet<String>();
				for (int z = 0; z < members; z++) {
					memb.add(USERS[rand.nextInt(USERS.length)]);
				}
				groupManager.addMembers(group, memb);
			}
		}
	}

	private static void initCommunities() {
		COMMUNITIES.add(communityManager.create("Scartcampus", "AAFFEEKKK123")
				.getId());
		COMMUNITIES.add(communityManager.create("Gamers", "AAFFEEKKK123")
				.getId());
		COMMUNITIES.add(communityManager.create("Readers", "EEEdddFGGlrk305")
				.getId());
		COMMUNITIES.add(communityManager.create("Hikers", "vmfsdkk5DDdk45k4k")
				.getId());
	}

	private static void initEntityTypes() {
		TYPES.add(typeManager.create("photo", "image/jpg"));
		TYPES.add(typeManager.create("text", "text/plain"));
		TYPES.add(typeManager.create("post", "text/plain"));
	}
}
