package eu.trentorise.smartcampus.social.managers;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.beans.Visibility;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml",
		"classpath:/spring/spring-security.xml" })
public class EntityTypeManagerTest {

	@Autowired
	EntityTypeManager entityTypeManager;

	@Autowired
	EntityManager entityManager;

	private static final String TYPE_NAME_1 = "SmallImage";
	private static final String TYPE_NAME_1_NN = "   SmallIMAGE   "; // Not
																		// Normalized
	private static final String TYPE_NAME_2 = "MediumImage";
	private static final String TYPE_NAME_3 = "BigImage";
	private static final String TYPE_NAME_3_NN = "   BIGImage    "; // Not
																	// Normalized
	private static final String TYPE_NAME_7 = "immage";
	private static final String TYPE_NAME_7_NN = " IMMaGE   "; // Not Normalized
	private static final String TYPE_MIME_1 = "IMAGE/jpg";
	private static final String TYPE_MIME_2 = "image/png";
	private static final String TYPE_MIME_3 = "ViDeO/mP4";
	private static final String TYPE_MINE_4 = "audio/mpx"; // Type not present
															// in allowedTypes
	private static final String TYPE_NEXT_ID = "123456";

	private EntityType type1, type2, type3, type4, type5, type6, type7, type8;

	private boolean checkTypeCreation(String name, EntityType type) {
		return type != null && type.getId() != null
				&& type.getMimeType() != null
				&& RepositoryUtils.normalizeCompare(type.getName(), name);
	}

	private boolean compareType(EntityType type1, EntityType type2) {
		return type1.getId().compareTo(type2.getId()) == 0
				&& RepositoryUtils.normalizeCompare(type1.getName(),
						type2.getName())
				&& type1.getMimeType().compareTo(type2.getMimeType()) == 0;
	}

	@Before
	public void CrateAll() {
		type1 = entityTypeManager.create(TYPE_NAME_1_NN, TYPE_MIME_1);
		type2 = entityTypeManager.create(TYPE_NAME_2, TYPE_MIME_2);
		type3 = entityTypeManager.create(TYPE_NAME_3, TYPE_MIME_3);

		// Try to recreate the same type
		type4 = entityTypeManager.create(TYPE_NAME_1, TYPE_MIME_1);
		type5 = entityTypeManager.create(TYPE_NAME_2, TYPE_MIME_2);
		type6 = entityTypeManager.create(TYPE_NAME_3_NN, TYPE_MIME_3);
	}

