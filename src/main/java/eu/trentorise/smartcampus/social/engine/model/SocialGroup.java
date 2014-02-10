package eu.trentorise.smartcampus.social.engine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import eu.trentorise.smartcampus.social.engine.beans.Group;

@Entity
public class SocialGroup implements Serializable {

	private static final long serialVersionUID = 4215375081912075994L;

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	private SocialUser creator;

	private Long creationTime;
	private Long lastModifiedTime;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "group_members")
	private Set<SocialUser> members;

	public SocialGroup() {
		creationTime = System.currentTimeMillis();
		lastModifiedTime = System.currentTimeMillis();
	}

	public SocialGroup(String name, SocialUser creator) {
		this.name = name;
		this.creator = creator;
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

	public Set<SocialUser> getMembers() {
		return members;
	}

	public void setMembers(Set<SocialUser> members) {
		this.members = members;
	}

	public SocialUser getCreator() {
		return creator;
	}

	public void setCreator(SocialUser creator) {
		this.creator = creator;
	}

	public Group toGroup() {
		Group group = new Group();
		group.setId(id != null ? id.toString() : null);
		group.setName(name);
		group.setCreationTime(creationTime);
		group.setLastModifiedTime(lastModifiedTime);
		if(getMembers() != null){
			Set<String> memberListId = new HashSet<String>();
			for (SocialUser su:getMembers()){
				memberListId.add(su.getId());
			}
			group.setMembers(memberListId);
		}
		group.setMemberNumber(getMembers() != null ? getMembers().size() : 0);
		return group;
	}

	public static List<Group> toGroup(Iterable<SocialGroup> groups) {
		List<Group> outputGroups = new ArrayList<Group>();
		for (SocialGroup group : groups) {
			outputGroups.add(group.toGroup());
		}
		return outputGroups;
	}

	public static Long convertId(String id) throws IllegalArgumentException {
		try {
			return Long.valueOf(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(id + " not valid");
		}
	}
}
