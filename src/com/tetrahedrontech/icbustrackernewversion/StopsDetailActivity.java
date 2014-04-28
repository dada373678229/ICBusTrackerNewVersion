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
	
	private int theme;
	private String[] actionBarColors=new String[]{"#99CCFF","#FFBFFF","#99FFCC"};
	private int[] pressedCardBackground=new int[]{R.drawable.card_selector_light_blue,R.drawable.card_selector_light_purple,R.drawable.card_selector_light_green};
	
	private boolean favorite;
	private Set<String> favoriteStops;
	private String stopId;
	
	private boolean firstTimeRunning=true;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stops_detail);
		
		//set action bar background color
		SharedPreferences settings=getSharedPreferences(themeListCard.PREFS_NAME,0);
		theme=Integer.valueOf(settings.getString("theme", "0"));
		ColorDrawable cd=new ColorDrawable(Color.parseColor(actionBarColors[theme]));
		getActionBar().setBackgroundDrawable(cd);
		
		//set up action bar
		String stopTitle=getIntent().getExtras().getString("stopTitle");
		stopId=stopTitle.split(",")[0];
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(stopTitle.split(",")[1]);
		context=this;
		
		//check if this stop is in favorite
		favoriteStops=settings.getStringSet("favorite", new HashSet<String>());
		favorite=favoriteStops.contains(stopId);
		
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
				  errorHandler(1);
		      }
		  }
		}, 5000);
	}
	
		//this class can do heavy tasks in the background. Here, we want it to set cardlist
		private class LongOperation extends AsyncTask<String, Void, String> {
			
			//this method sets the arraylist of cards, params can be viewed as String[]
			@Override
	        protected String doInBackground(String... params) {
				int stopId=Integer.parseInt(params[0]);
				cards=setPredictionItem(stopId);
				return "";
			}
			
			//since we CANNOT update user interface UNTIL the doInBackground is finished, so we
			//have to update the UI AFTER getting the predictions
			@Override
	        protected void onPostExecute(String result) {
				//if we don't have any error, set the cardlist
				if (errorCode==-1){
					firstTimeRunning=false;
					CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(context,cards);
					CardListView listView = (CardListView) findViewById(R.id.stopDetailListView);
					AnimationAdapter animCardArrayAdapter = new AlphaInAnimationAdapter(mCardArrayAdapter);
			        animCardArrayAdapter.setAbsListView(listView);
			        if (listView!=null){
			            listView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
			        }
				}
				
				//close progress dialog and show error message
				progressDialog.cancel();
				errorHandler(errorCode);			
	        }
		}
		
		//animation when back button is pressed
		@Override
		public void onBackPressed() {
		    super.onBackPressed();
		    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
		}
		
		//set up option menu
		@Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
			// Inflate the menu items for use in the action bar
		    MenuInflater inflater = getMenuInflater();
		    //if the stop is in favorite, load solid five star button
		    //if not, load empty five star button
		    if (favorite){
		    	inflater.inflate(R.menu.favorite, menu);
		    }
		    else{
		    	inflater.inflate(R.menu.not_favorite, menu);
		    }
		    return super.onCreateOptionsMenu(menu);
		}
		
		//when menu item is selected
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			//open settings
			SharedPreferences settings=getSharedPreferences(themeListCard.PREFS_NAME,0);
        	SharedPreferences.Editor editor=settings.edit();
			
		    //get item id, if it is favorite button, turn it to not favorite
        	//if it is not favorite button, turn it to favorite
        	//and save changes to settings
		    switch (item.getItemId()) {
		    	//is currently favorite
		        case R.id.stopDetail_favorite_icon:
		        	favoriteStops.remove(stopId);
		        	editor.putStringSet("favorite", favoriteStops);
		        	break;
		        //is currently not favorite
		        case R.id.stopDetail_not_favorite_icon:
		        	favoriteStops.add(stopId);
		        	editor.putStringSet("favorite",favoriteStops);
		        	break;
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		    editor.commit();
		    favorite= !favorite;
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
					temp.setBackgroundResourceId(pressedCardBackground[theme]);
					
					routeListDetailCardExpand expand = new routeListDetailCardExpand(this);
					expand.setInnerLayout(R.layout.stop_detail_expand_layout);
					expand.setContent(line[0], line[1], stopId);
					temp.addCardExpand(expand);
					ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand.builder().setupCardElement(ViewToClickToExpand.CardElementUI.CARD);
		            temp.setViewToClickToExpand(viewToClickToExpand);
		            
					result.add(temp);
				}
			}
			//no connected to Internet
			else{
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
		private void errorHandler(int errorCode){
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