	@Test
	public void test1_CreateType() {
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_1, type1));
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_2, type2));
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_3, type3));

		Assert.assertTrue(compareType(type1, type4));
		Assert.assertTrue(compareType(type2, type5));
		Assert.assertTrue(compareType(type3, type6));
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void test11_CreateTypeNotAllowed() {
		// Create a type with a not allowed mimeType
		entityTypeManager.create(TYPE_NAME_1, TYPE_MINE_4);
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void test12_CreateTypeNullParam() {
		// Create a type with a not allowed mimeType
		entityTypeManager.create(null, null);
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void test13_CreateTypeVoidParam() {
		// Create a type with a not allowed mimeType
		entityTypeManager.create("", "");
	}

	@Test
	public void test2_ReadType() {
		EntityType readedType = entityTypeManager.readTypeByNameAndMimeType(
				TYPE_NAME_1, TYPE_MIME_1);
		Assert.assertTrue(RepositoryUtils.normalizeCompare(
				readedType.getName(), TYPE_NAME_1)
				&& readedType.getMimeType().compareToIgnoreCase(TYPE_MIME_1) == 0);

		readedType = entityTypeManager.readTypeByNameAndMimeType(
				TYPE_NAME_3_NN, TYPE_MIME_3);
		Assert.assertTrue(RepositoryUtils.normalizeCompare(
				readedType.getName(), TYPE_NAME_3)
				&& readedType.getMimeType().compareToIgnoreCase(TYPE_MIME_3) == 0);

		readedType = entityTypeManager.readType(type1.getId());
		Assert.assertTrue(readedType.getName().compareToIgnoreCase(
				type1.getName()) == 0);
	}

	@Test
	public void test21_ReadTypeNullParams() {
		String typeId = null;
		String typeName = null;
		String mimeType = null;
		Limit limit = null;

		EntityType readedType = entityTypeManager.readType(typeId);
		Assert.assertTrue(readedType == null);

		readedType = entityTypeManager.readTypeByNameAndMimeType(typeName,
				mimeType);
		Assert.assertTrue(readedType == null);

		List<EntityType> readedTypes = entityTypeManager.readTypesByName(
				typeName, limit);
		Assert.assertTrue(readedTypes.isEmpty());

		readedTypes = entityTypeManager.readTypesByMimeType(mimeType, limit);
		Assert.assertTrue(readedTypes.isEmpty());
	}

	@Test
	public void test22_ReadTypeIncompleteParams() {
		String typeName = null;
		String mimeType = null;

		EntityType readedType = entityTypeManager.readTypeByNameAndMimeType(
				TYPE_NAME_1, mimeType);
		Assert.assertTrue(readedType.getName().compareToIgnoreCase(TYPE_NAME_1) == 0);

		readedType = entityTypeManager.readTypeByNameAndMimeType(typeName,
				TYPE_MIME_1);
		Assert.assertTrue(readedType.getName().compareToIgnoreCase(TYPE_NAME_1) == 0);
	}

	@Test
	public void test3_ReadTypesPageableAndSort() {
		Limit limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);

		List<EntityType> readedTypes = entityTypeManager.readTypes(limit);
		Assert.assertTrue(readedTypes.size() == 3
				&& RepositoryUtils.normalizeCompare(readedTypes.get(2)
						.getName(), TYPE_NAME_3));

		type7 = entityTypeManager.create(TYPE_NAME_7_NN, TYPE_MIME_2);
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_7, type7));

		type3 = entityTypeManager.updateType(type3.getId(), TYPE_MIME_2);

		readedTypes = entityTypeManager.readTypesByMimeType(TYPE_MIME_2, limit);
		Assert.assertTrue(readedTypes.size() == 3
				&& RepositoryUtils.normalizeCompare(readedTypes.get(1)
						.getName(), TYPE_NAME_3));

		type8 = entityTypeManager.create(TYPE_NAME_1, TYPE_MIME_3);
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_1, type8));

		readedTypes = entityTypeManager.readTypesByName(TYPE_NAME_1, limit);
		Assert.assertTrue(readedTypes.size() == 2
				&& RepositoryUtils.normalizeCompare(readedTypes.get(0)
						.getName(), TYPE_NAME_1));

		// sort by name asc
		limit = new Limit();
		limit.setDirection(0);
		List<String> sortList = new ArrayList<String>();
		sortList.add("name");
		limit.setSortList(sortList);
		readedTypes = entityTypeManager.readTypes(limit);
		Assert.assertTrue(readedTypes.size() == 5
				&& RepositoryUtils.normalizeCompare(readedTypes.get(0)
						.getName(), TYPE_NAME_3));

		// sort by mimeType desc
		limit.setPage(0);
		limit.setPageSize(5);
		limit.setDirection(1);
		sortList = new ArrayList<String>();
		sortList.add("mimeType");
		limit.setSortList(sortList);
		readedTypes = entityTypeManager.readTypes(limit);
		Assert.assertTrue(readedTypes.size() == 5
				&& RepositoryUtils.normalizeCompare(readedTypes.get(0)
						.getMimeType(), TYPE_MIME_3));

		// sort by id desc
		sortList = new ArrayList<String>();
		sortList.add("id");
		limit.setSortList(sortList);
		readedTypes = entityTypeManager.readTypesByName(TYPE_NAME_1, limit);
		Assert.assertTrue(readedTypes.size() == 2
				&& RepositoryUtils.normalizeCompare(readedTypes.get(0)
						.getMimeType(), TYPE_MIME_3));

		Assert.assertTrue(entityTypeManager.deleteType(type7.getId()));
		Assert.assertTrue(entityTypeManager.deleteType(type8.getId()));

	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void test31_ReadTypesPageableAndSortErrorParam() {
		Limit limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);
		limit.setDirection(0);
		List<String> sortList = new ArrayList<String>();
		sortList.add("name_mime");
		limit.setSortList(sortList);
		entityTypeManager.readTypes(limit);
	}

	@Test
	public void test4_UpdateType() {
		type3 = entityTypeManager.updateType(type3.getId(), TYPE_MIME_1);
		Assert.assertTrue(type3.getMimeType().compareToIgnoreCase(TYPE_MIME_1) == 0);
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void test41_UpdateTypeNullParam() {
		String mimeType = null;
		type3 = entityTypeManager.updateType(type3.getId(), mimeType);
	}

	@Test
	public void test42_UpdateTypeNotExists() {
		type8 = entityTypeManager.updateType(TYPE_NEXT_ID, TYPE_MIME_1);
		Assert.assertTrue(type8 == null);
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void test43_UpdateTypeAlreadyExist() {
		type3 = entityTypeManager.updateType(type3.getId(), TYPE_MIME_3);
	}

	@After
	public void deleteAll() {
		Assert.assertTrue(entityTypeManager.deleteType(type1.getId()));
		Assert.assertTrue(entityTypeManager.deleteType(type2.getId()));
		Assert.assertTrue(entityTypeManager.deleteType(type3.getId()));
	}

	@Test
	public void test5_deleteNullParam() {
		String typeId = null;
		Assert.assertTrue(entityTypeManager.deleteType(typeId));
	}

	@Test
	public void test51_deleteNotExists() {
		Assert.assertTrue(entityTypeManager.deleteType(TYPE_NEXT_ID));
	}

	// @Test
	// I have to add a specific function in EntityRepository like this
	// @Query("SELECT COUNT(*) FROM SocialEntity se WHERE se.type = ?1")
	// public Long countEntityByType(String type);
	//
	public void test52_deleteAUsedType() {
		Entity e = new Entity();
		e.setName("MyFirstEntity");
		e.setLocalId("01");
		e.setOwner("01");
		e.setType(type1.getId());
		e.setVisibility(new Visibility(true));
		entityManager.saveOrUpdate("Sc", e);

		Assert.assertFalse(entityTypeManager.deleteType(type1.getId()));
	}

}
