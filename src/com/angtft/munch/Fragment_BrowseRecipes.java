package com.angtft.munch;

/** Import Listing */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.angtft.munch.library.DataArrays;
import com.angtft.munch.library.Ingredient;
import com.angtft.munch.library.Recipe;
import com.angtft.munch.library.UserFunctions;
	 

	/**  Fragment_BrowseRecipes 
	 *	 This Module is used to read in Recipes from the database.
	 *	 
	 *	 
	 *	 Created: 		  3/27/2014 - EB
	 *	 Latest Revision: 4/16/2014 - JN
	 *
	 *	 Author: Eric Boggs
	 *	 Additonal Contributors: Jeremy Noel
	 *
	 */
	@SuppressLint("ValidFragment")
	public class Fragment_BrowseRecipes extends Fragment_AbstractTop {

		/** Class Field Declarations */
	    /** Unused at this time 
	     * private String 				 token;
	     * private String                ingredients;
	     */
		private UserFunctions 		 userFunctions;
		private ArrayAdapter<String> recipeAdapter;
	    private Button 				 btnFilter;    /** Used to submit the filter in the edit text */
	    private List<String>		 inventoryRecipes;
	    private Button				 btnInvRecipe;
	    private Button				 btnAllRecipe;
	    private TextView			 tvHeader;
	 	private ListView 			 recipeListView; /** Displays inventoryList */	
	    private boolean 			 filter = false; /** Flag to determine whether to filter ingredientList before adding to spinner */
	    private String				 selectedRecipeName = ""; /** Holds the name of the selected inventoryList item, initialized to "" */
	    private String				 filterText;
	    
	    /** Called when the view is created, Initializes key Variables, and loads the view with any necessary data */
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.fragment_browse_recipes,
	                container, false);
	        
	        if (DataArrays.activeRecipeListId == null)
	        {
	        	//if we didn't come to this screen from inventory activeRecipeListId could be null if it is set it to 1.
	        	DataArrays.activeRecipeListId = 1;
	        }
	        ((MainActivity)getActivity()).setTitle("Browse Recipes");
	        
	        //final ActionBar actionBar = getActionBar();
	        
	        
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
	        
	     Log.i("BrowseRecipes-onCreateView",  "Recipes loading...");
	        /** Check login status in database */
	        userFunctions = new UserFunctions();
	        if(userFunctions.isUserLoggedIn(container.getContext())){
	        	/** If the user is already Logged in, Proceed to load the rest of the content */
	        	
	        	btnFilter = (Button)view.findViewById(R.id.btnFilterRecipe);
	            btnFilter.setOnClickListener(new View.OnClickListener()
	            {
					
					@Override
					public void onClick(View v) 
					{
						/** When filter button is selected, change filter flag to true and proceed to populate  */
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
						selectedRecipeName = (String) (recipeListView.getItemAtPosition(position));
						Log.i("RecipeListener", selectedRecipeName);
						// set activity variable to selected recipe
						((MainActivity)getActivity()).recipeID = Recipe.findIDByName(selectedRecipeName);

						Fragment fragment = new Fragment_ShowRecipe();
		                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
		                ft.replace(R.id.frame_container, fragment);
		                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		                ft.addToBackStack(null);
		                ft.commit();
					}
	            	
	            });
		    	
		    	/** Set adapter for inventoryListView */
		    	/** recipeAdapter = new ArrayAdapter<String>(getActivity(), 
		    			android.R.layout.simple_list_item_1,
		    			new ArrayList<String>(Recipe.recipes.keySet())); */
		    	recipeAdapter = new ArrayAdapter<String>(getActivity(), 
		    			R.layout.custom_listview,
		    			new ArrayList<String>(DataArrays.activeRecipes));
		    	
		    	recipeListView.setAdapter(recipeAdapter);
		    	
		    	/** List for inventory Recipe Filter */
		    	inventoryRecipes = new ArrayList<String>();
		    	
		    	
		    	btnInvRecipe = (Button) view.findViewById(R.id.btnInv);
		    	btnInvRecipe.setOnClickListener(new View.OnClickListener()
		            {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							new SearchRecipes().execute();
							
						}
		    		
		            });
		    	
		    	btnAllRecipe = (Button) view.findViewById(R.id.btnAllRecipes);
		    	btnAllRecipe.setOnClickListener(new View.OnClickListener(){
		    		
		    		@Override
		    		public void onClick(View v){
		    			DisplayAll();
		    		}
		    	});
		    	
		    	tvHeader = (TextView) view.findViewById(R.id.tvRecipeHeader);
		    	try{
					switch((int)DataArrays.activeRecipeListId){
					case 0: tvHeader.setText("All Recipes");
							break;
					case 1: tvHeader.setText("Recipes Matching Inventory");
					default: tvHeader.setText("All Recipes");
					}
		    	}
		    	catch(NullPointerException e){
		    		tvHeader.setText("All Recipes");
		    		e.printStackTrace();
		    	}
			    	
		    	

	            /** 
	             * On create, Re-Populate inventoryListView if there are any items in the List
	             * If IngredientList is empty, Query ingredients from server. 
	             */
	            if(DataArrays.activeRecipeListId== null || DataArrays.activeRecipeListId == 0)
		            PopulateList();
	            else
	            	new SearchRecipes().execute();
	            
	            }
	            else{
	        	
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
	    
	    public void DisplayAll(){
	    	DataArrays.activeRecipeListId = 0;
	    	recipeAdapter.clear();
	    	DataArrays.activeRecipes.clear();
	    	for(String recipeName : Recipe.recipes.keySet()){
	    		recipeAdapter.add(recipeName);
	    		DataArrays.activeRecipes.add(recipeName);
	    	}
	    	tvHeader.setText("All Recipes");
	    }
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
		    	EditText filterEditText = (EditText) getActivity().findViewById(R.id.filterRecipeText);
		    	
		    	
		    	try{
		    	
		    		filterText = filterEditText.getText().toString();
		    		switch(DataArrays.activeRecipeListId){
		    			//all recipes
		    			case 0: for(String recipeName : Recipe.recipes.keySet())
		    			{
		    				if (recipeName.toLowerCase().contains(filterText.toLowerCase()))
		    					recipeAdapter.add(recipeName);    		
		    			}
		    			break;
		    			//filtered recipes
		    			case 1: for(String recipeName : inventoryRecipes){
		    				if(recipeName.toLowerCase().contains(filterText.toLowerCase()))
		    					recipeAdapter.add(recipeName);
		    			}
		    		}
		    	}
		    	catch(NullPointerException npe){
		    		Log.i("BrowseRecipe", "There was no filter, add all recipes");
		    		for(String recipeName : Recipe.recipes.keySet())
		    		{
		    			recipeAdapter.add(recipeName);
		    			Log.w("Browse Recipes - Filter", "name = " + recipeName);
		    		}
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

	    public class SearchRecipes extends AsyncTask<Void, Void, String> 
	    {
	       
	        @Override   
	        protected String doInBackground(Void... params) 
	        {
	        	inventoryRecipes.clear();
	        	/** Call function defined in project library to retrieve ingredients from server */
	            UserFunctions userFunction = new UserFunctions();
	            android.util.Log.w("Before searchRecipes","We are about to enter searchRecipes");
	            JSONObject json = userFunction.searchRecipe(Ingredient.IngredientNameToId(DataArrays.inventoryList));
	            try{
	            }
	            catch(NullPointerException e){
	            	Log.i("SearchRecipes-inventoryListtest", "Appears as though the list is empty");
	            }
	            String res = "";
	            
	            // check for json response
	            /** Try to receive JSON pair, and load ingredient name into Ingredients.allIngredients */
	            try 
	            {
	                if (json.getString(KEY_SUCCESS) != null){
	                	android.util.Log.w("Succesful Get String", "We were able to successfully get jsonString");
	                    res = json.getString(KEY_SUCCESS); 
	                    if(Integer.parseInt(res) == 1){


	                    	Iterator<?> keys = json.keys();
	                    	while( keys.hasNext() ){
	                    		//JSONObject key = (JSONObject)keys.next();
	                            String key = (String)keys.next();

	                            try
	                            {
	                            
	                            	Log.i("SearchRecipes-JSON_Key", key);
	                            	JSONArray ja;
	                            	try
	                            	{
	                            		ja = json.getJSONArray(key);
	                            	}
	                            	catch (JSONException ex)
	                            	{
	                            		// Bleh for 2 elements this returns as a k:v pair.
	                            		JSONObject o = json.getJSONObject(key);
	                            		ArrayList<Integer> al = new ArrayList<Integer>();
	                            		for (Iterator<?> it = o.keys(); it.hasNext();)
	                            		{
	                            			String k = (String) it.next();
	                            			al.add(Integer.parseInt(k));
	                            			al.add(o.getInt(k));
	                            		}
	                            		ja = new JSONArray();
	                            		for (int i : al)
	                            			ja.put(i);
	                            	}
	                            	Log.i("SearchRecipes-JSONArray Fun", "Objects in JSON: " +Integer.toString(json.length()));
	                            	Log.i("SearchRecipes-JSONArray Fun", "Objects in JA: " +Integer.toString(ja.length()));
	                            	Log.i("SearchRecipes-JSONArray Fun" , ja.toString());
	                            	for(int j = 0; j < ja.length(); ++j){
	                            		try{
	                            			/** Retrieve the id from JSON */
	                            			int recipeID =ja.getInt(j);
	                            			Recipe recipe = Recipe.findById(recipeID);
	                            			try{
	                            				inventoryRecipes.add(recipe.name);
	                            				Log.i("SearchRecipes-add", "Adding: " + recipe.name);
	                            			}
	                            			catch(Exception e){
	                            				e.printStackTrace();
	                            			}
	                            		}
	                            		catch(JSONException e){
	                            			e.printStackTrace();
	                            		}
	                            		
	                            		
	                            	}
	                            	//keys.

	                            	//Log.i("SearchRecipe", "Success" + i + "Retreiving: " + json_recipe.getString("name") + ": " + json_recipe.getString("id"));
	                            	//++i;
	                            
	                            	
	                            }
	                            catch(JSONException e)
	                            {
	                            	if (key.equals("tag"))	
	                            		Log.w("JsonGet-Exception", "key = tag");
	                            	else if(key.equals("success"))
	                            		Log.w("JSONGet-Exception", "key = succes");
	                            	else
	                            		e.printStackTrace();
	                            }
	                            //System.out.println("This is the key string: " + key);
	                            
	                           
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
	            for(int i = 0; i < inventoryRecipes.size(); ++i)
	            {
	            	Log.i("SearchRecipes-onPostExecute", "inventoryRecipes(" + i + "):" + inventoryRecipes.get(i));
	            }
	            recipeAdapter.clear();
	            DataArrays.activeRecipes.clear();
	            for(String recipeName : inventoryRecipes){
	            	recipeAdapter.add(recipeName);
	            	
	            	DataArrays.activeRecipes.add(recipeName);
	            }

	            DataArrays.activeRecipeListId = 1;
	            tvHeader.setText("Recipes Matching Inventory");
	        }
	    }
	    

	    
	}

