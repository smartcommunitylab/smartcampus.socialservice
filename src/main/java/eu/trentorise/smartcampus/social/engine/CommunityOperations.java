package eu.trentorise.smartcampus.social.engine;

import java.util.List;
import java.util.Set;

import eu.trentorise.smartcampus.social.engine.beans.Community;
import eu.trentorise.smartcampus.social.engine.beans.Limit;

public interface CommunityOperations {

	// creation
	public Community create(String name);

	// read

	public List<Community> readCommunities(Limit limit);

	public Community readCommunity(String communityId);

	// update

	public boolean addMembers(String communityId, Set<String> groupIds);

	public boolean removeMembers(String communityId, Set<String> groupIds);

	// delete

	public boolean delete(String groupId);
}
