package eu.trentorise.smartcampus.social.managers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.social.engine.GroupOperations;
import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.beans.User;
import eu.trentorise.smartcampus.social.engine.model.SocialGroup;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;
import eu.trentorise.smartcampus.social.engine.repo.GroupRepository;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@Component
@Transactional
public class SocialGroupManager implements GroupOperations {

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	SocialUserManager userManager;
	
	private static final Logger logger = Logger.getLogger(SocialGroupManager.class);
	
	@Override
	public Group create(String userId, String name) {
		String normalizedName = RepositoryUtils.normalizeString(name);
		SocialGroup created_group = null;
		SocialGroup search_group = null;
		// load user
		SocialUser user = userManager.readSocialUser(userId);
		if (user == null) {
			user = new SocialUser(userId);
			logger.info("User with id " + userId + " not present. Added to DB.");
		}
		// verify if a group with same name* already exist (* ignoring case)
		search_group = groupRepository.findByCreatorIdAndNameIgnoreCase(userId, normalizedName);
		if(search_group != null){
			logger.warn("Group with name '" + normalizedName + "' already present in DB.");
			return search_group.toGroup();
		}
		// create new group
		SocialGroup group = new SocialGroup(normalizedName, user);
		created_group = groupRepository.save(group);
		if(created_group != null){
			logger.info("New group with id " + created_group.getId() + " correctly created.");
		} else {
			logger.error("Error in creating new group with id " + group.getId() + ".");
		}
		return created_group.toGroup();
	}
	
