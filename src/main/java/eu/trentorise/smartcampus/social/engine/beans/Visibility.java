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

package eu.trentorise.smartcampus.social.engine.beans;

import java.util.List;

public class Visibility {

	private List<String> users;
	private List<String> communities;
	private List<String> groups;
	private boolean publicShared;

	public Visibility() {

	}

	public Visibility(boolean publicShared) {
		this.publicShared = publicShared;
	}

	public Visibility(List<String> users, List<String> communities,
			List<String> groups) {
		this.users = users;
		this.communities = communities;
		this.groups = groups;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public List<String> getCommunities() {
		return communities;
	}

	public void setCommunities(List<String> communities) {
		this.communities = communities;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public boolean isPublicShared() {
		return publicShared;
	}

	public void setPublicShared(boolean publicShared) {
		this.publicShared = publicShared;
	}

}
