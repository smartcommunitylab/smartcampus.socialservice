package eu.trentorise.smartcampus.social.engine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@Entity
public class SocialType implements Serializable {

	private static final long serialVersionUID = -5778857034813496173L;

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String mimeType;
	
	public SocialType(){
	}
	
	public SocialType(String name, String mimeType){
		this.name = name;
		this.mimeType = mimeType;
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

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public EntityType toEntityType(){
		EntityType eType = new EntityType();
		eType.setId(RepositoryUtils.convertId(id));
		eType.setName(name);
		eType.setMimeType(mimeType);
		return eType;
	}
	
	public static List<Group> toGroup(Iterable<SocialGroup> groups) {
		List<Group> outputGroups = new ArrayList<Group>();
		for (SocialGroup group : groups) {
			outputGroups.add(group.toGroup());
		}
		return outputGroups;
	}
	
	public static List<EntityType> toEntityType(Iterable<SocialType> types){
		List<EntityType> outputTypes = new ArrayList<EntityType>();
		for(SocialType socialType : types){
			outputTypes.add(socialType.toEntityType());
		}
		return outputTypes;
	}

}
