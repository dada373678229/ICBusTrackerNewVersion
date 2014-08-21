package com.tetrahedrontech.bongocity;

//this is an stop object, has stop id and stop name
public class stopObject {
	private String stopId;
	private String stopName;
	
	public stopObject(String stopId, String stopName){
		this.stopId=stopId;
		this.stopName=stopName;
	}
	
	public String getStopId(){
		return stopId;
	}
	
	public String getStopName(){
		return stopName;
	}

}
