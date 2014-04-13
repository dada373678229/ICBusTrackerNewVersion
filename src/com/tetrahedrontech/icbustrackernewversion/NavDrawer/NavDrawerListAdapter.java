package com.tetrahedrontech.icbustrackernewversion.NavDrawer;

import java.util.ArrayList;
import java.util.List;

import com.tetrahedrontech.icbustrackernewversion.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerListAdapter extends ArrayAdapter<String>{
	private Context context;
	private String[] NavDrawerItems;
	
	public NavDrawerListAdapter(Context context,String[] values) {
		super(context,R.layout.nav_drawer_list_item_layout,values);
		this.context = context;
		this.NavDrawerItems = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView=inflater.inflate(R.layout.nav_drawer_list_item_layout, parent, false);
		TextView textView=(TextView) itemView.findViewById(R.id.drawer_list_item_text_view);
		ImageView imageView=(ImageView) itemView.findViewById(R.id.drawer_list_item_image_view);
		imageView.setImageResource(R.drawable.ic_favorite);
		textView.setText(NavDrawerItems[position]);
		return itemView;
	}
	
	
}
