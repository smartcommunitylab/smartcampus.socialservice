package eu.trentorise.smartcampus.social.engine.utils;

import java.util.ArrayList;
import java.util.List;

public class CustomStringList extends ArrayList<String>{
	
	private static final long serialVersionUID = 1L;

	public CustomStringList(){
	}
	
	public CustomStringList(List<String> asList) {
		for(String s:asList){
			this.add(s);
		}
	}

	@Override
	public boolean contains(Object o){
		String param = (String)o;
		for(String s:this){
			if(param.equalsIgnoreCase(s)) return true;
		}
		return false;
	}

}
