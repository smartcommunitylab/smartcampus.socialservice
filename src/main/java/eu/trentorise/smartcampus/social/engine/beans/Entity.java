package eu.trentorise.smartcampus.social.engine.beans;

import java.beans.Visibility;

public class Entity {
	private String owner;
	private String communityOwner;
	private String uri;
	private String localId;
	private String type;
	private Visibility visibility;

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

}
