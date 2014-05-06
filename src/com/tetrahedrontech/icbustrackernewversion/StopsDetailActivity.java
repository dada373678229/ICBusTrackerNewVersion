package com.tetrahedrontech.icbustrackernewversion;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.tetrahedrontech.icbustrackernewversion.API.coreAPI;
import com.tetrahedrontech.icbustrackernewversion.cards.routeListDetailCard;
import com.tetrahedrontech.icbustrackernewversion.cards.routeListDetailCardExpand;
import com.tetrahedrontech.icbustrackernewversion.cards.themeListCard;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager.BadTokenException;
import android.widget.TextView;

public class StopsDetailActivity extends Activity{
	private Context context;
	//"cards" contains cards where each card has bus prediction information
	private ArrayList<Card> cards = new ArrayList<Card>();
	private ProgressDialog progressDialog;
	//errorCode -1=no error, 0=no internet connection, 1=internet timeout, 2=no predictions
	private int errorCode=-1;
	final LongOperation getData=new LongOperation();

	private SharedPreferences settings;
	private boolean favorite=false;
	private String stopId;
	private String stopName;
	
	private boolean autoRefresh;
	private boolean alarm;
	
	//this is to help determine whether to show progress dialog
	//if firstTimeRunning is true, show progress dialog
	//if false, means it is in refresh state, don't show progress dialog
	private boolean firstTimeRunning=true;
	
