package com.tetrahedrontech.bongocity.API;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class xmlPullParserBL {
	
	private String urlString = null;
	
	private XmlPullParserFactory xmlFactoryObject;
	
	public volatile boolean parsingComplete = true;
	
	private List<Information> Infor=new ArrayList<Information>();
	
	private Information infor;
	
	
	public List<Information> getInfor(){
		return Infor;
	}
	
	public xmlPullParserBL(String url)
	{
		this.urlString = url;
		
	}


	public List<Information> parseXMLAndStoreIt(XmlPullParser myParser) 
	{
		int event;
	    String text=null;
	    try 
	    {
	    	event = myParser.getEventType();
	        while (event != XmlPullParser.END_DOCUMENT) 
	        {
	        	String name=myParser.getName();
	            switch (event)
	            {
	            case XmlPullParser.START_TAG:
	            	if(name.equalsIgnoreCase("bus"))
	            	{
						infor=new Information();
					}
	            break;
	            
	            case XmlPullParser.TEXT:
	            text = myParser.getText();
	            break;
	            
	            case XmlPullParser.END_TAG:
	            	
	            	if(name.equals("bus"))
	            	{
	            		Infor.add(infor);
	            	}
	            	if(name.equals("id"))
	            	{
	            		infor.setId(text);
	                }
	                else if(name.equals("lat"))
	                { 	
	                	infor.setLat(text);
	                }
	                else if(name.equals("lng"))
	                {
	                	infor.setLng(text);
	                }
	                else if(name.equals("heading"))
	                {
	                	infor.setHeading(text);
	                }
	                break;
	            }
	            event = myParser.next(); 

	        }
	        parsingComplete = false;
	    } 
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    }
	    return Infor;
    }
	
	
	public void fetchXML()
	{
		Thread thread = new Thread(new Runnable()
		{
			public void run() 
			{
				try 
				{
					URL url = new URL(urlString);
					HttpURLConnection conn = (HttpURLConnection) 
					url.openConnection();
					conn.setReadTimeout(10000 /* milliseconds */);
					conn.setConnectTimeout(15000 /* milliseconds */);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					conn.connect();
	            
					InputStream stream = conn.getInputStream();

					xmlFactoryObject = XmlPullParserFactory.newInstance();
					XmlPullParser myparser = xmlFactoryObject.newPullParser();

					myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
					myparser.setInput(stream, null);
					parseXMLAndStoreIt(myparser);
					stream.close();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
		);
		thread.start(); 
	}

}
