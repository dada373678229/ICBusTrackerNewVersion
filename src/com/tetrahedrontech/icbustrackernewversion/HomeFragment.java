package com.tetrahedrontech.icbustrackernewversion;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

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
	
	private int[] pressedCardBackground=new int[]{R.drawable.card_selector_light_blue,R.drawable.card_selector_light_purple,R.drawable.card_selector_light_green};
	
	private SharedPreferences settings;
	private int theme;
	
	public HomeFragment(){}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        settings=getActivity().getSharedPreferences("mySettings",0);
        theme=Integer.valueOf(settings.getString("theme", "0"));
        cards=setCardList();
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);
		CardListView listView = (CardListView) getActivity().findViewById(R.id.favoriteListView);
		AnimationAdapter animCardArrayAdapter = new AlphaInAnimationAdapter(mCardArrayAdapter);
        animCardArrayAdapter.setAbsListView(listView);
        if (listView!=null){
            listView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
        }
    }
    
    public ArrayList<Card> setCardList(){
    	ArrayList<Card> result=new ArrayList<Card>();
    	
    	settings=getActivity().getSharedPreferences(themeListCard.PREFS_NAME,0);
    	Set<String> favorite=new HashSet<String>();
    	favorite=settings.getStringSet("favorite", new HashSet<String>());
    	
    	for (String stop: favorite){
    		stopListCard temp = new stopListCard(getActivity());
    		String stopId=stop.split(",")[0];
    		String stopName=stop.split(",")[1];
    		temp.setId(stop);
    		temp.setBackgroundResourceId(pressedCardBackground[theme]);
    		temp.setContent(stopId, stopName);
    		temp.setSwipeable(true);
    		result.add(temp);
    		
    		temp.setOnSwipeListener(new Card.OnSwipeListener() {
                @Override
                public void onSwipe(Card card) {
                	settings.getStringSet("favorite", new HashSet<String>()).remove(card.getId());
                }
            });
    	}
    	
    	return result;
    }

}
