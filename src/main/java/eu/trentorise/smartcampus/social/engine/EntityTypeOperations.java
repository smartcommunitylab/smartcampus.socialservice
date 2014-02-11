package eu.trentorise.smartcampus.social.engine;

import eu.trentorise.smartcampus.social.engine.beans.EntityType;

public interface EntityTypeOperations {

	// create

	public EntityType create(String name);

	// reads
	public EntityType readType(String entityId);

	public EntityType readTypes();
}
