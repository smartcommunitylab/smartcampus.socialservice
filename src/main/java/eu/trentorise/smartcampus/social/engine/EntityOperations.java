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

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.Limit;

public interface EntityOperations {

	// visibility

	public Entity saveOrUpdate(String namespace, Entity entity);

	// read

	public List<Entity> readShared(String actorId, boolean isCommunity,
			String appId, Limit limit);

	public Entity readShared(String actorId, boolean isCommunity, String uri);

	public List<Entity> readEntities(String ownerId, String communityId,
			String appId, Limit limit);

	public Entity readEntity(String uri);

}
