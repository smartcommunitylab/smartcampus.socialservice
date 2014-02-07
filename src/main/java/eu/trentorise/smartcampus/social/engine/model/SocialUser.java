package eu.trentorise.smartcampus.social.engine.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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

}
