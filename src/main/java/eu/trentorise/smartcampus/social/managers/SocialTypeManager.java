package eu.trentorise.smartcampus.social.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import eu.trentorise.smartcampus.social.engine.EntityTypeOperations;
import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.model.SocialType;
import eu.trentorise.smartcampus.social.engine.repo.SocialTypeRepository;
import eu.trentorise.smartcampus.social.engine.utils.CustomStringList;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@Component
@Transactional
public class SocialTypeManager implements EntityTypeOperations {

	private static final ArrayList<String> allowedMimeType = new CustomStringList(
			Arrays.asList("image/jpg", "image/gif", "image/tif", "image/png", "image/bmp",
					"audio/aif", "audio/iff", "audio/m3u", "audio/m4a", "audio/mid",
					"audio/mp3", "audio/mpa", "audio/ra", "audio/wav",
					"audio/wma", "video/avi", "video/3gp", "video/asx",
					"video/flv", "video/m4v", "video/mov", "video/mp4",
					"video/mpg", "video/rm", "video/srt", "video/swf",
					"video/vob", "video/wmv", "application/zip", "text/doc",
					"text/txt", "text/xml", "text/rtf"));
	@Autowired
	SocialTypeRepository typeRepository;

	private static final Logger logger = Logger
			.getLogger(SocialGroupManager.class);

	@Override
	public EntityType create(String name, String mimeType) {
		SocialType newType = null;
		EntityType createdType = null;
		if(!StringUtils.hasLength(name)){ 
			throw new IllegalArgumentException(String.format("param 'name' should be valid."));
		}
		
		if(!StringUtils.hasLength(mimeType)){ 
			throw new IllegalArgumentException(String.format("param 'mimeType' should be valid."));
		}
		
		String normalizedName = RepositoryUtils.normalizeString(name);
		List<SocialType> findedTypes = typeRepository
				.findByNameIgnoreCaseAndMimeType(normalizedName, mimeType);
		if (findedTypes == null || findedTypes.size() == 0) { 	// If there is not a type like
																// the "newType" i create it
			newType = new SocialType(normalizedName, mimeType);
			if (checkMimeType(mimeType)) {
				createdType = typeRepository.save(newType).toEntityType();
				if (createdType != null) {
					logger.info(String.format("Successfully create new type '%s'.", normalizedName));
				} else {
					logger.error(String.format("Error in new type '%s' creation.", normalizedName));
				}
			} else {
				//logger.error(String.format("Error in new type '%s' creation. MimeType '%s' not allowed.", normalizedName, mimeType));
				throw new IllegalArgumentException(String.format("MimeType '%s'exception. Not allowed.", mimeType));
			}
		} else { // else I use the existing type
			logger.warn(String.format("Type '%s' already exists.", normalizedName));
			createdType = findedTypes.get(0).toEntityType();
		}
		return createdType;
	}

	@Override
	public EntityType readType(String entityTypeId) {
		SocialType readedType = typeRepository.findOne(RepositoryUtils
				.convertId(entityTypeId));
		if (readedType != null) {
			return readedType.toEntityType();
		}
		logger.error(String.format("No type found with id %s.", entityTypeId));
		return null;
	}

	@Override
	public EntityType updateType(String entityTypeId, String mimeType) {
		SocialType updatedType = null;
		SocialType readedType = typeRepository.findOne(RepositoryUtils
				.convertId(entityTypeId));
		if (readedType != null) {
			readedType.setMimeType(mimeType);
			updatedType = typeRepository.save(readedType);
			logger.info(String.format("Type '%s' correctly updated.",entityTypeId));
			return updatedType.toEntityType();
		}
		logger.error(String.format("No type found with id %s .", entityTypeId));
		return null;
	}

	@Override
	public EntityType readTypeByNameAndMimeType(String name, String mimeType) {
		String normalizedName = RepositoryUtils.normalizeString(name);
		List<SocialType> findedTypes = typeRepository
				.findByNameIgnoreCaseAndMimeType(normalizedName, mimeType);
		SocialType readedType = null;
		if (findedTypes != null && findedTypes.size() > 0) {
			readedType = findedTypes.get(0);
			return readedType.toEntityType();
		}
		logger.error(String.format("No type found with name '%s' and mimeType '%s'.", normalizedName, mimeType));
		return null;
	}

	@Override
	public List<EntityType> readTypes(Limit limit) {
		PageRequest page = null;
		List<SocialType> readedType = null;
		if(limit != null){
			if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
				page = new PageRequest(limit.getPage(), limit.getPageSize());
			}
		}
		readedType = typeRepository.findAll(page).getContent();
		if (readedType == null) {
			logger.warn("No entityType found in db");
			return null;
		}
		return SocialType.toEntityType(readedType);
	}

	@Override
	public List<EntityType> readTypesByName(String name, Limit limit) {
		PageRequest page = null;
		String normalizedName = RepositoryUtils.normalizeString(name);
		if(limit != null){
			if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
				page = new PageRequest(limit.getPage(), limit.getPageSize());
			}
		}
		List<SocialType> readedType = typeRepository.findByNameIgnoreCase(
				normalizedName, page);
		if (readedType == null) {
			logger.warn(String.format("No entityType found with name '%s'.", normalizedName));
			return null;
		}
		return SocialType.toEntityType(readedType);
	}

	@Override
	public List<EntityType> readTypesByMimeType(String mimeType, Limit limit) {
		PageRequest page = null;
		if(limit != null){
			if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
				page = new PageRequest(limit.getPage(), limit.getPageSize());
			}
		}
		List<SocialType> readedType = typeRepository.findByMimeType(mimeType,
				page);
		if (readedType == null) {
			logger.warn(String.format("No entityType found with mimeType '%s.'", mimeType));
			return null;
		}
		return SocialType.toEntityType(readedType);
	}

	// Only for tests
	@Override
	public boolean deleteType(String entityTypeId) {
		SocialType type = typeRepository.findOne(RepositoryUtils
				.convertId(entityTypeId));
		if (type != null) {
			typeRepository.delete(RepositoryUtils.convertId(entityTypeId));
			logger.info(String.format("EntityType %s correctly removed.", entityTypeId));
		} else {
			logger.warn(String.format("No entityType found with id %s.", entityTypeId));
		}
		return true;
	}

	/**
	 * Method checkMimeType: used to verify if a new mimeType is contained in
	 * the available list
	 * 
	 * @param mimeType
	 *            : type to check;
	 * @return boolean true if type correct, false in all other case
	 */
	private boolean checkMimeType(String mimeType) {
		return mimeType != null && allowedMimeType.contains(mimeType.toLowerCase());
	}

}
