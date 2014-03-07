package eu.trentorise.smartcampus.social.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mapping.PropertyReferenceException;
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
			Arrays.asList("image/jpg", "image/gif", "image/tif", "image/png",
					"image/bmp", "image/psd", "image/tbm", "image/drw",
					"image/ps", "image/jpx", "image/raw", "image/ppm",
					"image/webp", "image/hdr", "image/pam", "audio/aif",
					"audio/iff", "audio/m3u", "audio/m4a", "audio/mid",
					"audio/mp3", "audio/mpa", "audio/ra", "audio/wav",
					"audio/wma", "video/avi", "video/3gp", "video/asx",
					"video/flv", "video/m4v", "video/mov", "video/mp4",
					"video/mpg", "video/rm", "video/srt", "video/swf",
					"video/vob", "video/wmv", "application/zip", "text/doc",
					"text/plain", "text/txt", "text/xml", "text/rtf",
					"text/pdf"));
	private static final String[] SORTEABLE_PARAMS = { "id", "name", "mimeType" };
	@Autowired
	SocialTypeRepository typeRepository;

	@Autowired
	private EntityManager entityManager;

	private static final Logger logger = Logger
			.getLogger(SocialGroupManager.class);

	@Override
	public EntityType create(String name, String mimeType) {
		SocialType newType = null;
		EntityType createdType = null;
		if (!StringUtils.hasLength(name)) {
			throw new IllegalArgumentException(
					String.format("param 'name' should be valid."));
		}

		if (!StringUtils.hasLength(mimeType)) {
			throw new IllegalArgumentException(
					String.format("param 'mimeType' should be valid."));
		}

		String normalizedName = RepositoryUtils.normalizeString(name);
		String normalizedMimeType = RepositoryUtils
				.normalizeStringLowerCase(mimeType);
		List<SocialType> findedTypes = typeRepository
				.findByNameIgnoreCaseAndMimeType(normalizedName,
						normalizedMimeType);
		if (findedTypes == null || findedTypes.size() == 0) { // If there is not
																// a type like
																// the "newType"
																// i create it
			newType = new SocialType(normalizedName, normalizedMimeType);
			if (checkMimeType(mimeType)) {
				createdType = typeRepository.save(newType).toEntityType();
				if (createdType != null) {
					logger.info(String.format(
							"Successfully create new type '%s'.",
							normalizedName));
				} else {
					logger.error(String.format(
							"Error in new type '%s' creation.", normalizedName));
				}
			} else {
				String exceptionMessage = String.format(
						"MimeType '%s'exception. Not allowed.",
						normalizedMimeType);
				logger.error(exceptionMessage);
				throw new IllegalArgumentException(exceptionMessage);
			}
		} else { // else I use the existing type
			logger.warn(String.format("Type '%s' already exists.",
					normalizedName));
			createdType = findedTypes.get(0).toEntityType();
		}
		return createdType;
	}

	@Override
	public EntityType readType(String entityTypeId) {
		if (StringUtils.hasLength(entityTypeId)) {
			SocialType readedType = typeRepository.findOne(RepositoryUtils
					.convertId(entityTypeId));
			if (readedType != null) {
				return readedType.toEntityType();
			}
			logger.error(String.format("No type found with id %s.",
					entityTypeId));
		} else {
			logger.error(String.format("Void type id passed to the function."));
		}
		return null;
	}

	// Only for tests
	@Override
	public EntityType updateType(String entityTypeId, String mimeType) {
		if (!StringUtils.hasLength(entityTypeId)) {
			throw new IllegalArgumentException(
					String.format("param 'entityTypeId' should be valid."));
		}
		if (!StringUtils.hasLength(mimeType)) {
			throw new IllegalArgumentException(
					String.format("param 'mimeType' should be valid."));
		}
		if (!checkMimeType(mimeType)) {
			throw new IllegalArgumentException(String.format(
					"MimeType '%s'exception. Not allowed.", mimeType));
		}

		SocialType updatedType = null;
		String normalizedMimeType = RepositoryUtils
				.normalizeStringLowerCase(mimeType);
		SocialType readedType = typeRepository.findOne(RepositoryUtils
				.convertId(entityTypeId));
		if (readedType != null) {
			String name = readedType.getName();
			if (readTypeByNameAndMimeType(name, mimeType) != null) {
				String exceptionMessage = String
						.format("A type with name '%s' and mimeType '%s' already present.",
								name, normalizedMimeType);
				logger.error(exceptionMessage);
				throw new IllegalArgumentException(exceptionMessage);
			}
			readedType.setMimeType(normalizedMimeType);
			updatedType = typeRepository.save(readedType);
			logger.info(String.format("Type '%s' correctly updated.",
					entityTypeId));
			return updatedType.toEntityType();
		}
		logger.error(String.format("No type found with id %s .", entityTypeId));
		return null;
	}

	@Override
	public EntityType readTypeByNameAndMimeType(String name, String mimeType) {
		List<EntityType> readedTypes = null;
		if (StringUtils.hasLength(name) && StringUtils.hasLength(mimeType)) {
			String normalizedName = RepositoryUtils.normalizeString(name);
			List<SocialType> findedTypes = typeRepository
					.findByNameIgnoreCaseAndMimeType(normalizedName, mimeType);
			SocialType readedType = null;
			if (findedTypes != null && findedTypes.size() > 0) {
				readedType = findedTypes.get(0);
				return readedType.toEntityType();
			}
			logger.error(String.format(
					"No type found with name '%s' and mimeType '%s'.",
					normalizedName, mimeType));
		}
		if (!StringUtils.hasLength(name) && StringUtils.hasLength(mimeType)) {
			readedTypes = readTypesByMimeType(mimeType, null);
			return ((readedTypes != null) && (!readedTypes.isEmpty()) ? readedTypes
					.get(0) : null);
		}
		if (!StringUtils.hasLength(mimeType) && StringUtils.hasLength(name)) {
			readedTypes = readTypesByName(name, null);
			return ((readedTypes != null) && (!readedTypes.isEmpty()) ? readedTypes
					.get(0) : null);
		}
		logger.error(String
				.format("Void type 'name' and 'mimeType' passed to the function."));
		return null;
	}

	@Override
	public List<EntityType> readTypes(Limit limit) {
		PageRequest page = null;
		List<SocialType> readedTypes = null;
		if (limit != null) {
			if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
				if (limit.getSortList() != null
						&& !limit.getSortList().isEmpty()) {
					Sort sort = new Sort(
							limit.getDirection() == 0 ? Direction.ASC
									: Direction.DESC, limit.getSortList());
					page = new PageRequest(limit.getPage(),
							limit.getPageSize(), sort);
				} else {
					page = new PageRequest(limit.getPage(), limit.getPageSize());
				}
			}
		}
		try {
			readedTypes = typeRepository.findAll(page).getContent();
		} catch (PropertyReferenceException pre) {
			String exceptionMessage = String
					.format("Property reference exception in sorting operation. Property '%s' not exists. Use %s instead.",
							RepositoryUtils.getParamFromException(pre
									.getMessage()), RepositoryUtils
									.concatStringParams(SORTEABLE_PARAMS));
			logger.error(exceptionMessage);
			throw new IllegalArgumentException(exceptionMessage);
		}
		if (readedTypes == null) {
			logger.warn("No entityType found in db");
			return null;
		}
		return SocialType.toEntityType(readedTypes);
	}

	@Override
	public List<EntityType> readTypesByName(String name, Limit limit) {
		PageRequest page = null;
		List<SocialType> readedTypes = null;
		if (StringUtils.hasLength(name)) {
			String normalizedName = RepositoryUtils.normalizeString(name);
			if (limit != null) {
				if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
					if (limit.getSortList() != null
							&& !limit.getSortList().isEmpty()) {
						Sort sort = new Sort(
								limit.getDirection() == 0 ? Direction.ASC
										: Direction.DESC, limit.getSortList());
						page = new PageRequest(limit.getPage(),
								limit.getPageSize(), sort);
					} else {
						page = new PageRequest(limit.getPage(),
								limit.getPageSize());
					}
				}
			}
			try {
				readedTypes = typeRepository.findByNameIgnoreCase(
						normalizedName, page);
			} catch (PropertyReferenceException pre) {
				String exceptionMessage = String
						.format("Property reference exception in sorting operation. Property '%s' not exists. Use %s instead.",
								RepositoryUtils.getParamFromException(pre
										.getMessage()), RepositoryUtils
										.concatStringParams(SORTEABLE_PARAMS));
				logger.error(exceptionMessage);
				throw new IllegalArgumentException(exceptionMessage);
			}
			if (readedTypes == null) {
				logger.warn(String.format(
						"No entityType found with name '%s'.", normalizedName));
				return null;
			}
		} else {
			logger.error("Void type 'name' passed to the function.");
		}
		return SocialType.toEntityType(readedTypes);

	}

	@Override
	public List<EntityType> readTypesByMimeType(String mimeType, Limit limit) {
		PageRequest page = null;
		List<SocialType> readedTypes = null;
		if (StringUtils.hasLength(mimeType)) {
			if (limit != null) {
				if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
					if (limit.getSortList() != null
							&& !limit.getSortList().isEmpty()) {
						Sort sort = new Sort(
								limit.getDirection() == 0 ? Direction.ASC
										: Direction.DESC, limit.getSortList());
						page = new PageRequest(limit.getPage(),
								limit.getPageSize(), sort);
					} else {
						page = new PageRequest(limit.getPage(),
								limit.getPageSize());
					}
				}
			}
			try {
				readedTypes = typeRepository.findByMimeType(mimeType, page);
			} catch (PropertyReferenceException pre) {
				String exceptionMessage = String
						.format("Property reference exception in sorting operation. Property '%s' not exists. Use %s instead.",
								RepositoryUtils.getParamFromException(pre
										.getMessage()), RepositoryUtils
										.concatStringParams(SORTEABLE_PARAMS));
				logger.error(exceptionMessage);
				throw new IllegalArgumentException(exceptionMessage);
			}
			if (readedTypes == null) {
				logger.warn(String.format(
						"No entityType found with mimeType '%s.'", mimeType));
				return null;
			}
		} else {
			logger.error("Void type 'mimeType' passed to the function.");
		}
		return SocialType.toEntityType(readedTypes);
	}

	// Only for tests
	@Override
	public boolean deleteType(String entityTypeId) {
		if (StringUtils.hasLength(entityTypeId)) {
			SocialType type = typeRepository.findOne(RepositoryUtils
					.convertId(entityTypeId));
			if (type != null) {
				// here I have to check if the type is used in a specific entity
				// Long typeUsedIn =
				// entityManager.entityRepository.countEntityByType(entityTypeId);
				typeRepository.delete(RepositoryUtils.convertId(entityTypeId));
				logger.info(String.format("EntityType %s correctly removed.",
						entityTypeId));
			} else {
				logger.warn(String.format("No entityType found with id %s.",
						entityTypeId));
			}
		} else {
			logger.error(String.format("Void type id passed to the function."));
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
		return mimeType != null
				&& allowedMimeType.contains(mimeType.toLowerCase());
	}

}
