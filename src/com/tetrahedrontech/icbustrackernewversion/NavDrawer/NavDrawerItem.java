package com.tetrahedrontech.icbustrackernewversion.NavDrawer;

public class NavDrawerItem {
	
	private String title;
	private int icon;
	
	public NavDrawerItem(String title, int icon){
		this.title=title;
		this.icon=icon;
	}
	
	public String getTitle(){
		return title;
	}
	
	public int getIcon(){
		return icon;
	}
}
