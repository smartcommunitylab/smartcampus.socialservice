package eu.trentorise.smartcampus.social.managers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.trentorise.smartcampus.social.engine.EntityOperations;
import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.repo.EntityRepository;

@Component
public class EntityManager implements EntityOperations {

	@Autowired
	EntityRepository entityRepository;

	@Override
	public boolean share(String ownerId, String communityId, Entity entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unshare(String uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Entity> readShared(String actorId, Limit limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> readEntities(String ownerId, String communityId,
			Limit limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity readEntity(String uri) {
		return entityRepository.findOne(uri);
	}

}
