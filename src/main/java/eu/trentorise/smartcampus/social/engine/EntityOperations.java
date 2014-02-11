package eu.trentorise.smartcampus.social.engine;

import java.util.List;

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.Limit;

public interface EntityOperations {

	// visibility

	public boolean share(String ownerId, String communityId, Entity entity);

	public boolean unshare(String uri);

	// read

	public List<Entity> readShared(String actorId, Limit limit);

	public List<Entity> readEntities(String ownerId, String communityId,
			Limit limit);

	public Entity readEntity(String uri);

}
