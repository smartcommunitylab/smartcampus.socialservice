package eu.trentorise.smartcampus.social.engine;

import java.util.List;

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.Limit;

public interface EntityOperations {

	// visibility

	public Entity saveOrUpdate(String namespace, Entity entity);

	// read

	public List<Entity> readShared(String actorId, boolean isCommunity,
			String appId, Limit limit);

	public Entity readShared(String actorId, boolean isCommunity, String uri);

	public List<Entity> readEntities(String ownerId, String communityId,
			String appId, Limit limit);

	public Entity readEntity(String uri);

}
