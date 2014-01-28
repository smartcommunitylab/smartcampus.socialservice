package eu.trentorise.smartcampus.social.engine;

import java.util.List;
import java.util.Set;

import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.beans.Member;

public interface GroupOperations {

	// creation
	public Group create(String userId, String name);

	// read

	public List<Group> readGroups(String userId, Limit limit);

	public Group readGroup(String groupId);

	public List<Member> readMembers(String groupId, Limit limit);

	// update

	public Group update(String groupId, String name);

	public boolean addMembers(String groupId, Set<String> userIds);

	public boolean removeMembers(String groupId, Set<String> userIds);

	// delete

	public boolean delete(String groupId);
}
