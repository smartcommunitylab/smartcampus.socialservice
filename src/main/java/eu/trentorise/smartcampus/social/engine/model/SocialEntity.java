package eu.trentorise.smartcampus.social.engine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import eu.trentorise.smartcampus.social.engine.beans.Visibility;
import eu.trentorise.smartcampus.social.managers.EntityManager;

@Entity
public class SocialEntity implements Serializable {

	private static final long serialVersionUID = 4911918164725643362L;

	private String name;

	@Id
	private String uri;

	private String namespace;
	private String localId;
	private String externalUri;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private SocialUser owner;

	@OneToOne
	private SocialCommunity communityOwner;

	@OneToOne
	private SocialType type;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	@JoinTable(name = "userSharedWith")
	private Set<SocialUser> usersSharedWith;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "groupSharedWith")
	private Set<SocialGroup> groupsSharedWith;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "communitySharedWith")
	private Set<SocialCommunity> communitiesSharedWith;

	private boolean publicShared;

	private int totalVoters;
	private double rating;

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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getLocalId() {
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public String getExternalUri() {
		return externalUri;
	}

	public void setExternalUri(String externalUri) {
		this.externalUri = externalUri;
	}

	public SocialCommunity getCommunityOwner() {
		return communityOwner;
	}

	public void setCommunityOwner(SocialCommunity communityOwner) {
		this.communityOwner = communityOwner;
	}

	public eu.trentorise.smartcampus.social.engine.beans.Entity toEntity(
			boolean showVisibility) {
		eu.trentorise.smartcampus.social.engine.beans.Entity entity = new eu.trentorise.smartcampus.social.engine.beans.Entity();
		entity.setOwner(owner != null ? owner.getId() : null);
		entity.setCommunityOwner(communityOwner != null ? communityOwner
				.getId().toString() : null);
		entity.setLocalId(localId);
		entity.setType(type.getId().toString());
		entity.setUri(uri);
		entity.setName(name);
		entity.setExternalUri(externalUri);
		entity.setRating(rating);
		entity.setTotalVoters(totalVoters);
		if (showVisibility) {
			entity.setVisibility(EntityManager.getVisibility(this));
		}
		return entity;
	}

	public static List<eu.trentorise.smartcampus.social.engine.beans.Entity> toEntity(
			Iterable<SocialEntity> collection) {
		List<eu.trentorise.smartcampus.social.engine.beans.Entity> outputList = null;
		if (collection != null) {
			outputList = new ArrayList<eu.trentorise.smartcampus.social.engine.beans.Entity>();
			for (SocialEntity element : collection) {
				outputList
						.add(element != null ? element.toEntity(false) : null);
			}
		}
		return outputList;
	}

	public void setVisibility(Visibility visibility) {
		if (visibility != null) {
			publicShared = visibility.isPublicShared();

		}
	}

	public int getTotalVoters() {
		return totalVoters;
	}

	public void setTotalVoters(int totalVoter) {
		this.totalVoters = totalVoter;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

}
