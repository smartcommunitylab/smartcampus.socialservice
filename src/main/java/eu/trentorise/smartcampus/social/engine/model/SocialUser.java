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

package eu.trentorise.smartcampus.social.engine.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import eu.trentorise.smartcampus.social.engine.beans.User;

@Entity
public class SocialUser implements Serializable {

	private static final long serialVersionUID = 1234242912160627306L;

	@Id
	private String id;

	@OneToMany(fetch = FetchType.LAZY)
	private Set<SocialGroup> myGroups;

	public SocialUser(String id) {
		this.id = id;
	}

	public SocialUser() {
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SocialUser)
				&& ((SocialUser) obj).getId() != null
				&& ((SocialUser) obj).getId().equals(id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<SocialGroup> getMyGroups() {
		return myGroups;
	}

	public void setMyGroups(Set<SocialGroup> myGroups) {
		this.myGroups = myGroups;
	}

	public String toUserString() {
		return id;
	}
	
	public User toUser() {
		User user = new User();
		user.setId(id);
		return user;
	}

	public static Set<String> toUser(Iterable<SocialUser> users) {
		Set<String> ids = new HashSet<String>();
		for (SocialUser user : users) {
			ids.add(user.toUserString());
		}
		return ids;
	}

}
