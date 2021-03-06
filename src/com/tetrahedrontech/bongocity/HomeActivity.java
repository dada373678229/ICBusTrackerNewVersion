package com.tetrahedrontech.bongocity;

import com.tetrahedrontech.bongocity.NavDrawer.NavDrawerListAdapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class HomeActivity extends Activity{
	
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    
    // nav drawer title
    private CharSequence mDrawerTitle="";
 
    // used to store app title
    private CharSequence mTitle="Bongo City";
 
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    
    //current fragment
    private Fragment currentFragment;
    //temp title, activate when drawer is open
    private CharSequence tempDrawerTitle;
    
    //theme id, 0=light blue, 1=light purple, 2=light green
    public static int theme;
    public static String[] actionBarColors=new String[]{"#87CEFA","#FFBFFF","#90EE90","#FFB6C1","#FFA07A","#FFD700","#00FFFF"};
    public static int[] pressedCardBackground=new int[]{R.drawable.card_selector_light_blue,R.drawable.card_selector_light_purple,R.drawable.card_selector_light_green,R.drawable.card_selector_light_pink,R.drawable.card_selector_light_salmon,R.drawable.card_selector_gold,R.drawable.card_selector_cyan};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		//set action bar background color
		SharedPreferences settings=getSharedPreferences("themeSettings",0);
		theme=Integer.valueOf(settings.getString("theme", "0"));
		ColorDrawable cd=new ColorDrawable(Color.parseColor(actionBarColors[theme]));
		getActionBar().setBackgroundDrawable(cd);
		
		//get drawer titles and icons from @string
		navMenuTitles=getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons=getResources().obtainTypedArray(R.array.nav_drawer_icons);
		
		mDrawerList=(ListView) findViewById(R.id.list_nav_drawer);
		mDrawerList.setAdapter(new NavDrawerListAdapter(this,navMenuTitles,navMenuIcons));
		

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.ic_drawer,R.string.drawer_open,R.string.drawer_close){
			public void onDrawerClosed(View view) {
				//if users didn't click anything in the drawer title, restore original title
				if (mDrawerTitle.equals("")){
					mDrawerTitle=tempDrawerTitle;
				}
				getActionBar().setTitle(mTitle);
				//this method will call onPrepareOptionMenu(Menu menu)
				invalidateOptionsMenu();
			}
			public void onDrawerOpened(View drawerView) {
				//remove mDrawerTitle, so that when invoke invalidateOptionsMenu(), it won't display a searchview on stop tab
				//the original mDrawerTitle will be saved to a temp tile --tempDrawerTitle
				tempDrawerTitle=mDrawerTitle;
				mDrawerTitle="";
				getActionBar().setTitle("Bongo City");
				//this method will call onPrepareOptionMenu(Menu menu)
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		 
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        displayView(0);

	}
	
	//this method is called when invalidateOptionsMenu() is called
	//this method is to set up option menus
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		if (mDrawerTitle.equals("Stops")){
			// Inflate the options menu from XML
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.options_menu, menu);

		    // Get the SearchView and set the searchable configuration
		    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		    // Assumes current activity is the searchable activity
		    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		    searchView.setOnQueryTextListener(new OnQueryTextListener(){
		    	@Override
		    	public boolean onQueryTextChange(String newText){
		    		((StopFragment) currentFragment).searchUpdate(newText);
		    		return true;
		    	}
		    	@Override
		    	public boolean onQueryTextSubmit(String arg0) {
		    		return false;
		    	}
		    });
		}
		
		if (mDrawerTitle.equals("Routes")){
			
		}
		
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
 
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
         // call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
        // then it has handled the app icon touch event
 
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
        	displayView(position);
        }
    }
    
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
        case 0:
            fragment = new HomeFragment();
            break;
            
        case 1:
            fragment = new StopFragment();
            break;
            
        case 2:
            fragment = new RouteFragment();
            break;
            
        case 4:
            fragment = new ThemeFragment();
            break;
            
        case 3:
            fragment = null;
        	Intent i = new Intent(this,NearMeActivity.class);
			startActivity(i);
            break;
        
        case 5:
            fragment = new SettingsFragment();
            break;
            
        case 6:
        	fragment = new AboutFragment();
        	break;
 		
        default:
            break;
        }
        
        
        if (fragment != null) {
        	currentFragment=fragment;
        	
        	
        	
        	//change fragment
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft=fragmentManager.beginTransaction();
            ft.replace(R.id.frame_container, fragment);

            //ft.addToBackStack(navMenuTitles[position]);
                
            ft.commit();
            //set Title
            setTitle(navMenuTitles[position]);
            mDrawerTitle=navMenuTitles[position];
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        else {
            // error in creating fragment
            //Log.e("MainActivity", "Error in creating fragment");
        }
    }
    
    //set action bar title
    @Override
    public void setTitle(CharSequence title){
    	mTitle=title;
    	getActionBar().setTitle(mTitle);
    }
    
    //when back pressed, restore actionbar title
    @Override
	public void onBackPressed() {
    	/*if (!(getFragmentManager().getBackStackEntryCount()==1) && !(getFragmentManager().getBackStackEntryCount()==0)){
    		super.onBackPressed();
    		int firstEle=getFragmentManager().getBackStackEntryCount()-1;
    		setTitle(getFragmentManager().getBackStackEntryAt(firstEle).getName());
    	}
    	else {
    		super.onBackPressed();
    	}*/
	}

}
