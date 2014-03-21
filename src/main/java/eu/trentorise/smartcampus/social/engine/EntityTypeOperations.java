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

import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Limit;

public interface EntityTypeOperations {

	// create
	public EntityType create(String name, String mimeType);

	// reads
	public EntityType readType(String entityId);

	public EntityType readTypeByNameAndMimeType(String name, String mimeType);
	
	public List<EntityType> readTypes(Limit limit);
	
	public List<EntityType> readTypesByName(String name, Limit limit);
	
	public List<EntityType> readTypesByMimeType(String mimeType, Limit limit);
	
	// update
	public EntityType updateType(String entityTypeId, String mimeType);
	
	// delete
	public boolean deleteType(String GroupId);
	
}
