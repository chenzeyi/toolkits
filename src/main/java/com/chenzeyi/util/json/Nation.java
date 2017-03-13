package com.chenzeyi.util.json;

public class Nation {
	private String nationName;
	private String language;
	private String religion;
	
	public Nation(String nationName, String language, String religion) {
		super();
		this.nationName = nationName;
		this.language = language;
		this.religion = religion;
	}
	public String getNationName() {
		return nationName;
	}
	public void setNationName(String nationName) {
		this.nationName = nationName;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getReligion() {
		return religion;
	}
	public void setReligion(String religion) {
		this.religion = religion;
	}
	
}
