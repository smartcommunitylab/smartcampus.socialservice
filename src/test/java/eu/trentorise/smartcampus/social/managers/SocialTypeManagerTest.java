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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class SocialTypeManagerTest {
	
	@Autowired
	SocialTypeManager entityTypeMamager;
	
	private static final String TYPE_NAME_1 = "SmallImage";
	private static final String TYPE_NAME_2 = "MediumImage";
	private static final String TYPE_NAME_3 = "BigImage";
	private static final String TYPE_MIME_1 = "jpg";
	private static final String TYPE_MIME_2 = "png";
	private static final String TYPE_MIME_3 = "tiff";
	
	private EntityType type1, type2, type3, type4, type5, type6;
	
	private boolean checkTypeCreation(String name, EntityType type){
		return type != null && type.getId() != null
				&& type.getMimeType() != null
				&& type.getName().equals(name);
	}
	
	@Before
	public void CrateAll(){
		type1 = entityTypeMamager.create(TYPE_NAME_1, TYPE_MIME_1);
		type2 = entityTypeMamager.create(TYPE_NAME_2, TYPE_MIME_2);
		type3 = entityTypeMamager.create(TYPE_NAME_3, TYPE_MIME_3);
		
		//Try to recreate the same type
//		type4 = entityTypeMamager.create(TYPE_NAME_1, TYPE_MIME_1);
//		type5 = entityTypeMamager.create(TYPE_NAME_2, TYPE_MIME_2);
//		type6 = entityTypeMamager.create(TYPE_NAME_3, TYPE_MIME_3);
	}
	
	@Test
	public void CreateType(){
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_1, type1));
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_2, type2));
		Assert.assertTrue(checkTypeCreation(TYPE_NAME_3, type3));
	}
	
	@Test
	public void ReadTypes(){
		Limit limit = new Limit();
		limit.setPage(0);
		limit.setPageSize(5);
		
		EntityType readedType = entityTypeMamager.readTypeByNameAndMimeType(TYPE_NAME_1, TYPE_MIME_1);
		Assert.assertTrue(readedType.getName().compareTo(TYPE_NAME_1) == 0 && readedType.getMimeType().compareTo(TYPE_MIME_1) == 0);
		
		List<EntityType> readedTypes = entityTypeMamager.readTypes(limit);
		Assert.assertTrue(readedTypes.size() == 3 && readedTypes.get(2).getName().compareTo(TYPE_NAME_3) == 0);
		
	}
	
	@After
	public void deleteAll(){
		Assert.assertTrue(entityTypeMamager.deleteType(type1.getId()));
		Assert.assertTrue(entityTypeMamager.deleteType(type2.getId()));
		Assert.assertTrue(entityTypeMamager.deleteType(type3.getId()));
	}
}
