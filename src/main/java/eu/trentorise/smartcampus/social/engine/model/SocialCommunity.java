package eu.trentorise.smartcampus.social.engine.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import eu.trentorise.smartcampus.social.engine.beans.Community;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@Entity
public class SocialCommunity implements Serializable {

	private static final long serialVersionUID = 5158453115231390566L;

	@Id
	@GeneratedValue
	private Long id;

	private String name;
	private Long creationTime;
	private Long lastModifiedTime;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	private Set<SocialUser> members;

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
		// community.setMemberNumber(getMembers().size());
		return community;
	}
}
