package eu.trentorise.smartcampus.social.managers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.social.engine.EntityTypeOperations;
import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.model.SocialType;
import eu.trentorise.smartcampus.social.engine.repo.SocialTypeRepository;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@Component
@Transactional
public class SocialTypeManager implements EntityTypeOperations{

	@Autowired 
	SocialTypeRepository typeRepository;
	
	private static final Logger logger = Logger.getLogger(SocialGroupManager.class);
	
	@Override
	public EntityType create(String name, String mimeType) {
		SocialType newType = null;
		EntityType createdType = null;
		List<SocialType> findedTypes = typeRepository.findByNameAndMimeType(name, mimeType);
		if(findedTypes == null || findedTypes.size() == 0){	//If there is not a type like the "newType" i create it
			newType = new SocialType(name, mimeType);
			createdType = typeRepository.save(newType).toEntityType();
			if(createdType != null){
				logger.info("Successfully create new type '" + name + "'.");
			} else {
				logger.error("Error in new type '" + name + "' creation.");
			}
		} else {					//else I use the existing type
			logger.warn("Type '" + name + "' already exists.");
			createdType = findedTypes.get(0).toEntityType();
		}
		return createdType;
	}

	@Override
	public EntityType readType(String entityTypeId) {
		SocialType readedType = typeRepository.findOne(RepositoryUtils.convertId(entityTypeId));
		if(readedType != null){
			return readedType.toEntityType();
		}
		logger.error("No type found with id " + entityTypeId + ".");
		return null;
	}
	
	@Override
	public EntityType readTypeByNameAndMimeType(String name, String mimeType) {
		List<SocialType> findedTypes = typeRepository.findByNameAndMimeType(name, mimeType);
		SocialType readedType = null;
		if(findedTypes != null && findedTypes.size() > 0){
			readedType = findedTypes.get(0);
			return readedType.toEntityType();
		}
		logger.error("No type found with name '" + name + "' and mimeType '" + mimeType + "'.");
		return null;
	}

	@Override
	public List<EntityType> readTypes(Limit limit) {
		PageRequest page = null;
		if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
			page = new PageRequest(limit.getPage(), limit.getPageSize());
		}
		List<SocialType> readedType = typeRepository.findAll(page).getContent();
		if(readedType == null){
			logger.warn("No entityType found in db");
			return null;
		}
		return SocialType.toEntityType(readedType);
	}

	@Override
	public List<EntityType> readTypesByName(String name, Limit limit) {
		PageRequest page = null;
		if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
			page = new PageRequest(limit.getPage(), limit.getPageSize());
		}
		List<SocialType> readedType = typeRepository.findByName(name, page);
		if(readedType == null){
			logger.warn("No entityType found with name " + name);
			return null;
		}
		return SocialType.toEntityType(readedType);
	}

	@Override
	public List<EntityType> readTypesByMimeType(String mimeType, Limit limit) {
		PageRequest page = null;
		if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
			page = new PageRequest(limit.getPage(), limit.getPageSize());
		}
		List<SocialType> readedType = typeRepository.findByMimeType(mimeType, page);
		if(readedType == null){
			logger.warn("No entityType found with mimeType " + mimeType);
			return null;
		}
		return SocialType.toEntityType(readedType);
	}

	//Only for tests
	@Override
	public boolean deleteType(String entityTypeId) {
		SocialType type = typeRepository.findOne(RepositoryUtils.convertId(entityTypeId));
		if(type != null){
			typeRepository.delete(RepositoryUtils.convertId(entityTypeId));
			logger.info("EntityType " + entityTypeId + " correctly removed.");
		} else {
			logger.warn("No entityType found with id " + entityTypeId);
		}
		return true;
	}

}
