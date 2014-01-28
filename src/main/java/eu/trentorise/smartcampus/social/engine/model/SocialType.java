package eu.trentorise.smartcampus.social.engine.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SocialType implements Serializable {

	private static final long serialVersionUID = -5778857034813496173L;

	@Id
	private Long id;

	// unique
	private String name;

	private String mimeType;

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

}
