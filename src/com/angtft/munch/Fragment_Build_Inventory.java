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
import java.util.Iterator;
import java.util.List;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.angtft.munch.library.DataArrays;
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.Ingredient;
import com.angtft.munch.library.UserFunctions;
	 


	public class Fragment_Build_Inventory extends Fragment_AbstractTop {

		/** Class Field Declarations */
	    /** Unused at this time 
	     * private String 				 token;
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
	    
	    /** Called when the view is created, Initializes key Variables, and loads the view with any necessary data */
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.fragment_browse_inventory,
	                container, false);
	        
	        
	        /** Used for DB connections to access the users shoppingList */
	        DatabaseHandler db = new DatabaseHandler(container.getContext());
	        HashMap<String,String> user = new HashMap<String,String>();
	        user = db.getUserDetails();


	        token = user.get(KEY_TOKEN);
	        Context context = container.getContext();
	        int duration = Toast.LENGTH_LONG;
	        

		        Toast toast = Toast.makeText(context, token, duration);
		        toast.show();
		     
	        
	     
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
		                android.R.layout.simple_list_item_1, filteredIngredientList);
		     
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
	    }

	    public void AddToShoppingList(final int i)
	    {

	    	class AddShopping extends AsyncTask<Void, Void, String> 
		    {

	    		String res = "";
		       

				@Override   
		        protected String doInBackground(Void... params)
		        {
					Log.i("Why isnt' it adding", Integer.toString(DataArrays.shoppingList.size()));
					/**
			    	for(int k = 0; k < DataArrays.shoppingList.size(); ++k)
			    	{
			    		Log.w("Why isn't it adding" , DataArrays.shoppingList.get(k));
			    		Log.w("Why isn't it adding" , filteredIngredientList.get(i));
			    		if(filteredIngredientList.get(i) == (DataArrays.shoppingList.get(k)));
			    		{
			    			Log.w("AddToShoppingList", "Function believes ingredient is already in shoppingList");
			    			/**
			    			Context context = container.getContext();
					        Toast toast = Toast.makeText(context, token, duration);
					        toast.show();
					        
			    			return res;
			    		}

			    	}
	    			*/

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
		            return res;
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
		    	String filter = filterEditText.getText().toString();

		    	for(String ingredientName : Ingredient.ingredients.keySet())
		    	{
		    		if (ingredientName.toString().toLowerCase().contains(filter.toLowerCase()))
		    			filteredIngredientList.add(ingredientName);   		
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
	    
	    /** This class defines an asynchronous task that populates the ingredients
	     *  in the static Ingredient list held by Ingredients.java
	     * 	Called by: LoadIngredientsLists()
	     * 
	     *  No longer in use, loaded in the Home Fragment
	     * 
	     */
	    public class LoadIngredients extends AsyncTask<Void, Void, String> 
	    {
	       
	        @Override   
	        protected String doInBackground(Void... params) {
	        	
	        	/** Call function defined in project library to retrieve ingredients from server */
	            UserFunctions userFunction = new UserFunctions();
	            android.util.Log.w("Before listIngredients","We are about to enter listIngredients");
	            JSONObject json = userFunction.listIngredients();
	            String res = "";
	            
	            // check for json response
	            /** Try to receive JSON pair, and load ingredient name into Ingredients.allIngredients */
	            try {
	                if (json.getString(KEY_SUCCESS) != null){
	                	android.util.Log.w("Succesful Get String", "We were able to successfully get jsonString");
	                    res = json.getString(KEY_SUCCESS); 
	                    if(Integer.parseInt(res) == 1){


	                    	Iterator<?> keys = json.keys();
	                    	while( keys.hasNext() ){
	                            String key = (String)keys.next();
	                            int i = 1;
	                        	JSONObject json_ingredient = null;


	                            try
	                            {
	                            	json_ingredient = json.getJSONObject(key);
	                            	Log.i("GetKey", "Success" + i + "Retreiving: " + json_ingredient.getString("name") + ": " + json_ingredient.getString("id"));
	                            	++i;
	                            }
	                            catch(JSONException e)
	                            {
	                            	if (key.equals("tag"))	
	                            		Log.w("JsonGet-Exception", "key = tag");
	                            	else
	                            		e.printStackTrace();
	                            }

	                            //System.out.println("This is the key string: " + key);
	                            
	                            if( json_ingredient != null)
	                            { 
	                            	/** Load the json name key into list */
	                            	try
	                            	{
	                            		String name = json_ingredient.getString("name");
	                            		int id = Integer.parseInt(json_ingredient.getString("id"));
	                            		if (name != null)
	                            			new Ingredient(id, name);
	                            	}
	                            	catch(JSONException e)
	                            	{
	                            		/** Print warning to console. Program may functionally continue, but will be missing
	                            		 *  whatever ingredient erred. This intentionally catches the pair (tag , success)
	                            		 */
	                            		Log.w("LoadSpinner","Could not get string");
	                            	}
	                            }           
	                        }	
	                    }
	                }
	            } 
	            /** Print out any JSON Exception */
	            catch (JSONException e) {
	            	android.util.Log.w("JSON Exception", "Something went wrong in the try");
	                e.printStackTrace();
	            }
	            

	            return res;
	        }        
	        
	        @Override
	        protected void onPostExecute (String logged)
	        {
	            super.onPostExecute(logged);
	            
	            /** Upon completion of LoadIngredients , Populate the spinner with the found ingredients */
	            FilterIngredients();

	        }
	    }
	    
	    /**
	     * Initialization Function for the ingredients and inventory Lists.
	     * Called by onCreate()
	     * 
	     * UPDATE: Outdated, this has been moved to the home menu to load as soon as program begins.
	     */
	    private void LoadIngredientsLists()
	    {
	    	
	    	/** If the complete ingredient list is empty, then LoadIngredients must be called */
	    	if (Ingredient.ingredients.isEmpty())
	    	{
	            LoadIngredients AsyncIngredientSpinner = new LoadIngredients();
	            AsyncIngredientSpinner.execute();
	    	}
	    	/** Else call PopulateSpinner to reinitialize */
	    	else
	    		FilterIngredients();	    	
	    }
	    public class LoadShoppingList extends AsyncTask<Void, Void, String> 
	    {
	       
	        @Override   
	        protected String doInBackground(Void... params) {
	        	
	        	/** Call function defined in project library to retrieve ingredients from server */
	            UserFunctions userFunction = new UserFunctions();
	            android.util.Log.w("Before listIngredients","We are about to enter listIngredients");
	            JSONObject json = userFunction.listIngredientShopping(token);
	            String res = "";
	            
	            // check for json response
	            /** Try to receive JSON pair, and load ingredient name into Ingredients.allIngredients */
	            try {
	                if (json.getString(KEY_SUCCESS) != null){
	                	android.util.Log.w("Succesful Get String", "We were able to successfully get jsonString");
	                    res = json.getString(KEY_SUCCESS); 
	                    if(Integer.parseInt(res) == 1){


	                    	Iterator<?> keys = json.keys();
	                    	while( keys.hasNext() ){
	                            String key = (String)keys.next();
	                            int i = 1;
	                        	JSONObject json_ingredient = null;


	                            try
	                            {
	                            	json_ingredient = json.getJSONObject(key);
	                            	Log.i("GetKey", "Success" + i + "Retreiving: " + json_ingredient.getString("name") + ": " + json_ingredient.getString("id"));
	                            	++i;
	                            }
	                            catch(JSONException e)
	                            {
	                            	if (key.equals("tag"))	
	                            		Log.w("JsonGet-Exception", "key = tag");
	                            	else
	                            		e.printStackTrace();
	                            }

	                            //System.out.println("This is the key string: " + key);
	                            
	                            if( json_ingredient != null)
	                            { 
	                            	/** Load the json name key into list */
	                            	try
	                            	{
	                            		DataArrays.shoppingList.add(json_ingredient.getString("name"));
	                            	}
	                            	catch(JSONException e)
	                            	{
	                            		/** Print warning to console. Program may functionally continue, but will be missing
	                            		 *  whatever ingredient erred. This intentionally catches the pair (tag , success)
	                            		 */
	                            		Log.w("LoadShoppingList","Could not get string");
	                            	}
	                            }           
	                        }	
	                    }
	                }
	            } 
	            /** Print out any JSON Exception */
	            catch (JSONException e) {
	            	android.util.Log.w("JSON Exception", "Something went wrong in the try");
	                e.printStackTrace();
	            }
	            

	            return res;
	        }   
	    }
	}
