package eu.trentorise.smartcampus.social.managers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

	@Autowired
	CommunityRepository communityRepository;

	@Override
	public Community create(String name) {
		SocialCommunity community = new SocialCommunity();
		community.setName(name);
		return communityRepository.save(community).toCommunity();
	}

	@Override
	public List<Community> readCommunities(Limit limit) {
		// TODO Auto-generated method stub
		return null;
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
			for (String member : members) {
				users.add(new SocialUser(member));
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
			for (String member : members) {
				users.add(new SocialUser(member));
			}
			result = community.getMembers().removeAll(users);
			communityRepository.save(community);
		}
		return result;
	}

	@Override
	public boolean delete(String communityId) {
		communityRepository.delete(RepositoryUtils.convertId(communityId));
		return true;
	}

}
