package com.tetrahedrontech.bongocity.API;

import java.util.List;

public class coreAPI 
{	
	//ebongo official key
	private String key="xApBvduHbU8SRYvc74hJa7jO70Xx4XNO";
	private String output="";
	//bus location tostring
	private String toStringBL(List<Information> Infor)
	{
		String result="";
		int listSize=Infor.size();
		for (int i=0;i<listSize;i++){
			Information temp=Infor.get(i);
			if(i<listSize-1){
				result+=temp.getId()+";"+temp.getLat()+";"+temp.getLng()+";"+temp.getHeading()+"?";
			}
			else{
				result+=temp.getId()+";"+temp.getLat()+";"+temp.getLng()+";"+temp.getHeading();
			}
		}
		return result;
	}
	//bus prediction tostring
	private String toStringBP(List<Information> Infor){
		String result="";
		int listSize=Infor.size();
		for (int i=0;i<listSize;i++){
			Information temp=Infor.get(i);
			if(i<listSize-1){
				result+=temp.getTitle()+";"+temp.getMinutes()+";"+temp.getAgency()+";"+temp.getDirection()+";"+temp.getStopname()+"?";
			}
			else{
				result+=temp.getTitle()+";"+temp.getMinutes()+";"+temp.getAgency()+";"+temp.getDirection()+";"+temp.getStopname();
			}
		}
		return result;
	}
	
	//these are the only two public method you can call to get API information
	public String busPrediction(int stopNumber)
	{	
		String finalUrl ="http://api.ebongo.org/prediction?stopid="+stopNumber+"&api_key="+key;
	    xmlPullParserBP obj = new xmlPullParserBP(finalUrl);
	    obj.fetchXML();
	    while(obj.parsingComplete);
		output=toStringBP(obj.getInfor());
		return output;

	}
	
	public String busLocations(String agency, String route)
	{
		String finalUrl ="http://api.ebongo.org/buslocation?agency="+agency+"&route="+route+"&api_key="+key;
		xmlPullParserBL obj = new xmlPullParserBL(finalUrl);
	    obj.fetchXML();
	    while(obj.parsingComplete);
		output=toStringBL(obj.getInfor());
		return output;
	}	
}
