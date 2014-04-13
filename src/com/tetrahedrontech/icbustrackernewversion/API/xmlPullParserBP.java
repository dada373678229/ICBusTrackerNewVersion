package com.tetrahedrontech.icbustrackernewversion.API;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class xmlPullParserBP 
{
    private String urlString = null;
	
	private XmlPullParserFactory xmlFactoryObject;
	
	public volatile boolean parsingComplete = true;
	
	private List<Information> Infor=new ArrayList<Information>();
	
	private Information infor;
	
	
	public List<Information> getInfor(){
		return Infor;
	}
	
	public xmlPullParserBP(String url)
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
	            	if(name.equalsIgnoreCase("prediction"))
	            	{
						infor=new Information();
					}
	            break;
	            
	            case XmlPullParser.TEXT:
	            text = myParser.getText();
	            break;
	            
	            case XmlPullParser.END_TAG:
	            	
	            	if(name.equals("prediction"))
	            	{
	            		Infor.add(infor);
	            	}
	            	if(name.equals("title"))
	            	{
	            		infor.setTitle(text);
	                }
	                else if(name.equals("tag"))
	                { 	
	                	infor.setTag(text);
	                }
	                else if(name.equals("minutes"))
	                {
	                	infor.setMinutes(text);
	                }
	                else if(name.equals("agency"))
	                {
	                	infor.setAgency(text);
	                }
	                else if(name.equals("direction"))
	                {
	                	infor.setDirection(text);
	                }
	                else if(name.equals("stopname"))
	                {
	                	infor.setStopname(text);
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
