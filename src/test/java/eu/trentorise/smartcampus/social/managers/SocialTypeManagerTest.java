package eu.trentorise.smartcampus.social.managers;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class SocialTypeManagerTest {
	
	@Autowired
	SocialTypeManager entityTypeManager;
	
	private static final String TYPE_NAME_1 = "SmallImage";
	private static final String TYPE_NAME_1_NN = "   SmallIMAGE   ";	//Not Normalized
	private static final String TYPE_NAME_2 = "MediumImage";
	private static final String TYPE_NAME_3 = "BigImage";
	private static final String TYPE_NAME_3_NN = "   BIGImage    ";		//Not Normalized
	private static final String TYPE_NAME_7 = "immage";
	private static final String TYPE_NAME_7_NN = " IMMaGE   ";			//Not Normalized
	private static final String TYPE_MIME_1 = "IMAGE/jpg";
	private static final String TYPE_MIME_2 = "image/png";
	private static final String TYPE_MIME_3 = "ViDeO/mP4";
	private static final String TYPE_MINE_4 = "audio/mp3";				//Type not present in allowedTypes
	
	private EntityType type1, type2, type3, type4, type5, type6, type7, type8, type9;
	
	private boolean checkTypeCreation(String name, EntityType type){
		return type != null && type.getId() != null
				&& type.getMimeType() != null
				&& RepositoryUtils.normalizeCompare(type.getName(), name);
	}
	
	private boolean compareType(EntityType type1, EntityType type2){
		return type1.getId().compareTo(type2.getId()) == 0
				&& RepositoryUtils.normalizeCompare(type1.getName(), type2.getName())
				&& type1.getMimeType().compareTo(type2.getMimeType()) == 0;
	}
	
	@Before
	public void CrateAll(){
		type1 = entityTypeManager.create(TYPE_NAME_1_NN, TYPE_MIME_1);
		type2 = entityTypeManager.create(TYPE_NAME_2, TYPE_MIME_2);
		type3 = entityTypeManager.create(TYPE_NAME_3, TYPE_MIME_3);
		
		//Try to recreate the same type
		type4 = entityTypeManager.create(TYPE_NAME_1, TYPE_MIME_1);
		type5 = entityTypeManager.create(TYPE_NAME_2, TYPE_MIME_2);
		type6 = entityTypeManager.create(TYPE_NAME_3_NN, TYPE_MIME_3);
	}
	
	@Test
	public void CreateType(){
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_1, type1));
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_2, type2));
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_3, type3));
		
		Assert.assertTrue(compareType(type1, type4));
		Assert.assertTrue(compareType(type2, type5));
		Assert.assertTrue(compareType(type3, type6));
	}
	
	@Test
	public void ReadTypes(){
		Limit limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);
		
		EntityType readedType = entityTypeManager.readTypeByNameAndMimeType(TYPE_NAME_1, TYPE_MIME_1);
		Assert.assertTrue(RepositoryUtils.normalizeCompare(readedType.getName(), TYPE_NAME_1) && readedType.getMimeType().compareTo(TYPE_MIME_1) == 0);
		
		readedType = entityTypeManager.readTypeByNameAndMimeType(TYPE_NAME_3_NN, TYPE_MIME_3);
		Assert.assertTrue(RepositoryUtils.normalizeCompare(readedType.getName(),TYPE_NAME_3) && readedType.getMimeType().compareTo(TYPE_MIME_3) == 0);
		
		List<EntityType> readedTypes = entityTypeManager.readTypes(limit);
		Assert.assertTrue(readedTypes.size() == 3 && RepositoryUtils.normalizeCompare(readedTypes.get(2).getName(),TYPE_NAME_3));
		
		type7 = entityTypeManager.create(TYPE_NAME_7_NN, TYPE_MIME_2);
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_7, type7));
		
		type3 = entityTypeManager.updateType(type3.getId(), TYPE_MIME_2);
		
		readedTypes = entityTypeManager.readTypesByMimeType(TYPE_MIME_2, limit);
		Assert.assertTrue(readedTypes.size() == 3 && RepositoryUtils.normalizeCompare(readedTypes.get(1).getName(),TYPE_NAME_3));
		
		type8 = entityTypeManager.create(TYPE_NAME_1, TYPE_MIME_3);
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_1, type8));
		
		readedTypes = entityTypeManager.readTypesByName(TYPE_NAME_1, limit);
		Assert.assertTrue(readedTypes.size() == 2 && RepositoryUtils.normalizeCompare(readedTypes.get(0).getName(),TYPE_NAME_1));
		
		//Create a type with a not allowed mimeType
		type9 = entityTypeManager.create(TYPE_NAME_1, TYPE_MINE_4);
		Assert.assertTrue(type9 == null);
		
		Assert.assertTrue(entityTypeManager.deleteType(type7.getId()));
		Assert.assertTrue(entityTypeManager.deleteType(type8.getId()));
		
	}
	
	@After
	public void deleteAll(){
		Assert.assertTrue(entityTypeManager.deleteType(type1.getId()));
		Assert.assertTrue(entityTypeManager.deleteType(type2.getId()));
		Assert.assertTrue(entityTypeManager.deleteType(type3.getId()));
	}
}
