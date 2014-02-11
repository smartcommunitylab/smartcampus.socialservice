package eu.trentorise.smartcampus.social.engine.beans;

import java.util.Set;

public class Group {

	private String id;

	private String name;

	private Long creationTime;
	private Long lastModifiedTime;

	private Set<String> members;
	private int totalMembers;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Set<String> getMembers() {
		return members;
	}

	public void setMembers(Set<String> members) {
		this.members = members;
	}

	public int getTotalMembers() {
		return totalMembers;
	}

	public void setTotalMembers(int memberNumber) {
		this.totalMembers = memberNumber;
	}

}
