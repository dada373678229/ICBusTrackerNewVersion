package com.tetrahedrontech.bongocity.NavDrawer;

import com.tetrahedrontech.bongocity.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerListAdapter extends ArrayAdapter<String>{
	private Context context;
	private String[] NavDrawerItems;
	private TypedArray NavDrawerIcons;
	
	public NavDrawerListAdapter(Context context,String[] values,TypedArray NavDrawerIcons) {
		super(context,R.layout.nav_drawer_list_item_layout,values);
		this.context = context;
		this.NavDrawerItems = values;
		this.NavDrawerIcons=NavDrawerIcons;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView=inflater.inflate(R.layout.nav_drawer_list_item_layout, parent, false);
		TextView textView=(TextView) itemView.findViewById(R.id.drawer_list_item_text_view);
		ImageView imageView=(ImageView) itemView.findViewById(R.id.drawer_list_item_image_view);
		imageView.setImageResource(NavDrawerIcons.getResourceId(position, -1));
		textView.setText(NavDrawerItems[position]);
		return itemView;
	}
	
	
}
