package eu.trentorise.smartcampus.social.engine.beans;

import java.util.List;

public class Visibility {

	private List<String> users;
	private List<String> communities;
	private List<String> groups;
	private boolean publicShared;

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
