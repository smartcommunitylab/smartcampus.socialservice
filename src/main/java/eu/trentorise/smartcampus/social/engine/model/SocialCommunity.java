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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import eu.trentorise.smartcampus.social.engine.beans.Community;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@Entity
public class SocialCommunity implements Serializable {

	private static final long serialVersionUID = 5158453115231390566L;

	@Id
	@GeneratedValue
	private Long id;

	private String name;
	private String appId;
	private Long creationTime;
	private Long lastModifiedTime;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	private Set<SocialUser> members;

	public SocialCommunity(String name, String appId) {
		this.name = name;
		this.appId = appId;
		creationTime = System.currentTimeMillis();
		lastModifiedTime = System.currentTimeMillis();
	}

	public SocialCommunity() {
		creationTime = System.currentTimeMillis();
		lastModifiedTime = System.currentTimeMillis();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<SocialUser> getMembers() {
		return members;
	}

	public void setMembers(Set<SocialUser> members) {
		this.members = members;
	}

	public Long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
	}

	public Long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public Community toCommunity() {
		Community community = new Community();
		community.setId(RepositoryUtils.convertId(id));
		community.setCreationTime(creationTime);
		community.setLastModifiedTime(lastModifiedTime);
		community.setName(name);
		community.setTotalMembers(members != null ? members.size() : 0);
		community.setMemberIds(members != null ? SocialUser.toUser(members)
				: null);
		return community;
	}

	public static List<Community> toCommunity(
			Iterable<SocialCommunity> collection) {
		List<Community> outputList = null;
		if (collection != null) {
			outputList = new ArrayList<Community>();
			for (SocialCommunity element : collection) {
				outputList.add(element != null ? element.toCommunity() : null);
			}
		}
		return outputList;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
}
