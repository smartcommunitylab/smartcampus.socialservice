package eu.trentorise.smartcampus.social.engine;

import java.util.List;

import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Limit;

public interface EntityTypeOperations {

	// create
	public EntityType create(String name, String mimeType);

	// reads
	public EntityType readType(String entityId);

	public EntityType readTypeByNameAndMimeType(String name, String mimeType);
	
	public List<EntityType> readTypes(Limit limit);
	
	public List<EntityType> readTypesByName(String name, Limit limit);
	
	public List<EntityType> readTypesByMimeType(String mimeType, Limit limit);
	
	// delete
	public boolean deleteType(String GroupId);
	
}
