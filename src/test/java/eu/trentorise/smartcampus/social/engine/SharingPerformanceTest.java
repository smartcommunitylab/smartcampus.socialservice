package eu.trentorise.smartcampus.social.engine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Visibility;
import eu.trentorise.smartcampus.social.managers.EntityManager;
import eu.trentorise.smartcampus.social.managers.SocialTypeManager;

/**
 * Test to emulate sharing performance in a realist environment
 * 
 * @author mirko perillo
 * 
 */

public class SharingPerformanceTest {

	// private static final int USERS = 5;

	private static String[] USERS = new String[5];

	private static final List<String> NS = Arrays.asList("Sc", "NS", "appNs");

	private static List<EntityType> TYPES = new ArrayList<EntityType>();

	private static final int ENT_SHARED = 50;

	private static int index = 0;

	private static EntityManager entityManager;
	private static SocialTypeManager typeManager;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// init Spring manager classes
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"spring/applicationContext.xml");
		entityManager = ctx.getBean(EntityManager.class);
		typeManager = ctx.getBean(SocialTypeManager.class);
		// init users in env
		for (int i = 0; i < USERS.length; i++) {
			USERS[i] = randomUserCreate();
		}

		// init types
		initEntityTypes();

		// entity sharing
		for (int i = 0; i < ENT_SHARED; i++) {
			entityManager.saveOrUpdate(NS.get(0), randomEntity());
		}

		long startTime = System.currentTimeMillis();
		entityManager.readShared(USERS[2], null);
		System.out.println("Read shared entities in "
				+ DecimalFormat.getInstance(Locale.ITALY).format(
						System.currentTimeMillis() - startTime * 1000));

	}

	private static Entity randomEntity() {
		Entity e = new Entity();
		e.setOwner(USERS[0]);
		e.setLocalId("" + (index++));
		e.setName("entity " + System.currentTimeMillis());
		e.setVisibility(new Visibility(true));
		e.setType(TYPES.get(2).getId());
		return e;
	}

	private static String randomUserCreate() {
		return Long.toString(new Random(System.currentTimeMillis()).nextLong());
	}

	private static void initEntityTypes() {
		TYPES.add(typeManager.create("photo", "image/jpg"));
		TYPES.add(typeManager.create("text", "text/plain"));
		TYPES.add(typeManager.create("post", "text/plain"));
	}
}
