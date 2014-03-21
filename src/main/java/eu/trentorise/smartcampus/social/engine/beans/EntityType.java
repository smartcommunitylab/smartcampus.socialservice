package eu.trentorise.smartcampus.social.engine.beans;

import java.io.Serializable;

public class EntityType implements Serializable {

	private static final long serialVersionUID = -4985212556248448954L;

	private String id;
	private String name;
	private String mimeType;

	public EntityType() {

	}

	public EntityType(String name, String mimeType) {
		this.name = name;
		this.mimeType = mimeType;
	}

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

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}
