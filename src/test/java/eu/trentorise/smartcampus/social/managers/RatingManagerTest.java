package eu.trentorise.smartcampus.social.managers;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Visibility;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml",
		"classpath:/spring/spring-security.xml" })
public class RatingManagerTest {
	@Autowired
	RatingManager ratingManager;

	@Autowired
	EntityManager entityManager;
	@Autowired
	EntityTypeManager typeManager;

	@Test
	public void rate() {
		String USER_1 = "23";
		String USER_2 = "44";
		String USER_3 = "244";
		String USER_4 = "434";
		String USER_5 = "454";
		EntityType type = typeManager.create("photo", "image/jpg");
		Entity entity = new Entity();
		entity.setLocalId("AASDKF4");
		entity.setOwner("222");
		entity.setName("my summer photo");
		entity.setType(type.getId());
		entity.setVisibility(new Visibility(true));

		entity = entityManager.saveOrUpdate("space1", entity);
		Assert.assertTrue(ratingManager.delete(entity.getUri(), USER_1));

		Assert.assertTrue(ratingManager.rate(entity.getUri(), USER_1, 3));
		Assert.assertEquals(3d, ratingManager
				.getRating(USER_1, entity.getUri()).getRating());
		entity = entityManager.readEntity(entity.getUri());
		Assert.assertEquals(3d, entity.getRating());
		Assert.assertEquals(1, entity.getTotalVoters());

		ratingManager.rate(entity.getUri(), USER_1, 1);
		entity = entityManager.readEntity(entity.getUri());
		Assert.assertEquals(1d, entity.getRating());
		Assert.assertEquals(1, entity.getTotalVoters());

		ratingManager.rate(entity.getUri(), USER_2, 3);
		entity = entityManager.readEntity(entity.getUri());
		Assert.assertEquals(2d, entity.getRating());
		Assert.assertEquals(2, entity.getTotalVoters());

		ratingManager.delete(entity.getUri(), USER_1);
		entity = entityManager.readEntity(entity.getUri());
		Assert.assertEquals(3d, entity.getRating());
		Assert.assertEquals(1, entity.getTotalVoters());

		ratingManager.rate(entity.getUri(), USER_1, 0);
		entity = entityManager.readEntity(entity.getUri());
		Assert.assertEquals(1.5d, entity.getRating());
		Assert.assertEquals(2, entity.getTotalVoters());

		Assert.assertTrue(ratingManager.delete(entity.getUri(), USER_1));
		entity = entityManager.readEntity(entity.getUri());
		Assert.assertEquals(1, entity.getTotalVoters());
		Assert.assertEquals(3d, entity.getRating());

		ratingManager.rate(entity.getUri(), USER_2, 4);
		ratingManager.rate(entity.getUri(), USER_3, 1);
		ratingManager.rate(entity.getUri(), USER_5, 2);
		ratingManager.rate(entity.getUri(), USER_4, 2.5);

		ratingManager.delete(entity.getUri(), USER_2);
		entity = entityManager.readEntity(entity.getUri());
		Assert.assertEquals(3, entity.getTotalVoters());
	}
}
