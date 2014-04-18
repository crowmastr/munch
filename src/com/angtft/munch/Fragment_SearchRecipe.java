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

import com.angtft.munch.Fragment_Home.LoadIngredients;
import com.angtft.munch.library.DataArrays;
import com.angtft.munch.library.Ingredient;
import com.angtft.munch.library.Recipe;
import com.angtft.munch.library.UserFunctions;
import com.angtft.munch.slidingmenu.adapter.NavDrawerListAdapter;
	 


	public class Fragment_SearchRecipe extends Fragment_AbstractTop {

		/** Class Field Declarations */
	    /** Unused at this time 
	     * private String 				 token;
	     * private String                ingredients;
	     */
		private UserFunctions 		 userFunctions;
		private ArrayAdapter<String> recipeAdapter;
	    private Button 				 btnAddMoreIngredients; /** Used to add more ingredients to the inventory list */
	 	private ListView 			 recipeListView; /** Displays recipeList */	
	    private int 				 selectedRecipeID = -1; /** Holds the position of the selected recipeList item, initialized to sentinel value */
	    
	    /** Called when the view is created, Initializes key Variables, and loads the view with any necessary data */
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.fragment_search_recipe,
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

	           

		    	/** Set adapter for inventoryListView 
		    	recipeAdapter = new ArrayAdapter<String>(getActivity(), 
		    			R.layout.custom_listview,
		    			DataArrays.inventoryList);
		    	*/
		    	recipeListView = (ListView) view.findViewById(R.id.recipeListView);
	            recipeListView.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {

							String selectedRecipeName = (String) (recipeListView.getItemAtPosition(position));
							((MainActivity)getActivity()).recipeID = Recipe.findIDByName(selectedRecipeName);
							Fragment fragment = new Fragment_ShowRecipe();
			                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
			                ft.replace(R.id.frame_container, fragment);
			                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			                ft.addToBackStack(null);
			                ft.commit();
							
						}
		            	
		            });
		    	
	            	    	
	            /** Initalize Button to add more ingredients */
	            btnAddMoreIngredients = (Button) view.findViewById(R.id.searchRecipes);
	            btnAddMoreIngredients.setOnClickListener(new View.OnClickListener()
	            {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//check and make sure ingredients count > 1
						if (DataArrays.inventoryList.size() > 0)
						{
							new SearchRecipes().execute();
							//show recipeResult layout
						}
						
							
					}
				});
	            
	            
	            recipeListView.setAdapter(recipeAdapter);
	            
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
	    

	    
	    public class SearchRecipes extends AsyncTask<Void, Void, String> 
	    {
	       
	        @Override   
	        protected String doInBackground(Void... params) 
	        {
	        	
	        	/** Call function defined in project library to retrieve ingredients from server */
	            UserFunctions userFunction = new UserFunctions();
	            android.util.Log.w("Before searchRecipes","We are about to enter searchRecipes");
	            JSONObject json = userFunction.searchRecipe(DataArrays.inventoryList);
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
	                            String key = (String)keys.next();
	                            int i = 1;
	                        	JSONObject json_recipe = null;


	                            try
	                            {
	                            	json_recipe = json.getJSONObject(key);
	                            	Log.i("SearchRecipe", "Success" + i + "Retreiving: " + json_recipe.getString("name") + ": " + json_recipe.getString("id"));
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
	                            		int id = Integer.parseInt(json_recipe.getString("id"));
	                            		if (name != null)
	                            			new Ingredient(id, name);
	                            	}
	                            	catch(JSONException e)
	                            	{
	                            		/** Print warning to console. Program may functionally continue, but will be missing
	                            		 *  whatever ingredient erred. This intentionally catches the pair (tag , success) */
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
	            
	            //set adapter
	            recipeAdapter = new ArrayAdapter<String>(getActivity(), 
		    			R.layout.custom_listview,
		    			new ArrayList<String>(Recipe.recipes.keySet()));
	           
		    	
		    	recipeListView.setAdapter(recipeAdapter);
	        }
	    }
	    
	}
	    

