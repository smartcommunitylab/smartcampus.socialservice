package eu.trentorise.smartcampus.social.engine.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class SocialEntity implements Serializable {

	private static final long serialVersionUID = 4911918164725643362L;

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String uri;

	@OneToOne
	private SocialUser owner;

	@OneToOne
	private SocialType type;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "userSharedWith")
	private Set<SocialUser> usersSharedWith;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "groupSharedWith")
	private Set<SocialGroup> groupsSharedWith;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "communitySharedWith")
	private Set<SocialCommunity> communitiesSharedWith;

	private boolean publicShared;

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

	public SocialUser getOwner() {
		return owner;
	}

	public void setOwner(SocialUser owner) {
		this.owner = owner;
	}

	public Set<SocialUser> getUsersSharedWith() {
		return usersSharedWith;
	}

	public void setUsersSharedWith(Set<SocialUser> usersSharedWith) {
		this.usersSharedWith = usersSharedWith;
	}

	public Set<SocialGroup> getGroupsSharedWith() {
		return groupsSharedWith;
	}

	public void setGroupsSharedWith(Set<SocialGroup> groupsSharedWith) {
		this.groupsSharedWith = groupsSharedWith;
	}

	public SocialType getType() {
		return type;
	}

	public void setType(SocialType type) {
		this.type = type;
	}

	public Set<SocialCommunity> getCommunitiesSharedWith() {
		return communitiesSharedWith;
	}

	public void setCommunitiesSharedWith(
			Set<SocialCommunity> communitiesSharedWith) {
		this.communitiesSharedWith = communitiesSharedWith;
	}

	public boolean isPublicShared() {
		return publicShared;
	}

	public void setPublicShared(boolean publicShared) {
		this.publicShared = publicShared;
	}

}
