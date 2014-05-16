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

import eu.trentorise.smartcampus.social.managers.EntityManager;

public class Entity {
	private String name;
	private String owner;
	private String communityOwner;
	private String uri;
	private String localId;
	private String externalUri;
	private String type;
	private double rating;
	private int totalVoters;
	private Visibility visibility;
	private long creationTime;
	private long lastModifiedTime;

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCommunityOwner() {
		return communityOwner;
	}

	public void setCommunityOwner(String communityOwner) {
		this.communityOwner = communityOwner;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLocalId() {
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExternalUri() {
		return externalUri;
	}

	public void setExternalUri(String externalUri) {
		this.externalUri = externalUri;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public int getTotalVoters() {
		return totalVoters;
	}

	public void setTotalVoters(int totalVoters) {
		this.totalVoters = totalVoters;
	}

	public EntityInfo toEntityInfo() {
		EntityInfo info = new EntityInfo();
		info.setUri(uri);
		info.setLocalId(localId);
		try {
			info.setAppId(uri != null ? uri.substring(0,
					uri.indexOf(EntityManager.URI_CONCAT_CHAR)) : null);
		} catch (IndexOutOfBoundsException e) {
			info.setAppId(null);
		}
		info.setUserOwner(owner);
		info.setCommunityOwner(communityOwner);
		return info;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
}
