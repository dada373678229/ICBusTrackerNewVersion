package com.tetrahedrontech.icbustrackernewversion;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;

import com.tetrahedrontech.icbustrackernewversion.cards.themeListCard;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ThemeFragment extends Fragment{
		//this arraylist contains all stops
		private ArrayList<Card> themes=new ArrayList<Card>();
		CardArrayAdapter mCardArrayAdapter;
			
		private String[] themeNames=new String[]{"Light Blue","Light_Purple","Light_Green"};
		private int[] pressedCardBackground=new int[]{R.drawable.card_selector_light_blue,R.drawable.card_selector_light_purple,R.drawable.card_selector_light_green};
		
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_themes, container, false);
			
			initThemeCards();
			
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			mCardArrayAdapter = new CardArrayAdapter(getActivity(),themes);
			CardListView listView = (CardListView) getActivity().findViewById(R.id.themeListView);
	        if (listView!=null){
	            listView.setAdapter(mCardArrayAdapter);
	        }
		}
		
		private void initThemeCards(){
			for (int i=0;i<3;i++){
				Card theme=new themeListCard(getActivity());
				((themeListCard) theme).setContent(themeNames[i]);
				theme.setBackgroundResourceId(pressedCardBackground[i]);
				theme.setId(Integer.toString(i));
				themes.add(theme);
			}
		}
}
