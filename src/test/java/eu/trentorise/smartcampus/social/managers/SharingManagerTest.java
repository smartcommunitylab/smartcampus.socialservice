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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.model.Community;
import eu.trentorise.smartcampus.social.model.Concept;
import eu.trentorise.smartcampus.social.model.Entity;
import eu.trentorise.smartcampus.social.model.EntityType;
import eu.trentorise.smartcampus.social.model.ShareVisibility;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class SharingManagerTest {

	@Autowired
	private SharingManager sharingManager;
	@Autowired
	private GroupManager groupManager;
	@Autowired
	private CommunityManager communityManager;
	@Autowired
	private SocialEngineOperation socialOperation;

	@Test
	public void userEntities() throws Exception {
		User user1 = socialOperation.createUser();
		
		List<Concept> concepts = sharingManager.getConceptsByName("concert", 1);
		Concept test = sharingManager.getConceptByGlobalId(4080l);
		EntityType entityType = sharingManager.getEntityTypeByConceptId(test.getId());
		if (entityType != null) {
			sharingManager.deleteEntityType(Long.parseLong(entityType.getId()));
		}
		entityType = sharingManager.createEntityType(test.getId());
		eu.trentorise.smartcampus.social.model.EntityRequest e = new eu.trentorise.smartcampus.social.model.EntityRequest();
		e.setDescription("description");
		e.setName("entitySample");
		e.setTags(Arrays.asList(concepts.get(0)));
		e.setTypeId(entityType.getId());
		eu.trentorise.smartcampus.social.model.Entity entity = sharingManager.createEntity(e,user1.getId().toString());
		Assert.assertNotNull(entity);
		System.err.println(entity);
		
		e = new eu.trentorise.smartcampus.social.model.EntityRequest();
		e.setDescription("description");
		e.setName("entitySample");
		e.setTags(Arrays.asList(concepts.get(0)));
		e.setTypeId(entityType.getId());
		e.setRelations(Collections.singletonList(entity.getEntityId()));
		entity = sharingManager.createEntity(e,user1.getId().toString());
		Assert.assertNotNull(entity);
		System.err.println(entity);
		
		e.setName("entitySampleUpdated");
		e.setId(entity.getEntityId());
		sharingManager.updateEntity(e);
		entity = sharingManager.getShared(e.getId(), user1.getId().toString(), true);
		Assert.assertEquals(entity.getTitle(),"entitySampleUpdated");
		
		List<Entity> list = sharingManager.getShared(user1.getId().toString(), Collections.singletonList(user1.getId().toString()),null,null,0,-1, entityType.getId() ,false,true);
		Assert.assertEquals(list.size(), 2);
		for (Entity ee : list) {
			Assert.assertTrue(sharingManager.deleteEntity(ee.getEntityId()));
		}
		

		sharingManager.deleteEntityType(Long.parseLong(entityType.getId()));
		
		socialOperation.deleteUser(user1.getId());
	} 

	@After
	public void cleanUp() throws SocialServiceException {
		communityManager.delete("cid");
	}
	
	@Test
	public void communityEntities() throws Exception {
		Community community = new Community();
		community.setName("SC_TEST_COMMUNITY_" + System.currentTimeMillis());
		community = communityManager.create(community, "cid");
		
		List<Concept> concepts = sharingManager.getConceptsByName("concert", 1);
		Concept test = sharingManager.getConceptByGlobalId(4080l);
		EntityType entityType = sharingManager.getEntityTypeByConceptId(test.getId());
		if (entityType != null) {
			sharingManager.deleteEntityType(Long.parseLong(entityType.getId()));
		}
		entityType = sharingManager.createEntityType(test.getId());
		eu.trentorise.smartcampus.social.model.EntityRequest e = new eu.trentorise.smartcampus.social.model.EntityRequest();
		e.setDescription("description");
		e.setName("entitySample");
		e.setTags(Arrays.asList(concepts.get(0)));
		e.setTypeId(entityType.getId());
		eu.trentorise.smartcampus.social.model.Entity entity = sharingManager.createEntity(e,community.getSocialId().toString());
		Assert.assertNotNull(e);
		System.err.println(e);
		
		e.setName("entitySampleUpdated");
		e.setId(entity.getEntityId());
		sharingManager.updateEntity(e);
		entity = sharingManager.getShared(e.getId(), community.getSocialId().toString(), true);
		Assert.assertEquals(entity.getTitle(),"entitySampleUpdated");
		
//		List<Entity> list = sharingManager.getCommunityEntities("cid",0,-1, null, false);
//		Assert.assertEquals(1, list.size());
		
		Assert.assertTrue(sharingManager.deleteEntity(e.getId()));

		sharingManager.deleteEntityType(Long.parseLong(entityType.getId()));
	} 

	/**
	 * Failure test. If a entityType binded to a concept already exists, method
	 * threw exception
	 * 
	 * @throws SocialServiceException
	 * @throws AlreadyExistException
	 */
	@Test(expected = AlreadyExistException.class)
	public void entityTypeAlreadyExists() throws SocialServiceException,
			AlreadyExistException {
		Concept c = sharingManager.getConceptByGlobalId(4080L);
		Assert.assertNotNull(sharingManager.createEntityType(c.getId()));
		sharingManager.createEntityType(c.getId());

	}

	/**
	 * Entity type crud
	 * 
	 * @throws SocialServiceException
	 * @throws AlreadyExistException
	 */
	@Test
	public void entityTypeCrud() throws SocialServiceException,
			AlreadyExistException {

		Concept c = sharingManager.getConceptByGlobalId(4080L);
		String conceptId = c.getId();

		if (sharingManager.getEntityTypeByConceptId(conceptId) != null) {
			sharingManager.deleteEntityType(Long.parseLong(sharingManager.getEntityTypeByConceptId(conceptId).getId()));
		}

		EntityType newType = sharingManager.createEntityType(c.getId());
		Assert.assertNotNull(sharingManager.getEntityType(newType.getId()));
		Assert.assertEquals(c.getName(), newType.getName());
		Assert.assertEquals(conceptId, newType.getConcept().getId());
		Assert.assertTrue(sharingManager.deleteEntityType(Long.parseLong(newType.getId())));
		Assert.assertNull(sharingManager.getEntityType(newType.getId()));

	}
	
	/**
	 * Unsharing using unsharing method and reset manually SharedVisibility
	 * options
	 * 
	 * @throws SocialServiceException
	 * @throws WebApiException
	 */
	@Test
	public void unsharing() throws SocialServiceException, WebApiException {
		User user1 = socialOperation.createUser();
		User user2 = socialOperation.createUser();
		// user1 creates event1
		it.unitn.disi.sweb.webapi.model.entity.Entity event = socialOperation.createEntity(user1.getEntityBaseId(), EntityTypes.event);

		Assert.assertEquals(
				0,
				sharingManager.getShared(user2.getId().toString(),
						Arrays.asList(user1.getId().toString()), "-1", null, 0, 5, null,
						false, false).size());

		// user1 shares an entity with user2
		ShareVisibility visibility = new ShareVisibility();
		visibility.setUserIds(Arrays.asList(user2.getId().toString()));

		Assert.assertTrue(sharingManager.share(event.getId().toString(), user1.getId().toString(), visibility));

		Assert.assertEquals(
				1,
				sharingManager.getShared(user2.getId().toString(),
						Arrays.asList(user1.getId().toString()), "-1", null, 0, 5, null,
						false, false).size());

		// unshare
		Assert.assertTrue(sharingManager.unshare(event.getId().toString(), user1.getId().toString()));

		Assert.assertEquals(
				0,
				sharingManager.getShared(user2.getId().toString(),
						Arrays.asList(user1.getId().toString()), "-1", null, 0, 5, null,
						false, false).size());

		// user1 shares an entity with user2
		Assert.assertTrue(sharingManager.share(event.getId().toString(), user1.getId().toString(), visibility));

		Assert.assertEquals(
				1,
				sharingManager.getShared(user2.getId().toString(),
						Arrays.asList(user1.getId().toString()), "-1", null, 0, 5, null,
						false, false).size());

		// reset sharing options
		visibility.setUserIds(null);

		Assert.assertTrue(sharingManager.share(event.getId().toString(), user1.getId().toString(), visibility));

		Assert.assertEquals(
				0,
				sharingManager.getShared(user2.getId().toString(),
						Arrays.asList(user1.getId().toString()), "-1", null, 0, 5, null,
						false, false).size());

		socialOperation.deleteEntity(event);
		socialOperation.deleteUser(user1.getId());
		socialOperation.deleteUser(user2.getId());

	}

	/**
	 * Test sharing
	 * 
	 * @throws SocialServiceException
	 * @throws WebApiException
	 */
	@Test
	public void sharing() throws SocialServiceException, WebApiException {
		User user1 = socialOperation.createUser();
		User user2 = socialOperation.createUser();

		// user1 creates event1
		it.unitn.disi.sweb.webapi.model.entity.Entity event = socialOperation.createEntity(user1.getEntityBaseId(), EntityTypes.event);

		Assert.assertEquals(
				0,
				sharingManager.getShared(user2.getId().toString(),
						Arrays.asList(user1.getId().toString()), "-1", null, 0, 5, null,
						false, false).size());

		// user1 shares an entity with user2
		ShareVisibility visibility = new ShareVisibility();
		visibility.setUserIds(Arrays.asList(user2.getId().toString()));

		Assert.assertTrue(sharingManager.share(event.getId().toString(), user1.getId().toString(), visibility));

		Assert.assertEquals(
				1,
				sharingManager.getShared(user2.getId().toString(),
						Arrays.asList(user1.getId().toString()), "-1", null, 0, 5, null,
						false, false).size());

		// public share
		Assert.assertEquals(
				0,
				sharingManager.getShared("-1", Arrays.asList(user1.getId().toString()),
						"-1", null, 0, 5, null, false, false).size());

		visibility = new ShareVisibility();
		visibility.setAllUsers(true);
		visibility.setAllCommunities(true);
		// user1 shares a public event
		Assert.assertTrue(sharingManager.share(event.getId().toString(), user1.getId().toString(), visibility));

		Assert.assertEquals(
				1,
				sharingManager.getShared("-1", Arrays.asList(user1.getId().toString()),
						"-1", null, 0, 5, null, false, false).size());

		socialOperation.deleteEntity(event);
		socialOperation.deleteUser(user1.getId());
		socialOperation.deleteUser(user2.getId());

	}
//
//	/**
//	 * Test to check sharing operation of My People group
//	 * 
//	 * @throws SocialServiceException
//	 * @throws WebApiException
//	 */
//	@Test
//	public void myPeopleShare() throws SocialServiceException,
//			WebApiException {
//		User user1 = socialOperation.createUser();
//		User user2 = socialOperation.createUser();
//
//		// user2 creates entity1
//		Entity entity1 = socialOperation.createEntity(user2.getEntityBaseId(),
//				EntityTypes.event);
//
//		// user1 create group1
//		long gid = groupManager.create(user1.getId(),
//				"SC_TEST_GROUP_" + System.currentTimeMillis());
//		// user1 adds user2 to group1
//		groupManager.addUser(user1.getId(), Collections.singletonList(gid),
//				user2.getId());
//
//		Assert.assertEquals(
//				0,
//				sharingManager.getShared(user1.getId(), null,
//						Constants.MY_PEOPLE_GROUP_ID, null, 0, 5, null, false)
//						.size());
//
//		// user2 shares entity1 public
//		ShareVisibility visibility = new ShareVisibility();
//		visibility.setAllUsers(true);
//		Assert.assertTrue(sharingManager.share(entity1.getId(), user2.getId(),
//				visibility));
//
//		Assert.assertEquals(
//				1,
//				sharingManager.getShared(user1.getId(), null,
//						Constants.MY_PEOPLE_GROUP_ID, null, 0, 5, null, false)
//						.size());
//
//		socialOperation.deleteUser(user1.getId());
//		socialOperation.deleteUser(user2.getId());
//		socialOperation.deleteEntity(entity1);
//
//	}
//
	/**
	 * Getting entities created by a user
	 * 
	 * @throws SocialServiceException
	 * @throws WebApiException
	 */
	@Test
	public void myContents() throws SocialServiceException, WebApiException {

		User user1 = socialOperation.createUser();
		// event of user1
		it.unitn.disi.sweb.webapi.model.entity.Entity e1 = socialOperation.createEntity(user1.getEntityBaseId(), EntityTypes.event);
		it.unitn.disi.sweb.webapi.model.entity.Entity e2 = socialOperation.createEntity(user1.getEntityBaseId(), EntityTypes.location);
		it.unitn.disi.sweb.webapi.model.entity.Entity e3 = socialOperation.createEntity(user1.getEntityBaseId(), EntityTypes.experience);

		Assert.assertEquals(
				3,
				sharingManager.getShared(user1.getId().toString(),
						Arrays.asList(user1.getId().toString()), "-1", null, 0, 5, null,
						false, false).size());

		Assert.assertEquals(
				1,
				sharingManager.getShared(user1.getId().toString(),
						Arrays.asList(user1.getId().toString()), "-1", null, 0, 1, null,
						false, false).size());

		Assert.assertEquals(
				2,
				sharingManager.getShared(user1.getId().toString(),
						Arrays.asList(user1.getId().toString()), "-1", null, 1, 5, null,
						false, false).size());

		Assert.assertEquals(
				1,
				sharingManager.getShared(user1.getId().toString(),
						Arrays.asList(user1.getId().toString()), "-1", null, 0, 5, EntityTypes.event.value().toString(),
						false, false).size());

		socialOperation.deleteUser(user1.getId());
		socialOperation.deleteEntity(e1);
		socialOperation.deleteEntity(e2);
		socialOperation.deleteEntity(e3);

	}

	@Test
	public void readEntityTypes() throws SocialServiceException {
		Assert.assertEquals(1, sharingManager
				.getEntityTypeByName("event", null).size());
		Assert.assertEquals(0,
				sharingManager.getEntityTypeByName("dummmie", null).size());
	}

}
