package com.tetrahedrontech.bongocity.cards;

import it.gmariotti.cardslib.library.internal.Card;

import java.util.Comparator;
//this is the comparator class that helps to sort an arraylist of nearMeCards
public class nearMeCardComparator implements Comparator<Card>{
	//return negative number when arg0<arg1
	@Override
	public int compare(Card arg0, Card arg1) {
		//getDistance() will return for example "7000 ft"
		int d0=Integer.valueOf(((nearMeCard) arg0).getDistance().split(" ")[0]);
		int d1=Integer.valueOf(((nearMeCard) arg1).getDistance().split(" ")[0]);
		if (d0 < d1){
			return -1;
		}
		else if (d0 == d1){
			return 0;
		}
		else {
			return 1;
		}
	}

}
