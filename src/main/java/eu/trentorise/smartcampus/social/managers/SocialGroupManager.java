package eu.trentorise.smartcampus.social.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	@Override
	public Group create(String userId, String name) {

		// load user
		SocialUser user = userManager.readSocialUser(userId);
		if (user == null) {
			user = new SocialUser(userId);
		}
		SocialGroup group = new SocialGroup(name, user);
		
		return groupRepository.save(group).toGroup();
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
		//if (user == null) {
		//	throw new IllegalArgumentException(userId + " not exists");
		//}
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
				return SocialGroup.toGroup(groupRepository.findByCreatorId(userId));
			}
		} else {
			return null;
		}
	}

	@Override
	public Group readGroup(String groupId) {
		SocialGroup result = groupRepository.findOne(SocialGroup
				.convertId(groupId));
		return result != null ? result.toGroup() : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	//public List<SocialUser> readMembers(String groupId, Limit limit) {
	public List<User> readMembers(String groupId, Limit limit) {
		//For the limit I consider only the page and the page size because the from/to date is not applicable
		SocialGroup result = groupRepository.findOne(SocialGroup
				.convertId(groupId));
		if(result != null){
			List<User> members = new ArrayList<User>();
			for (SocialUser su:result.getMembers()){
				members.add(su.toUser());
			}
			//To sort the list
			Collection unsorted = members.subList(0, members.size());	//Get all the list
			members = RepositoryUtils.asSortedList(unsorted);
			
			if(limit != null){
				return (List<User>) RepositoryUtils.getSublistPagination(members, limit);
			} else {
				return members;
			}
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	//public List<SocialUser> readMembers(String groupId, Limit limit) {
	public List<String> readMembersAsString(String groupId, Limit limit) {
		//For the limit I consider only the page and the page size because the from/to date is not applicable
		SocialGroup result = groupRepository.findOne(SocialGroup
				.convertId(groupId));
		if(result != null){
			List<String> members = new ArrayList<String>();
			for (SocialUser su:result.getMembers()){
				members.add(su.getId());
			}
			//To sort the list
			Collection unsorted = members.subList(0, members.size());	//Get all the list
			members = RepositoryUtils.asSortedList(unsorted);
			
			if(limit != null){
				return (List<String>) RepositoryUtils.getSublistPagination(members, limit);
			} else {
				return members;
			}
		} else {
			return null;
		}
	}

	@Override
	public Group update(String groupId, String name) {
		SocialGroup group = retrieveGroup(groupId);
		if(group != null){
			group.setName(name);
			group.setLastModifiedTime(System.currentTimeMillis());
			return groupRepository.save(group).toGroup();
		}
		return null;
	}
	
	//Used in tests
	@Override
	public Group update(String groupId, Long creationTime) {
		SocialGroup group = retrieveGroup(groupId);
		if(group != null){
			group.setCreationTime(creationTime);
			group.setLastModifiedTime(System.currentTimeMillis());
		}
		return groupRepository.save(group).toGroup();
	}

	@Override
	public boolean addMembers(String groupId, Set<String> userIds) {
		SocialGroup group = retrieveGroup(groupId);
		if(group != null){
			Set<SocialUser> users = new HashSet<SocialUser>();
			for (String userId : userIds) {
				users.add(new SocialUser(userId));
			}
			group.getMembers().addAll(users);
			groupRepository.save(group);
		}
		return true;
	}

	@Override
	public boolean removeMembers(String groupId, Set<String> userIds) {
		boolean removed = true;
		SocialGroup group = retrieveGroup(groupId);
		if(group != null){
			Set<SocialUser> users = new HashSet<SocialUser>();
			for (String userId : userIds) {
				users.add(new SocialUser(userId));
			}
			if(users.size() > 0){
				removed = group.getMembers().removeAll(users);
				groupRepository.save(group);
			}	
		}
		return removed;
	}

	@Override
	public boolean delete(String groupId) {
		SocialGroup group = groupRepository.findOne(SocialGroup.convertId(groupId));
		if(group != null){
			groupRepository.delete(SocialGroup.convertId(groupId));
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
