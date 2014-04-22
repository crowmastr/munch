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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.angtft.munch.library.DataArrays;
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.Ingredient;
import com.angtft.munch.library.UserFunctions;
import com.angtft.munch.slidingmenu.adapter.NavDrawerListAdapter;

	 


	public class Fragment_Build_Inventory extends Fragment_AbstractTop {

		/** Class Field Declarations */
	    /** Unused at this time 
	     * private String                ingredients;
	     */
		private UserFunctions 		 userFunctions;
		private ArrayAdapter<String> filteredIngredientAdapter;
	    private Button 				 btnFilter;    /** Used to submit the filter in the edit text */ 
	    private List<String> 	 filteredIngredientList = new ArrayList<String>(); /** List of ingredients in the spinner */
	    private Button 				 btnAddIngredient; /** Used to select the spinner's item and add to the Active list */	
	    private Button				 btnAddShopping; /** Used to add ingredients to the shopping list */
	    private ListView			 lvIngredients; /** Used to display all ingredients passing filter */
	    private int					 selectedIngredientID; /** keeps track of position in the list that has been seleccted */
	    private boolean 			 filter = false; /** Flag to determine whether to filter ingredientList before adding to spinner */
		private String               token;
		private EditText 			filterEditText;

	    
	    /** Called when the view is created, Initializes key Variables, and loads the view with any necessary data */
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.fragment_browse_inventory,
	                container, false);
	        
	        ((MainActivity)getActivity()).setTitle("Browse Ingredients");
	        /** Used for DB connections to access the users shoppingList */
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
	            
	            btnFilter = (Button) view.findViewById(R.id.btnFilterIngredients);
	            btnFilter.setOnClickListener(new View.OnClickListener()
	            {
					
					@Override
					public void onClick(View v) 
					{
						/** When filter button is selected, change filter flag to true and proceed to populate */
						filter = true;
						FilterIngredients();
						
					}
				});
	            
	            /** Initialize spinner for ingredient selection */
	            lvIngredients = (ListView) view.findViewById(R.id.ingredientsListView);
	            lvIngredients.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						if(filteredIngredientList.size() != 0)
						{
							/** Get the string at the selected Item position and save it to selectedIngredient */
						    selectedIngredientID = position;
							Log.i("ActiveIngredientListener", Integer.toString(selectedIngredientID));

						}
					}
	            	
	            });
		    	
	        		
	            
	            /** Initialize Button to add the selected Spinner ingredient to the inventoryList */
	            btnAddIngredient = (Button) view.findViewById(R.id.addInventory);
	            btnAddIngredient.setOnClickListener(new View.OnClickListener()
	            {
	            
	            	@Override
	            	public void onClick(View v)
	            	{
	        	    	Log.i("AddIngredient", "Attempting to add ingredient to inventory");

	        	    	/** Check sentinel value, and if valid remove item */
	        	    	if(selectedIngredientID != -1)
	        	    	{
		            		AddToInventory(selectedIngredientID);
	        	    		//filteredIngredientAdapter.notifyDataSetChanged();
	        	    	}

	            	}
	            	
	            }
	            );
	            
	            btnAddShopping = (Button)	view.findViewById(R.id.addShoppingList);
	            btnAddShopping.setOnClickListener(new View.OnClickListener()
	            {
	            
	            	@Override
	            	public void onClick(View v)
	            	{
	        	    	Log.i("AddShopping", "Attempting to add ingredient to shopping List");

	        	    	/** Check sentinel value, and if valid remove item */
	        	    	if(selectedIngredientID != -1)
	        	    	{
		            		AddToShoppingList(selectedIngredientID);
	        	    		//filteredIngredientAdapter.notifyDataSetChanged();
	        	    	}

	            	}
	            	
	            }
	            );
	            
		        filteredIngredientAdapter = new ArrayAdapter<String>(getActivity(),
		                R.layout.custom_listview, filteredIngredientList);
		     
		        /** attaching data adapter to spinner, should populate */
		        lvIngredients.setAdapter(filteredIngredientAdapter);
	            
	            // Note
	            /** 
	             * On create, Re-Populate inventoryListView if there are any items in the List
	             * If IngredientList is empty, Query ingredients from server. 
	             * 
	             * Now loaded in Home Fragment
	             
	            LoadIngredientsLists();
	            new LoadShoppingList().execute();
	            */
		        FilterIngredients();
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
	    
	    

	    /** Function used to display the ingredients in a spinner allowing the users to select the ones they need. 
	     * CalledBy: LoadIngredients Class
	     * 		   : LoadIngredientList()
	     * 		   : btnFilter.onClick()
	     */
	    
	    
	    /** Function used to Add provided ingredient to the inventoryList
	     *  Called by: btnAddIngredient.onClick 
	     */
	    public void AddToInventory(int i)
	    {
	    	
	    	/** Verify that ingredient is not already in the List*/
	    	for(int j = 0; j < DataArrays.inventoryList.size(); ++j)
	    	{
	    		if(DataArrays.inventoryList.get(j).toString().equals(filteredIngredientList.get(i)))
	    			return;
	    	}
	    	Log.i("AddToInventory", "Attempting to add Ingredient");


	    	/** Add Ingredient to List */
	    	DataArrays.inventoryList.add(filteredIngredientList.get(i));
	    	/** Inform adapter of change */
	    	filteredIngredientAdapter.notifyDataSetChanged();
	    	if(DataArrays.inventoryList.size() != 0)
	    		Log.i("AddIngredient", DataArrays.inventoryList.get(DataArrays.inventoryList.size() - 1) + " added to inventory.");
	    	else
	    		Log.w("AddIngredient", "It is very likely that the ingredient was added to the inventoryList");
	    	
	    	/** increment counter on side menu for inventory*/
	    	
	    	try {
	    		
	    		String count = ((MainActivity)getActivity()).navDrawerItems.get(2).getCount();
	    		((MainActivity)getActivity()).navDrawerItems.get(2).setCount(Integer.toString(Integer.parseInt(count) + 1));
	    	    Log.i("AddIngredient", "count: " + Integer.toString(Integer.parseInt(count) + 1));
	    	 
	    	    NavDrawerListAdapter adapter = new NavDrawerListAdapter(getActivity(),
	    	    		((MainActivity)getActivity()).navDrawerItems);
	    	    ((MainActivity)getActivity()).mDrawerList.setAdapter(adapter);
	    	    
	    	    
	    	} catch(NumberFormatException nfe) {}	    	
	    }

	    public void AddToShoppingList(final int i)
	    {

	    	class AddShopping extends AsyncTask<Void, Void, String> 
		    {

	    		String res = "";
	    		boolean success = false;
		       

				@Override   
		        protected String doInBackground(Void... params)
		        {
					Log.i("Why isnt' it adding", Integer.toString(DataArrays.shoppingList.size()));
					
					for(int j = 0; j < DataArrays.shoppingList.size(); ++j)
					{
						Log.i("AddToShoppingList", "DataArrays.shoppingList.get(j): " + DataArrays.shoppingList.get(j));
						if(DataArrays.shoppingList.get(j) == filteredIngredientList.get(i))
						{
							Log.w("AddToShoppingList", "Selected ingredient = ShoppingListIngredient(" + j + ").");
							return res;
						}
					}
						
	    			

			    	UserFunctions myUser = new UserFunctions();
		            JSONObject json = myUser.addIngredientShopping(Ingredient.ingredients.get(filteredIngredientList.get(i)).GetId(), token);

		            
		            Integer myInt = -1;
		            try
		            {
		            	 myInt= json.getInt("ingredient_id");
		            }
		            catch(JSONException e)
		            {
		            	e.printStackTrace();
		            }
		            if(myInt < 0) 
		            	Log.e("AddShopping", "Could not add ingredient");
		            else
		            {
		            DataArrays.shoppingListID.add(myInt);
		            DataArrays.shoppingList.add(filteredIngredientList.get(i));
		            }
		            success = true;
		            return res;
		        } 
				@Override
				protected void onPostExecute(String log)
				{
			    	/** increment counter on side menu for shopping list*/
			    	
					if(success)
					{
				    	try {
				    		
				    		String count = ((MainActivity)getActivity()).navDrawerItems.get(4).getCount();
				    		((MainActivity)getActivity()).navDrawerItems.get(4).setCount(Integer.toString(Integer.parseInt(count) + 1));
				    	    Log.i("AddIngredient", "count: " + Integer.toString(Integer.parseInt(count) + 1));
				    	 
				    	    NavDrawerListAdapter adapter = new NavDrawerListAdapter(getActivity(),
				    	    		((MainActivity)getActivity()).navDrawerItems);
				    	    ((MainActivity)getActivity()).mDrawerList.setAdapter(adapter);
				    	    
				    	    
				    	} catch(NumberFormatException nfe) {}	
				    	catch(NullPointerException npe){
				    		Log.e("AddToShoppingList-onPostExecute", "Null pointer error, possibly connectivity issue");
				    	}
					}
				}
		        
		        
		    }
	    	AddShopping as = new AddShopping();
	    	as.execute();
	    	Log.i("AddToShoppingList", "Added " + filteredIngredientList.get(i) + " to the shoppingList");
	    	

	    }
	    
	    /** Function used to find filter value from EditText on view and only add 
	     *  ingredients to the spinner that meet the filter's requirements.
	     *  Called by: PopulateSpinner()
	     */
	    public void FilterIngredients()
	    {
	    	String filterText;
	    	Log.i("FilterIngredients", "Entering FilterIngredients");
	    	/** Clear out current contents. Faster cleaner way to make sure there are no duplicates
	    	 * than to iterate through list and find if it already exists else add.
	    	 */
	    	filteredIngredientList.clear();
	    	
	    	/** Validate that filtering is necessary, if so check each ingredient for filter value */
	    	if(filter)
	    	{
	    		Log.i("FilterIngredients", "There is a filter, Apply.");
		    	EditText filterEditText = (EditText) getActivity().findViewById(R.id.filterEditText);
		    	
		    	try{
		    		filterText = filterEditText.getText().toString();
		    		for(String ingredientName : Ingredient.ingredients.keySet())
		    		{
		    			if (ingredientName.toString().toLowerCase().contains(filterText.toLowerCase()))
		    				filteredIngredientList.add(ingredientName);   		
		    		}
		    	}
		    	catch(NullPointerException npe){
		    		Log.i("FilterIngredients", "There was no filter, add all ingredients");
		    		for(String ingredientName : Ingredient.ingredients.keySet())
		    		{
		    			Log.i("FilteIngredients", "Adding: " + ingredientName);
		    			filteredIngredientList.add(ingredientName);
		    		}
		    	}
		    	
	    	}
	    	/** Otherwise, add all ingredients in the database */
	    	else
	    	{
	    		Log.i("FilterIngredients", "There was no filter, add all ingredients");
	    		for(String ingredientName : Ingredient.ingredients.keySet())
	    		{
	    			Log.i("FilteIngredients", "Adding: " + ingredientName);
	    			filteredIngredientList.add(ingredientName);
	    		}
	    	}
	    	filteredIngredientAdapter.notifyDataSetChanged();
		    		
	    }
	   
	}
