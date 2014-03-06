package eu.trentorise.smartcampus.social.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import eu.trentorise.smartcampus.social.engine.CommunityOperations;
import eu.trentorise.smartcampus.social.engine.beans.Community;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.model.SocialCommunity;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;
import eu.trentorise.smartcampus.social.engine.repo.CommunityRepository;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@Component
@Transactional
public class SocialCommunityManager implements CommunityOperations {

	private static final Logger logger = Logger
			.getLogger(SocialCommunityManager.class);
	@Autowired
	CommunityRepository communityRepository;

	@Override
	public Community create(String name, String appId)
			throws IllegalArgumentException {
		name = RepositoryUtils.normalizeString(name);
		appId = RepositoryUtils.normalizeString(appId);
		if (!StringUtils.hasLength(name)) {
			throw new IllegalArgumentException("name should be valid");
		}

		if (!StringUtils.hasLength(appId)) {
			throw new IllegalArgumentException("appId should be valid");
		}

		if (communityRepository.findByNameIgnoreCase(name) != null) {
			throw new IllegalArgumentException(String.format(
					"community name %s already present", name));
		}
		logger.info(String.format("Created community %s from app %s", name,
				appId));
		return communityRepository.save(new SocialCommunity(name, appId))
				.toCommunity();
	}

	@Override
	public List<Community> readCommunities(Limit limit) {
		if (limit != null) {
			Iterable<SocialCommunity> result = null;

			PageRequest page = null;
			Sort sort = null;
			if (limit.getSortList() != null && !limit.getSortList().isEmpty()) {
				sort = new Sort(limit.getDirection() == 0 ? Direction.ASC
						: Direction.DESC, limit.getSortList());
			}

			if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
				page = new PageRequest(limit.getPage(), limit.getPageSize(),
						sort);
				sort = null;
			}

			if (limit.getFromDate() > 0 || limit.getToDate() > 0) {
				if (limit.getFromDate() <= 0) {
					limit.setFromDate(RepositoryUtils.DEFAULT_FROM_DATE);
				}
				if (limit.getToDate() <= 0) {
					limit.setToDate(RepositoryUtils.DEFAULT_TO_DATE);
				}
				if (page != null) {
					result = communityRepository.findByCreationTimeBetween(
							limit.getFromDate(), limit.getToDate(), page);
				} else {
					result = communityRepository.findByCreationTimeBetween(
							limit.getFromDate(), limit.getToDate(), sort);
				}
			} else {
				if (page != null) {
					result = communityRepository.findAll(page).getContent();
				} else {
					result = communityRepository.findAll(sort);
				}
			}
			return SocialCommunity.toCommunity(result);
		} else {
			return SocialCommunity.toCommunity(communityRepository.findAll());
		}
	}

	@Override
	public Community readCommunity(String communityId) {
		SocialCommunity community = communityRepository.findOne(RepositoryUtils
				.convertId(communityId));
		return community == null ? null : community.toCommunity();
	}

	@Override
	public boolean addMembers(String communityId, Set<String> members) {
		SocialCommunity community = communityRepository.findOne(RepositoryUtils
				.convertId(communityId));
		boolean result = true;
		if (community != null) {
			Set<SocialUser> users = new HashSet<SocialUser>();
			if (members != null) {
				for (String member : members) {
					users.add(new SocialUser(member));
				}
			}
			result = community.getMembers().addAll(users);
			communityRepository.save(community);
		}
		return result;
	}

	@Override
	public boolean removeMembers(String communityId, Set<String> members) {
		SocialCommunity community = communityRepository.findOne(RepositoryUtils
				.convertId(communityId));
		boolean result = true;
		if (community != null) {
			Set<SocialUser> users = new HashSet<SocialUser>();
			if (members != null) {
				for (String member : members) {
					users.add(new SocialUser(member));
				}
			}
			result = community.getMembers().removeAll(users);
			communityRepository.save(community);
		}
		return result;
	}

	@Override
	public boolean delete(String communityId) {
		communityRepository.delete(RepositoryUtils.convertId(communityId));
		logger.info(String.format("Deleted community %s", communityId));
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> readMembers(String communityId, Limit limit) {
		Community community = readCommunity(communityId);
		if (community != null) {
			if (limit != null) {
				return new HashSet<String>(
						RepositoryUtils.getSublistPagination(
								new ArrayList<String>(community.getMemberIds()),
								limit));
			} else {
				return community.getMemberIds();
			}
		} else {
			logger.warn(String.format("Community %s not exist", communityId));
		}
		return Collections.<String> emptySet();
	}

	@Override
	public List<Community> readCommunitiesByAppId(String appId, Limit limit)
			throws IllegalArgumentException {

		appId = RepositoryUtils.normalizeString(appId);
		if (!StringUtils.hasLength(appId)) {
			throw new IllegalArgumentException("appId should be valid");
		}
		if (limit != null) {
			List<SocialCommunity> result = null;

			PageRequest page = null;
			if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
				page = new PageRequest(limit.getPage(), limit.getPageSize());
			}
			if (limit.getFromDate() > 0 && limit.getToDate() > 0) {
				result = communityRepository.findByAppIdAndCreationTimeBetween(
						appId, limit.getFromDate(), limit.getToDate(), page);
			} else if (limit.getFromDate() > 0) {
				result = communityRepository
						.findByAppIdAndCreationTimeGreaterThan(appId,
								limit.getFromDate(), page);
			} else if (limit.getToDate() > 0) {
				result = communityRepository
						.findByAppIdAndCreationTimeLessThan(appId,
								limit.getToDate(), page);
			} else {
				result = communityRepository.findAll(page).getContent();
			}
			return SocialCommunity.toCommunity(result);
		} else {
			return SocialCommunity.toCommunity(communityRepository
					.findByAppId(appId));
		}
	}

}
