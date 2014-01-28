package eu.trentorise.smartcampus.social.managers;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.social.engine.model.SocialEntity;
import eu.trentorise.smartcampus.social.engine.model.SocialGroup;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class JPATest {

	@PersistenceContext
	EntityManager em;

	@Test
	public void groupCRUD() {
		SocialGroup group = new SocialGroup();
		group.setName("amici");
		group.setCreationTime(System.currentTimeMillis());
		// group.setCreatorId(21l);
		group.setLastModifiedTime(System.currentTimeMillis());
		Set<SocialUser> members = new HashSet<SocialUser>();
		members.add(new SocialUser("32"));
		group.setMembers(members);
		em.persist(group);
		em.flush();

		SocialEntity entity = new SocialEntity();
		Set<SocialGroup> shared = new HashSet<SocialGroup>();
		shared.add(group);
		entity.setGroupsSharedWith(shared);
		em.persist(entity);
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery criteria = builder.createQuery(SocialEntity.class);

	}
}
