package eu.trentorise.smartcampus.social.engine;

import java.util.List;

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.beans.ShareInfo;

public interface EntityOperations {

	// share

	public boolean share(String ownerId, String resourceUri, String typeId,
			ShareInfo shareInfo);

	// unshare

	public boolean unshare(String entityId);

	// read

	public List<Entity> readMyShare(String userId, ShareInfo target,
			String typeId, Limit limit);
}
