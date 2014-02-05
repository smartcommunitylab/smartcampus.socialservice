package eu.trentorise.smartcampus.social.managers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.trentorise.smartcampus.social.engine.GroupOperations;
import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.beans.Member;
import eu.trentorise.smartcampus.social.engine.model.SocialGroup;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;
import eu.trentorise.smartcampus.social.engine.repo.GroupRepository;
import eu.trentorise.smartcampus.social.engine.repo.UserRepository;

@Component
public class SocialGroupManager implements GroupOperations {

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	UserRepository userRepository;

	@Override
	public Group create(String userId, String name) {

		// load user
		SocialUser user = userRepository.findOne(userId);
		if (user == null) {
			throw new IllegalArgumentException(userId + "not exists");
		}
		SocialGroup group = new SocialGroup(name, user);
		return groupRepository.save(group).toGroup();
	}

	@Override
	public List<Group> readGroups(String userId, Limit limit) {
		// load user
		SocialUser user = userRepository.findOne(userId);
		if (user == null) {
			throw new IllegalArgumentException(userId + "not exists");
		}
		return SocialGroup.toGroup((groupRepository.findByCreator(userId)));
	}

	@Override
	public Group readGroup(String groupId) {
		SocialGroup result = groupRepository.findOne(SocialGroup
				.convertId(groupId));
		return result != null ? result.toGroup() : null;
	}

	@Override
	public List<Member> readMembers(String groupId, Limit limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group update(String groupId, String name) {
		SocialGroup group = retrieveGroup(groupId);
		group.setName(name);
		return groupRepository.save(group).toGroup();
	}

	@Override
	public boolean addMembers(String groupId, Set<String> userIds) {
		SocialGroup group = retrieveGroup(groupId);
		Set<SocialUser> users = new HashSet<SocialUser>();
		for (String userId : userIds) {
			users.add(new SocialUser(userId));
		}
		group.getMembers().addAll(users);
		groupRepository.save(group);
		return true;
	}

	@Override
	public boolean removeMembers(String groupId, Set<String> userIds) {
		SocialGroup group = retrieveGroup(groupId);
		Set<SocialUser> users = new HashSet<SocialUser>();
		for (String userId : userIds) {
			users.add(new SocialUser(userId));
		}
		group.getMembers().removeAll(users);
		groupRepository.save(group);
		return true;
	}

	@Override
	public boolean delete(String groupId) {
		groupRepository.delete(SocialGroup.convertId(groupId));
		return true;
	}

	private SocialGroup retrieveGroup(String groupId)
			throws IllegalArgumentException {
		SocialGroup group = groupRepository.findOne(SocialGroup
				.convertId(groupId));
		if (group == null) {
			throw new IllegalArgumentException(groupId + " not exists");
		} else {
			return group;
		}
	}
}
