package eu.trentorise.smartcampus.social.engine.beans;

import java.io.Serializable;

public class User implements Serializable, Comparable<Object>{
	
	private static final long serialVersionUID = 1L;
	private String id;
	
	public User(){
	}
	
	public User(String id){
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int compareTo(Object o) {
		User us = (User) o;
		// TODO Auto-generated method stub
		return this.getId().compareTo(us.getId());
	}

}