	@Override
	public List<Group> readGroups(Limit limit) {
		if (limit != null) {
			List<SocialGroup> groups = null;

			PageRequest page = null;
			if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
				page = new PageRequest(limit.getPage(), limit.getPageSize());
			}
			if (limit.getFromDate() > 0 && limit.getToDate() > 0) {
				groups = groupRepository.findByCreationTimeBetween(
						limit.getFromDate(), limit.getToDate(), page);
			} else if (limit.getFromDate() > 0) {
				groups = groupRepository.findByCreationTimeGreaterThan(
						limit.getFromDate(), page);
			} else if (limit.getToDate() > 0) {
				groups = groupRepository.findByCreationTimeLessThan(
						limit.getToDate(), page);
			} else {
				groups = groupRepository.findAll(page).getContent();
			}
			return SocialGroup.toGroup(groups);
		} else {
			return SocialGroup.toGroup(groupRepository.findAll());
		}
	}

	@Override
	public List<Group> readGroups(String userId, Limit limit) {
		// load user
		SocialUser user = userManager.readSocialUser(userId);
		if(user != null){
			if(limit != null){
				List<SocialGroup> groups = null;
				
				Pageable pageable = new PageRequest(limit.getPage(), limit.getPageSize());
				if(limit.getFromDate() > 0 && limit.getToDate() > 0){
					groups = groupRepository.findByCreatorIdAndCreationTimeBetween(userId, limit.getFromDate(), limit.getToDate(), pageable);
				} else if(limit.getFromDate() > 0){
					groups = groupRepository.findByCreatorIdAndCreationTimeGreaterThan(userId, limit.getFromDate(), pageable);
				} else if(limit.getToDate() > 0){
					groups = groupRepository.findByCreatorIdAndCreationTimeLessThan(userId, limit.getToDate(), pageable);
				} else {
					groups = groupRepository.findByCreatorId(userId, pageable);
				}
				return SocialGroup.toGroup(groups);
			} else {
				logger.warn("No limit specified for this search.");
				return SocialGroup.toGroup(groupRepository.findByCreatorId(userId));
			}
		} else {
			//throw new IllegalArgumentException(userId + " not exists");
			logger.error("User with id " + userId + " not exists.");
			return null;
		}
	}

	@Override
	public Group readGroup(String groupId) {
		SocialGroup result = groupRepository.findOne(SocialGroup
				.convertId(groupId));
		if(result == null){
			logger.error("Group with id " + groupId + " not exists.");
		}
		return result != null ? result.toGroup() : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> readMembers(String groupId, Limit limit) {
		//For the limit I consider only the page and the page size because the from/to date is not applicable
		SocialGroup result = groupRepository.findOne(SocialGroup
				.convertId(groupId));
		if(result != null){
			if(result.getMembers().size() == 0){
				logger.warn("No members found for the group " + groupId + ".");
				return null;
			}
			List<User> members = new ArrayList<User>();
			for (SocialUser su:result.getMembers()){
				members.add(su.toUser());
			}
			//To sort the list
			List<User> unsorted = members.subList(0, members.size());	//Get all the list
			members = RepositoryUtils.asSortedList(unsorted);
			
			if(limit != null){
				return (List<User>) RepositoryUtils.getSublistPagination(members, limit);
			} else {
				logger.warn("No limit specified for this search.");
				return members;
			}
		} else {
			logger.error("Group with id " + groupId + " not exists.");
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> readMembersAsString(String groupId, Limit limit) {
		//For the limit I consider only the page and the page size because the from/to date is not applicable
		SocialGroup result = groupRepository.findOne(SocialGroup
				.convertId(groupId));
		if(result != null){
			if(result.getMembers().size() == 0){
				logger.warn("No members found for the group " + groupId + ".");
				return null;
			}
			List<String> members = new ArrayList<String>();
			for (SocialUser su:result.getMembers()){
				members.add(su.getId());
			}
			//To sort the list
			List<String> unsorted = members.subList(0, members.size());	//Get all the list
			members = RepositoryUtils.asSortedList(unsorted);
			
			if(limit != null){
				return (List<String>) RepositoryUtils.getSublistPagination(members, limit);
			} else {
				logger.info("No limit specified for this search.");
				return members;
			}
		} else {
			logger.error("Group with id " + groupId + " not exists.");
			return null;
		}
	}

	@Override
	public Group update(String groupId, String name) {
		String normalizedName = RepositoryUtils.normalizeString(name);
		SocialGroup saved_group = null;
		SocialGroup group = retrieveGroup(groupId);
		if(group != null){
			// block of already group name from same user present: ask to Raman
			String creatorId = group.getCreator().getId();
			List<SocialGroup> user_group = groupRepository.findByCreatorId(creatorId);
			if(SocialGroup.toGroupName(user_group).contains(name)){
				logger.error("A group from this user with the name " + normalizedName + " already exists.");
				return null;
			}// end of block
			group.setName(normalizedName);
			group.setLastModifiedTime(System.currentTimeMillis());
			saved_group = groupRepository.save(group);
			if(saved_group != null){
				logger.info("Group " + groupId + " correctly updated.");
				return saved_group.toGroup();
			} else {
				logger.error("Error in updating group " + groupId);
			}
		} else {
			logger.warn("No group found with id " + groupId);
		}
		return null;
	}
	
	//Used in tests
	@Override
	public Group update(String groupId, Long creationTime) {
		SocialGroup saved_group = null;
		SocialGroup group = retrieveGroup(groupId);
		if(group != null){
			group.setCreationTime(creationTime);
			group.setLastModifiedTime(System.currentTimeMillis());
			saved_group = groupRepository.save(group);
			if(saved_group != null){
				logger.info("Group " + groupId + " correctly updated.");
				return saved_group.toGroup();
			} else {
				logger.error("Error in updating group " + groupId);
			}
		} else {
			logger.warn("No group found with id " + groupId);
		}
		return null;
	}

	@Override
	public boolean addMembers(String groupId, Set<String> userIds) {
		SocialGroup saved_group = null;
		SocialGroup group = retrieveGroup(groupId);
		if(group != null){
			Set<SocialUser> users = new HashSet<SocialUser>();
			for (String userId : userIds) {
				users.add(new SocialUser(userId));
			}
			group.getMembers().addAll(users);
			saved_group = groupRepository.save(group);
			if(saved_group != null){
				logger.info("Members added correctly to group " + groupId);
			} else {
				logger.error("Error in adding members to group " + groupId);
			}
		} else {
			logger.warn("No group found with id " + groupId);
		}
		return true;
	}

	@Override
	public boolean removeMembers(String groupId, Set<String> userIds) {
		boolean removed = true;
		SocialGroup saved_group = null;
		SocialGroup group = retrieveGroup(groupId);
		if(group != null){
			Set<SocialUser> users = new HashSet<SocialUser>();
			for (String userId : userIds) {
				users.add(new SocialUser(userId));
			}
			if(users.size() > 0){
				removed = group.getMembers().removeAll(users);
				saved_group = groupRepository.save(group);
				if(saved_group != null){
					logger.info("Members correctly removed from group " + groupId);
				} else {
					logger.error("Error in removing members from group " + groupId);
				}
			} else {
				logger.warn("No members to remove in group " + groupId);
			}	
		} else {
			logger.warn("No group found with id " + groupId);
		}
		return removed;
	}

	@Override
	public boolean delete(String groupId) {
		SocialGroup group = groupRepository.findOne(SocialGroup.convertId(groupId));
		if(group != null){
			groupRepository.delete(SocialGroup.convertId(groupId));
			logger.info("Group " + groupId + " correctly removed.");
		} else {
			logger.warn("No group found with id " + groupId);
		}
		return true;
	}

	private SocialGroup retrieveGroup(String groupId)
			throws IllegalArgumentException {
		SocialGroup group = groupRepository.findOne(SocialGroup
				.convertId(groupId));
		if (group == null) {
			//throw new IllegalArgumentException(groupId + " not exists");
			return null;
		} else {
			return group;
		}
	}
}
