package eu.trentorise.smartcampus.social.engine.beans;

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

}
