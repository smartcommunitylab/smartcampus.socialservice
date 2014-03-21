/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.trentorise.smartcampus.social.engine;

import java.util.List;
import java.util.Set;

import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.beans.User;;

public interface GroupOperations {

	// creation
	
	public Group create(String userId, String name);

	// read

	public List<Group> readGroups(String userId, Limit limit);

	public Group readGroup(String groupId);

	public List<User> readMembers(String groupId, Limit limit);
	
	List<String> readMembersAsString(String groupId, Limit limit);

	// update

	public Group update(String groupId, String name);

	public Group update(String groupId, Long creationTime);
	
	public boolean addMembers(String groupId, Set<String> userIds);

	public boolean removeMembers(String groupId, Set<String> userIds);

	// delete

	public boolean delete(String groupId);

}
