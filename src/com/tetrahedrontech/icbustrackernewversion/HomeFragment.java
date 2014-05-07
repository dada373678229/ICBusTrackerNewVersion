package com.tetrahedrontech.icbustrackernewversion;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnUndoSwipeListListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.tetrahedrontech.icbustrackernewversion.cards.stopListCard;
import com.tetrahedrontech.icbustrackernewversion.cards.themeListCard;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment{
	private ArrayList<Card> cards=new ArrayList<Card>();
	
	private CardArrayAdapter mCardArrayAdapter;

	private SharedPreferences settings;
	Set<String> favoriteStops=new HashSet<String>();
	
	public HomeFragment(){}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        settings=getActivity().getSharedPreferences("mySettings",0);
        cards=setCardList();
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	
    	Card weather=new Card(getActivity());
    	CardHeader title=new CardHeader(getActivity());
    	title.setTitle("weather card");
    	weather.addCardHeader(title);
    	weather.setClickable(true);
    	weather.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
            }
        });
    	CardView cardView = (CardView) getActivity().findViewById(R.id.favoriteWeatherCard);
        cardView.setCard(weather);
        
    	mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);
    	mCardArrayAdapter.setEnableUndo(true);
		CardListView listView = (CardListView) getActivity().findViewById(R.id.favoriteListView);
		AnimationAdapter animCardArrayAdapter = new AlphaInAnimationAdapter(mCardArrayAdapter);
        animCardArrayAdapter.setAbsListView(listView);
        if (listView!=null){
            listView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
        }
    }
    
    public ArrayList<Card> setCardList(){
    	ArrayList<Card> result=new ArrayList<Card>();
    	
    	settings=getActivity().getSharedPreferences("mySettings",0);
    	favoriteStops=settings.getStringSet("favorite", null);
    	if (favoriteStops==null){
    		return result;
    	}
    	for (String stop: favoriteStops){
    		stopListCard temp = new stopListCard(getActivity());
    		String stopId=stop.split(",")[0];
    		String stopName=stop.split(",")[1];
    		temp.setId(stop);
    		temp.setBackgroundResourceId(HomeActivity.pressedCardBackground[HomeActivity.theme]);
    		temp.setContent(stopId, stopName);
    		temp.setSwipeable(true);
    		result.add(temp);
    		//listen to swipe action. when swipe, remove the stop from favorite
    		temp.setOnSwipeListener(new Card.OnSwipeListener() {
                @Override
                public void onSwipe(Card card) {
                	Set<String> favoriteStops2=settings.getStringSet("favorite", new HashSet<String>());
                	favoriteStops2.remove(card.getId());
                	SharedPreferences.Editor editor=settings.edit();
		        	editor.clear();
		        	editor.putStringSet("favorite",favoriteStops2);
		        	editor.commit();
                }
            });
    		//listen to undo action. when undo, add the stop back to favorite
    		temp.setOnUndoSwipeListListener(new OnUndoSwipeListListener() {
                @Override
                public void onUndoSwipe(Card card) {
                	Set<String> favoriteStops2=settings.getStringSet("favorite", new HashSet<String>());
                	favoriteStops2.add(card.getId());
                	SharedPreferences.Editor editor=settings.edit();
		        	editor.clear();
		        	editor.putStringSet("favorite",favoriteStops2);
		        	editor.commit();
                }
            });
    	}
    	//Log.i("mytag",String.valueOf(result.size()));
    	return result;
    }

}