	CardArrayAdapter mCardArrayAdapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stops_detail);

		settings=getSharedPreferences("mySettings",0);
		
		//set action bar background color
		ColorDrawable cd=new ColorDrawable(Color.parseColor(HomeActivity.actionBarColors[HomeActivity.theme]));
		getActionBar().setBackgroundDrawable(cd);
		
		//set up action bar
		String stopTitle=getIntent().getExtras().getString("stopTitle");
		stopId=stopTitle.split(",")[0];
		stopName=stopTitle.split(",")[1];
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(stopTitle.split(",")[1]);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		context=this;
		
		//check if this stop is in favorite
		String stopKey=stopId+","+stopName;
		favorite=settings.getStringSet("favorite", new HashSet<String>()).contains(stopKey);
		
		//check if auto-refresh or alarm is enabled
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		autoRefresh=sharedPref.getBoolean("auto_refresh", true);
		alarm=sharedPref.getBoolean("alarm", false);
		
		
		//show progress dialog
		progressDialog=createProgressDialog(this);
		progressDialog.show();
		
		//now begin to do the heavy job: get bus predictions
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		    getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{stopId});
		else
		    getData.execute(new String[]{stopId});
		 
		//if it takes more than 5 seconds to fetch data from the Internet,
		//stop AsyncTask and show error page
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
		  @Override
		  public void run() {
		      if ( getData.getStatus() == AsyncTask.Status.RUNNING && firstTimeRunning){
		          getData.cancel(true);
		      	  progressDialog.cancel();
		      	  errorCode=1;
				  errorHandler();
		      }
		  }
		}, 5000);
		
	}
	
		//this class can do heavy tasks in the background. Here, we want it to set cardlist
		private class LongOperation extends AsyncTask<String, String, String> {
			
			//this method sets the arraylist of cards, params can be viewed as String[]
			@Override
	        protected String doInBackground(String... params) {
				int stopId=Integer.parseInt(params[0]);
				//if auto refresh enabled
				if (autoRefresh){
					while(true){
						if (isCancelled()) {
							//Log.i("mytag","breaked");
							break;
						}
						cards=setPredictionItem(stopId);
						publishProgress(new String[]{});
						
						try {
							Thread.currentThread().sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					return "";
				}
				//if auto refresh is off
				else{
					cards=setPredictionItem(stopId);
					return "";
				}
				
			}
			
			//since we CANNOT update user interface UNTIL the doInBackground is finished, so we
			//have to update the UI AFTER getting the predictions
			@Override
	        protected void onProgressUpdate(String... result) {
				setUpUI();
	        }
			
			@Override
			protected void onPostExecute(String result){
				setUpUI();
				firstTimeRunning=true;
			}
			
			@Override
			protected void onCancelled(String result){
				if (errorCode!=-1){
					errorHandler();
				}
			}
			
			protected void setUpUI(){
				if (progressDialog.isShowing()){
					//close progress dialog and show error message
					progressDialog.cancel();
				}
				
				//if we don't have any error, set the cardlist
				if (errorCode==-1){
					CardListView listView = (CardListView) findViewById(R.id.stopDetailListView);
					//at initial stage, use animation to show cards
					if (firstTimeRunning){
						firstTimeRunning=false;
						mCardArrayAdapter = new CardArrayAdapter(context,cards);
						AnimationAdapter animCardArrayAdapter = new AlphaInAnimationAdapter(mCardArrayAdapter);
				        animCardArrayAdapter.setAbsListView(listView);
				        if (listView!=null){
				            listView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
				        }
					}
					//at refresh stage, just update data
					else{
						mCardArrayAdapter.clear();
						mCardArrayAdapter.addAll(cards);
						mCardArrayAdapter.notifyDataSetChanged();
					}
				}
			}
		}
		
		//animation when back button is pressed
		@Override
		public void onBackPressed() {
		    super.onBackPressed();
		    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
		}
		
		@Override
	    protected void onDestroy() {
	        super.onDestroy();
	        if (! getData.isCancelled()){
				getData.cancel(true);
			}
	    }
		
		//set up option menu
		@Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
			// Inflate the menu items for use in the action bar
		    MenuInflater inflater = getMenuInflater();
		    //if the stop is in favorite, load solid five star button
		    //if not, load empty five star button
		    //if auto-refresh disabled, show manual refresh button
		    if (favorite && !autoRefresh){
		    	//Log.i("mytag","favorite && !autoRefresh");
		    	inflater.inflate(R.menu.favorite_not_autorefresh, menu);
		    }
		    else if (!favorite & !autoRefresh){
		    	//Log.i("mytag","!favorite && !autoRefresh");
		    	inflater.inflate(R.menu.not_favorite_not_autorefresh, menu);
		    }
		    else if(favorite && autoRefresh){
		    	//Log.i("mytag","favorite && autoRefresh");
		    	inflater.inflate(R.menu.favorite_autorefresh, menu);
		    }
		    else {
		    	//Log.i("mytag","!favorite && autoRefresh");
		    	inflater.inflate(R.menu.not_favorite_autorefresh, menu);
		    }
		    return super.onCreateOptionsMenu(menu);
		}
		
		//when menu item is selected
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			
		    //get item id, if it is favorite button, turn it to not favorite
        	//if it is not favorite button, turn it to favorite
        	//and save changes to settings
        	String stopKey=stopId+","+stopName;
		    switch (item.getItemId()) {
		    	//is currently favorite
		        case R.id.stopDetail_favorite_icon:
		        	Set<String> favoriteStops=settings.getStringSet("favorite", new HashSet<String>());
		        	favoriteStops.remove(stopKey);
		        	SharedPreferences.Editor editor=settings.edit();
		        	editor.clear();
		        	editor.putStringSet("favorite", favoriteStops);
		        	editor.commit();
		        	favorite= !favorite;
		        	break;
		        //is currently not favorite
		        case R.id.stopDetail_not_favorite_icon:
		        	Set<String> favoriteStops2=settings.getStringSet("favorite", new HashSet<String>());
		        	favoriteStops2.add(stopKey);
		        	SharedPreferences.Editor editor2=settings.edit();
		        	editor2.clear();
		        	editor2.putStringSet("favorite",favoriteStops2);
		        	editor2.commit();
		        	favorite= !favorite;
		        	break;
		        //refresh button pressed	
		        case R.id.stopDetail_refresh_icon:{
		        	setContentView(R.layout.activity_stops_detail);
		        	
		        	final LongOperation getData2=new LongOperation();
		        	//now begin to do the heavy job: get bus predictions
		    		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		    		    getData2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{stopId});
		    		else
		    		    getData2.execute(new String[]{stopId});
		    		 
		    		//if it takes more than 5 seconds to fetch data from the Internet,
		    		//stop AsyncTask and show error page
		    		Handler handler = new Handler();
		    		handler.postDelayed(new Runnable()
		    		{
		    		  @Override
		    		  public void run() {
		    		      if ( getData2.getStatus() == AsyncTask.Status.RUNNING && firstTimeRunning){
		    		          getData2.cancel(true);
		    		      	  progressDialog.cancel();
		    		      	  errorCode=1;
		    				  errorHandler();
		    		      }
		    		  }
		    		}, 5000);
		    		errorHandler();
		    		break;
		        	}
		        case android.R.id.home:
		        	onBackPressed();
		        	break;
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		    
		    //refresh option menu
		    invalidateOptionsMenu();
		    return true;
		}
		
		//this method returns an arraylist of cards where each card is a bus prediction
		//this method also handles errors
		private ArrayList<Card> setPredictionItem(int stopId){
			ArrayList<Card> result=new ArrayList<Card>();
			//fetch data only if the device is connected to Internet
			if(isOnline()){
				//assume there is no error
				errorCode=-1;
				
				coreAPI api=new coreAPI();
				String p=api.busPrediction(stopId);
				//if there is no bus prediction
				if (p.length()==0) {
					getData.cancel(true);
					errorCode=2;
					return result;
				}
				//if there is some bus predictions
				String data[]=p.split(";");
				for (int i=0; i<data.length;i++){		
					//create card and card expand
					routeListDetailCard temp=new routeListDetailCard(this);
		            
					//set values on card
					String line[]=data[i].split(",");
					temp.setContent(line[0],line[3],line[1]+"min");
					temp.setBackgroundResourceId(HomeActivity.pressedCardBackground[HomeActivity.theme]);
					
					//show alarm expand only when alarm is enabled
					if(alarm){
						routeListDetailCardExpand expand = new routeListDetailCardExpand(this);
						expand.setInnerLayout(R.layout.stop_detail_expand_layout);
						expand.setContent(line[0], line[1], stopId);
						temp.addCardExpand(expand);
					}
					
					ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand.builder().setupCardElement(ViewToClickToExpand.CardElementUI.CARD);
		            temp.setViewToClickToExpand(viewToClickToExpand);
		            
					result.add(temp);
				}
			}
			//no connected to Internet
			else{
				getData.cancel(true);
				errorCode=0;
			}
			return result;
		}
		
		//this methods checks if the device is connected to the Internet and can receive data
		private boolean isOnline(){
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting() && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
				return true;}
			return false;
		}
		
		//this method shows a progress dialog
		private static ProgressDialog createProgressDialog(Context mContext) {
	        ProgressDialog dialog = new ProgressDialog(mContext);
	        try {
	                dialog.show();
	        } catch (BadTokenException e) {

	        }
	        dialog.setCancelable(false);
	        dialog.setContentView(R.layout.progress_dialog);
	        // dialog.setMessage(Message);
	        return dialog;
		}
		
		//this method is called everytime we fetch api data.
		//if errorCode is not -1 (means no error), we show error message on screen
		private void errorHandler(){
			if ((!getData.isCancelled()) && (errorCode != -1)){
				getData.cancel(true);
			}
			if (progressDialog.isShowing()){
				progressDialog.cancel();
			}
			if (errorCode!=-1){
				setContentView(R.layout.error_layout);
				TextView t = (TextView) this.findViewById(R.id.stop_detail_textView);
				switch (errorCode){
				case 0: t.setText("No Internet Connection");
						break;
				case 1: t.setText("Internet Timeout");
						break;
				case 2: t.setText("No Upcomming Buses");
						break;
				}
			}
		}
}
