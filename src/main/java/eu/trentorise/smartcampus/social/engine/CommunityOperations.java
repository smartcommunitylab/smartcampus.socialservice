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

import eu.trentorise.smartcampus.social.engine.beans.Community;
import eu.trentorise.smartcampus.social.engine.beans.Limit;

public interface CommunityOperations {

	// creation
	public Community create(String name, String appId)
			throws IllegalArgumentException;

	// read

	public List<Community> readCommunities(Limit limit);

	public List<Community> readCommunitiesByAppId(String appId, Limit limit) throws IllegalArgumentException;

	public Community readCommunity(String communityId);

	public Set<String> readMembers(String communityId, Limit limit);

	// update

	public boolean addMembers(String communityId, Set<String> members);

	public boolean removeMembers(String communityId, Set<String> members);

	// delete

	public boolean delete(String communityId);
}
