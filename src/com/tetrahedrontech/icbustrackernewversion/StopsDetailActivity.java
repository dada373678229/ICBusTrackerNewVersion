package com.tetrahedrontech.icbustrackernewversion;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;

import com.tetrahedrontech.icbustrackernewversion.API.coreAPI;
import com.tetrahedrontech.icbustrackernewversion.cards.routeListDetailCard;
import com.tetrahedrontech.icbustrackernewversion.cards.routeListDetailCardExpand;
import com.tetrahedrontech.icbustrackernewversion.cards.themeListCard;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
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
	private int[] pressedCardBackground=new int[]{R.drawable.card_selector_light_blue,R.drawable.card_selector_light_purple,R.drawable.card_selector_light_green};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stops_detail);
		
		//set up action bar
		String stopTitle=getIntent().getExtras().getString("stopTitle");
		String stopId=stopTitle.split(",")[0];
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(stopTitle.split(",")[1]);
		context=this;
		
		SharedPreferences settings=getSharedPreferences(themeListCard.PREFS_NAME,0);
		theme=Integer.valueOf(settings.getString("theme", "0"));
		
		//show progress dialog
		progressDialog=createProgressDialog(this);
		progressDialog.show();
		
		//now begin to do the heavy job: get bus predictions
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		    getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{stopId});
		else
		    getData.execute(new String[]{stopId});
		 
		//if it takes more than 3 seconds to fetch data from the Internet,
		//stop AsyncTask and show error page
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
		  @Override
		  public void run() {
		      if ( getData.getStatus() == AsyncTask.Status.RUNNING ){
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
					CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(context,cards);
					CardListView listView = (CardListView) findViewById(R.id.stopDetailListView);
			        if (listView!=null){
			            listView.setAdapter(mCardArrayAdapter);
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
					Card temp=new routeListDetailCard(this);
		            
					//set values on card
					String line[]=data[i].split(",");
					((routeListDetailCard) temp).setContent(line[0],line[3],line[1]+"min");
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
