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
import java.util.Iterator;
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
import android.widget.Spinner;

import com.angtft.munch.library.DataArrays;
import com.angtft.munch.library.Ingredient;
import com.angtft.munch.library.UserFunctions;
	 


	public class Fragment_Inventory extends Fragment_AbstractTop {

		/** Class Field Declarations */
	    /** Unused at this time 
	     * private String 				 token;
	     * private String                ingredients;
	     */
		private UserFunctions 		 userFunctions;
		private ArrayAdapter<String> inventoryAdapter;
	    private Button 				 btnFilter;    /** Used to submit the filter in the edit text */
	    private Spinner 			 spinnerFood; /** contains all ingredients that pass by filter */    
	    private List<String> 		 spnIngredientList = new ArrayList<String>(); /** List of ingredients in the spinner */
	    private Button 				 btnAddIngredient; /** Used to select the spinner's item and add to the Active list */
	    private Button 				 btnRemIngredient; /** Used to remove the selected ingredient from Active List */
	 	private ListView 			 inventoryListView; /** Displays inventoryList */	
	    private boolean 			 filter = false; /** Flag to determine whether to filter ingredientList before adding to spinner */
	    private int 				 selectedIngredientID = -1; /** Holds the position of the selected inventoryList item, initialized to sentinel value */
	    
	    /** Called when the view is created, Initializes key Variables, and loads the view with any necessary data */
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.fragment_iventory,
	                container, false);
	        
	        
	        /** Used for testing of the token commented out
	        DatabaseHandler db = new DatabaseHandler(container.getContext());
	        HashMap<String,String> user = new HashMap<String,String>();
	        user = db.getUserDetails();

	        token = user.get(KEY_TOKEN);
	        Context context = container.getContext();
	        int duration = Toast.LENGTH_LONG;
	        

		        Toast toast = Toast.makeText(context, token, duration);
		        toast.show();
		     */
	        
	     
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
						PopulateSpinner();
						
					}
				});
	            
	            /** Initialize spinner for ingredient selection */
	            spinnerFood = (Spinner) view.findViewById(R.id.spinIngredients);

	            /** Initialize ListView for displaying the chosen ingredients known as inventory */
	            inventoryListView = (ListView) view.findViewById(R.id.activeIngredientListView);
	            inventoryListView.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						/** Get the string at the selected Item position and save it to selectedIngredient */
						selectedIngredientID = position;
						Log.i("ActiveIngredientListener", Integer.toString(selectedIngredientID));
					}
	            	
	            });
		    	
		    	/** Set adapter for inventoryListView */
		    	inventoryAdapter = new ArrayAdapter<String>(getActivity(), 
		    			android.R.layout.simple_list_item_1,
		    			DataArrays.inventoryList);
		    	
		    	inventoryListView.setAdapter(inventoryAdapter);

		    	
	        		
	            
	            /** Initialize Button to add the selected Spinner ingredient to the inventoryList */
	            btnAddIngredient = (Button) view.findViewById(R.id.addActiveIngredient);
	            btnAddIngredient.setOnClickListener(new View.OnClickListener()
	            {
	            
	            	@Override
	            	public void onClick(View v)
	            	{
	            		Log.d("AddIngredient", "Attempting to add Ingredient");
	                	Log.i("AddIngredient", "Selected Spinner: " + spinnerFood.getSelectedItem().toString());
	            		AddIngredient(spinnerFood.getSelectedItem().toString());
	            	}
	            	
	            }
	            );
	            
	            /** Initalize Button to remove the selected inventoryList from the inventoryList */
	            btnRemIngredient = (Button) view.findViewById(R.id.removeActiveIngredient);
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
							
							
					}
				});
	            
	            /** 
	             * On create, Re-Populate inventoryListView if there are any items in the List
	             * If IngredientList is empty, Query ingredients from server. 
	             */
	            LoadIngredientsLists();
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
	    
	    public void PopulateSpinner() 
	    {
	    	/** CallFilterIngredients before loading Ingredients.*/
	    	FilterIngredients();
	    	try
	    	{
		    	Log.i("PopulateSpinner", "Entering PopulateSpinner");
		    	
		    	
		        /** Creating adapter for spinner */
		        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
		                android.R.layout.simple_spinner_item);
	
	
		        for( int i = 0; i < spnIngredientList.size(); ++i)
		        	spinnerAdapter.add(spnIngredientList.get(i));
	
		        
		        
		        /** Drop down layout style - list view with radio button */
		        spinnerAdapter
		                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		     
		        /** attaching data adapter to spinner, should populate */
		        spinnerFood.setAdapter(spinnerAdapter);
	    	}
	    	catch(Exception e)
	    	{
	    		/** Should an exception take place, print it out to Log */
	    		e.printStackTrace();
	    	}
	    } 
	    
	    /** Function used to Add provided ingredient to the inventoryList
	     *  Called by: btnAddIngredient.onClick 
	     */
	    public void AddIngredient(String ingredient)
	    {
	    	
	    	/** Verify that ingredient is not already in the List*/
	    	for(String activeIngredient : DataArrays.inventoryList)
	    	{
	    		if(activeIngredient.equals(ingredient))
	    			return;
	    	}
	    	Log.i("PopulateActiveIngredientView", "Entering PopulateActiveIngredientView");


	    	/** Add Ingredient to List */
	    	DataArrays.inventoryList.add(ingredient);
	    	/** Inform adapter of change */
	    	inventoryAdapter.notifyDataSetChanged();
	    }
	    
	    /** Fucntion used to Remove selected ingredient from inventoryList
	     *  Called by: btnRemoveIngredint.onClick
	     */
	    public void RemoveIngredient()
	    {
	    	Log.i("RemoveActiveIngredient", "Attempting to remove ingredient");

	    	/** Check sentinel value, and if valid remove item */
	    	if(selectedIngredientID != -1)
	    	{
	    		DataArrays.inventoryList.remove(selectedIngredientID);
	    		inventoryAdapter.notifyDataSetChanged();
	    	}
	   
	    	
	    }
	    
	    /** Function used to find filter value from EditText on view and only add 
	     *  ingredients to the spinner that meet the filter's requirements.
	     *  Called by: PopulateSpinner()
	     */
	    public void FilterIngredients()
	    {
	    	/** Clear out current contents. Faster cleaner way to make sure there are no duplicates
	    	 * than to iterate through list and find if it already exists else add.
	    	 */
	    	spnIngredientList.clear();
	    	
	    	/** Validate that filtering is necessary, if so check each ingredient for filter value */
	    	if(filter)
	    	{
		    	EditText filterEditText = (EditText) getActivity().findViewById(R.id.filterEditText);
		    	String filter = filterEditText.getText().toString();
		    	for(String ingredientName : Ingredient.ingredients.keySet())
		    	{
		    		if (ingredientName.toLowerCase().contains(filter.toLowerCase()))
		    			spnIngredientList.add(ingredientName);    		
		    	}
	    	}
	    	/** Otherwise, add all ingredients in the database */
	    	else
	    		for(String ingredientName : Ingredient.ingredients.keySet())
	    			spnIngredientList.add(ingredientName);
		    		
	    }
	    
	    /** This class defines an asynchronous task that populates the ingredients
	     *  in the static Ingredient list held by Ingredients.java
	     * 	Called by: LoadIngredientsLists()
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
	            PopulateSpinner();

	        }
	    }
	    
	    /**
	     * Initialization Function for the ingredients and inventory Lists.
	     * Called by onCreate()
	     */
	    private void LoadIngredientsLists()
	    {
	    	/**
	    	 * If the inventory List of ingredients is not empty, initialize The adapter that loads the chosen ingredients
	    	 * into the list view display.
	    	 */
	    	if (!DataArrays.inventoryList.isEmpty())
	    	{
		    	inventoryAdapter = new ArrayAdapter<String>(getActivity(), 
		    			android.R.layout.simple_list_item_1,
		    			DataArrays.inventoryList);
		    	
		    	inventoryListView.setAdapter(inventoryAdapter);
		    	inventoryAdapter.notifyDataSetChanged();
	    	}
	    	
	    	/** If the complete ingredient list is empty, then LoadIngredients must be called */
	    	if (Ingredient.ingredients.isEmpty())
	    	{
	            LoadIngredients AsyncIngredientSpinner = new LoadIngredients();
	            AsyncIngredientSpinner.execute();
	    	}
	    	/** Else call PopulateSpinner to reinitialize */
	    	else
	    		PopulateSpinner();	    	
	    }
	}
