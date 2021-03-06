/**  Fragment_Inventory 
 *	 This Module is used to read in Ingredients from the database.
 *	 At that point, the user can select ingredients to be added to his or her inventory.
 *	 The selected ingredient in the Spinner will be added to inventory when the user presses the Add Ingredient button.
 *	 The inventory will later be used to find Recipes that have matching ingredients.
 *	 The inventory is displayed to the user as a ListView at the bottom of the page.
 *	 An item in inventory can be selected and then removed by pressing the Remove Ingredient button.
 *	 
 *	 Created: 		  2/19/2014
 *	 Latest Revision: 3/27/2014
 *
 *	 Author: Jeremy Noel
 *
 */

package com.angtft.munch;

/** Import Listing */
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.angtft.munch.library.DataArrays;
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.Ingredient;
import com.angtft.munch.library.UserFunctions;
import com.angtft.munch.slidingmenu.adapter.NavDrawerListAdapter;
	 


	public class Fragment_View_Shopping_List extends Fragment_AbstractTop {

		/** Class Field Declarations */
	    /** Unused at this time 
	     * private String 				 token;
	     * private String                ingredients;
	     */
		private UserFunctions 		 userFunctions;
		private ArrayAdapter<String> inventoryAdapter;
	    private Button 				 btnRemIngredient; /** Used to remove the selected ingredient from Active List */
	 	private ListView 			 inventoryListView; /** Displays inventoryList */	
	    private int 				 selectedIngredientID = -1; /** Holds the position of the selected inventoryList item, initialized to sentinel value */
	    private String 				 token;
	    
	    /** Called when the view is created, Initializes key Variables, and loads the view with any necessary data */
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.fragment_view_shopping_list,
	                container, false);
	        
	        ((MainActivity)getActivity()).setTitle("Shopping List");
	        /**Used for accessing Shopping List */
	        DatabaseHandler db = new DatabaseHandler(container.getContext());
	        HashMap<String,String> user = new HashMap<String,String>();
	        user = db.getUserDetails();


        	                         
	        token = user.get(KEY_TOKEN);
	        //Context context = container.getContext();
	        //int duration = Toast.LENGTH_LONG;
	        

	        //Toast toast = Toast.makeText(context, token, duration);
	        //toast.show();

	     
	        /** Check login status in database */
	        userFunctions = new UserFunctions();
	        if(userFunctions.isUserLoggedIn(container.getContext())){
	        	/** If the user is already Logged in, Proceed to load the rest of the content */

	           

		    	/** Set adapter for inventoryListView */
		    	inventoryAdapter = new ArrayAdapter<String>(getActivity(), 
		    			R.layout.custom_listview,
		    			DataArrays.shoppingList);
		    	
		    	inventoryListView = (ListView) view.findViewById(R.id.shoppingListView);
	            inventoryListView.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {

							if(DataArrays.shoppingList.size() != 0)
							{
								/** Get the string at the selected Item position and save it to selectedIngredient */
							    selectedIngredientID = position;
								Log.w("ActiveIngredientListener", Integer.toString(selectedIngredientID));
							}
						}
		            	
		            });
		    	inventoryListView.setAdapter(inventoryAdapter);

	            
	            /** Initalize Button to remove the selected inventoryList from the inventoryList */
	            btnRemIngredient = (Button) view.findViewById(R.id.removeShoppingIngredient);
	            btnRemIngredient.setOnClickListener(new View.OnClickListener()
	            {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(selectedIngredientID == -1)
						{
							Log.d("RemoveIngredient", "No Selected Ingredient, Exiting");
							return;
						}
						else
						{
							Log.d("RemoveIngredient", "Calling RemoveIngredient");
							RemoveIngredient();
						}
						
						try {
				    		
				    		String count = ((MainActivity)getActivity()).navDrawerItems.get(4).getCount();
				    		((MainActivity)getActivity()).navDrawerItems.get(4).setCount(Integer.toString(Integer.parseInt(count) - 1));
				    	    Log.i("AddIngredient", "count: " + Integer.toString(Integer.parseInt(count) - 1));
				    	 
				    	    NavDrawerListAdapter adapter = new NavDrawerListAdapter(getActivity(),
				    	    		((MainActivity)getActivity()).navDrawerItems);
				    	    ((MainActivity)getActivity()).mDrawerList.setAdapter(adapter);
							
						}
						catch(NumberFormatException nfe) {}	
					}
				});
	            
	            
	            inventoryAdapter.notifyDataSetChanged();
	            
	            
	            /** 
	             * On create, Re-Populate inventoryListView if there are any items in the List
	             * If IngredientList is empty, Query ingredients from server. 
	             */
	        }else{
	        	
	            /**
	             * user is not logged in show login screen
	             */
	            Fragment fragment = new Fragment_Login();

                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
	        }
	        return view;
	    }
	    


	    public void RemoveIngredient()
	    {

	    	class RemShopping extends AsyncTask<Void, Void, String> 
		    {

	    		String res = "";
		       
		        @Override   
		        protected String doInBackground(Void... params)
		        {
		        	if(DataArrays.shoppingListID.size() == 0)
		        		Log.i("ShoppingListID", "The list is empty");
		        	else
		        	{
		        		try
		        		{
				        	for(int i = 0; i < DataArrays.shoppingListID.size(); ++i)
				        	{
				        		Log.i("ShoppingListId", DataArrays.shoppingListID.get(i).toString());
				        	}
		        		}
		        		catch(Exception e)
		        		{
		        			e.printStackTrace();
		        		}
		        	} 
 

			    	UserFunctions myUser = new UserFunctions();
			    	Log.i("RemoveIngredient", "My token is: " + token);
		            //myUser.delIngredientShopping(Ingredient.ingredients.get(DataArrays.shoppingList.get(selectedIngredientID)).GetId(), token);
			    	Log.i("RemoveIngredient", "Id attempting to remove: " + (int)DataArrays.shoppingListID.get(selectedIngredientID));
			    	try
			    	{
			    	myUser.delIngredientShopping((int)DataArrays.shoppingListID.get(selectedIngredientID), token);
			    	} 
			    	catch(Exception e)
			    	{
			    		e.printStackTrace();
			    	}
		            DataArrays.shoppingList.remove(selectedIngredientID);
		            DataArrays.shoppingListID.remove(selectedIngredientID);

		            return res;
		        }  
		        
		        
		        protected void onPostExecute(String r)
		        {
			    	inventoryAdapter.notifyDataSetChanged();
		        }
		    }
	    	new RemShopping().execute();

	    }
	   
	}

	    

