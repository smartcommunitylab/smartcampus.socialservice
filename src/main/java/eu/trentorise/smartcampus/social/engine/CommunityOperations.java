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

	public Set<String> readMembers(String communityId, Limit limit);

	// update

	public boolean addMembers(String communityId, Set<String> members);

	public boolean removeMembers(String communityId, Set<String> members);

	// delete

	public boolean delete(String communityId);
}
