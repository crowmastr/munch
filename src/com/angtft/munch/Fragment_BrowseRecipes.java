package com.angtft.munch;

/** Import Listing */
import java.util.ArrayList; 
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
import android.widget.Spinner;

import com.angtft.munch.library.DataArrays;
import com.angtft.munch.library.UserFunctions;
	 

	/**  Fragment_BrowseRecipes 
	 *	 This Module is used to read in Recipes from the database.
	 *	 
	 *	 
	 *	 Created: 		  3/27/2014
	 *	 Latest Revision: 3/27/2014
	 *
	 *	 Author: Eric Boggs
	 *
	 */
	public class Fragment_BrowseRecipes extends Fragment_AbstractTop {

		/** Class Field Declarations */
	    /** Unused at this time 
	     * private String 				 token;
	     * private String                ingredients;
	     */
		private UserFunctions 		 userFunctions;
		private ArrayAdapter<String> recipeAdapter;
	    private Button 				 btnFilter;    /** Used to submit the filter in the edit text */
	 	private ListView 			 recipeListView; /** Displays inventoryList */	
	    private boolean 			 filter = false; /** Flag to determine whether to filter ingredientList before adding to spinner */
	    private int 				 selectedRecipeID = -1; /** Holds the position of the selected inventoryList item, initialized to sentinel value */
	    
	    /** Called when the view is created, Initializes key Variables, and loads the view with any necessary data */
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.fragment_browse_recipes,
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
	            
	            btnFilter = (Button) view.findViewById(R.id.btnFilterRecipes);
	            btnFilter.setOnClickListener(new View.OnClickListener()
	            {
					
					@Override
					public void onClick(View v) 
					{
						/** When filter button is selected, change filter flag to true and proceed to populate */
						filter = true;
						PopulateList();
						
					}
				});
	            
	            /** Initialize ListView for displaying the recipes */
	            recipeListView = (ListView) view.findViewById(R.id.recipeListView);
	            recipeListView.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						/** Get the string at the selected Item position and save it to selectedIngredient */
						selectedRecipeID = position;
						Log.i("RecipeListener", Integer.toString(selectedRecipeID));
					}
	            	
	            });
		    	
		    	/** Set adapter for inventoryListView */
		    	recipeAdapter = new ArrayAdapter<String>(getActivity(), 
		    			android.R.layout.simple_list_item_1,
		    			new ArrayList<String>(Recipe.recipes.keySet()));
		    	
		    	recipeListView.setAdapter(recipeAdapter);

		    	
	        		
	            
	            
	            /** 
	             * On create, Re-Populate inventoryListView if there are any items in the List
	             * If IngredientList is empty, Query ingredients from server. 
	             */
	            LoadRecipeLists();
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
	    
	    public void PopulateList() 
	    {
	    	/** CallFilterIngredients before loading Ingredients.*/
	    	FilterRecipes();
	    	try
	    	{
		    	Log.i("PopulateList", "Entering PopulateList");
		    	
		    	
		        recipeAdapter.notifyDataSetChanged();
	    	}
	    	catch(Exception e)
	    	{
	    		/** Should an exception take place, print it out to Log */
	    		e.printStackTrace();
	    	}
	    } 
	    
	    /** Function used to find filter value from EditText on view and only add 
	     *  ingredients to the spinner that meet the filter's requirements.
	     *  Called by: PopulateSpinner()
	     */
	    public void FilterRecipes()
	    {
	    	Log.i("FilterRecipes", "Entering Filter Recipes");
	    	/** Clear out current contents. Faster cleaner way to make sure there are no duplicates
	    	 * than to iterate through list and find if it already exists else add.
	    	 */
	    	recipeAdapter.clear();
	    	
	    	/** Validate that filtering is necessary, if so check each recipe for filter value */
	    	if(filter)
	    	{
	    		Log.i("FilterRecipes", "Filter was true");
		    	EditText filterEditText = (EditText) getActivity().findViewById(R.id.filterEditText);
		    	String filter = filterEditText.getText().toString();
		    	for(String recipeName : Recipe.recipes.keySet())
		    	{
		    		if (recipeName.toLowerCase().contains(filter.toLowerCase()))
		    			recipeAdapter.add(recipeName);    		
		    	}
	    	}
	    	/** Otherwise, add all recipes in the database */
	    	else
	    	{
	    		android.util.Log.w("Browse Recipes", "START: We are in Filter and filter was not true.");
	    		for(String recipeName : Recipe.recipes.keySet())
	    		{
	    			recipeAdapter.add(recipeName);
	    			Log.w("Browse Recipes - Filter", "name = " + recipeName);
	    		}
	    		android.util.Log.w("Browse Recipes", "END: We are in Filter and filter was not true.");
	    	}
	    }
	    
	    /** This class defines an asynchronous task that populates the ingredients
	     *  in the static Ingredient list held by Ingredients.java
	     * 	Called by: LoadIngredientsLists()
	     * 
	     */
	    public class LoadRecipes extends AsyncTask<Void, Void, String> 
	    {
	       
	        @Override   
	        protected String doInBackground(Void... params) {
	        	
	        	/** Call function defined in project library to retrieve ingredients from server */
	            UserFunctions userFunction = new UserFunctions();
	            android.util.Log.w("Before LoadRecipes","We are about to load recipes");
	            JSONObject json = userFunction.listRecipes();
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
	                        	JSONObject json_recipe = null;


	                            try
	                            {
	                            	json_recipe = json.getJSONObject(key);
	                            	Log.i("GetKey", "Success" + i + "Retreiving: " + json_recipe.getString("name") + ": " + json_recipe.getString("id"));
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
	                            
	                            if( json_recipe != null)
	                            { 
	                            	/** Load the json name key into list */
	                            	try
	                            	{
	                            		String name = json_recipe.getString("name");
	                            		if (name != null)
	                            		{
	                            			new Recipe(Integer.parseInt(json_recipe.getString("id")), json_recipe.getString("name"));
	                            			Log.i("LoadArray", "Loaded: " + json_recipe.getString("name") + ": " + json_recipe.getString("id"));
	                            		}
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
	            PopulateList();

	        }
	    }
	    
	    /**
	     * Initialization Function for the ingredients and inventory Lists.
	     * Called by onCreate()
	     */
	    private void LoadRecipeLists()
	    {
	    	/**
	    	 * If the inventory List of ingredients is not empty, initialize The adapter that loads the chosen ingredients
	    	 * into the list view display.
	    	 */
	    	if (!Recipe.recipes.isEmpty())
	    	{
		    	recipeAdapter = new ArrayAdapter<String>(getActivity(), 
		    			android.R.layout.simple_list_item_1,
		    			new ArrayList<String>(Recipe.recipes.keySet()));
		    	
		    	recipeListView.setAdapter(recipeAdapter);
		    	recipeAdapter.notifyDataSetChanged();
	    	}
	    	
	    	/** If the complete ingredient list is empty, then LoadRecipes must be called */
	    	if (Recipe.recipes.isEmpty())
	    	{
	            LoadRecipes AsyncRecipeList = new LoadRecipes();
	            AsyncRecipeList.execute();
	    	}
	    	/** Else call PopulateSpinner to reinitialize */
	    	else
	    		PopulateList();	    	
	    }
	}
