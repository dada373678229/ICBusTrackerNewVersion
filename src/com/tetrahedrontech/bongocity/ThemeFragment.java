package com.tetrahedrontech.bongocity;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;

import com.tetrahedrontech.bongocity.cards.themeListCard;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ThemeFragment extends Fragment{
		//this arraylist contains all stops
		private ArrayList<Card> themes=new ArrayList<Card>();
		CardArrayAdapter mCardArrayAdapter;
			
		public static String[] themeNames=new String[]{"Light_Blue","Light_Purple","Light_Green","Light_Pink","Light_Salmon","Gold","Cyan"};
		public static int[] themeCardStyles=new int[]{R.drawable.theme_card_selector_light_blue,R.drawable.theme_card_selector_light_purple,R.drawable.theme_card_selector_light_green,R.drawable.theme_card_selector_light_pink,R.drawable.theme_card_selector_light_salmon,R.drawable.theme_card_selector_gold,R.drawable.theme_card_selector_cyan};
		
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
			for (int i=0;i<themeNames.length;i++){
				Card theme=new themeListCard(getActivity());
				((themeListCard) theme).setContent(themeNames[i]);
				theme.setBackgroundResourceId(themeCardStyles[i]);
				theme.setId(Integer.toString(i));
				themes.add(theme);
			}
		}
}
