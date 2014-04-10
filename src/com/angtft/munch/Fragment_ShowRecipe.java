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
import android.widget.TextView;

import com.angtft.munch.Fragment_BrowseRecipes.LoadRecipes;
import com.angtft.munch.library.DataArrays;
import com.angtft.munch.library.Recipe;
import com.angtft.munch.library.UserFunctions;
	 

	/**  Fragment_ShowRecipe 
	 *	 This Module is used to read in Recipes from the database.
	 *	 
	 *	 
	 *	 Created: 		  3/27/2014
	 *	 Latest Revision: 3/27/2014
	 *
	 *	 Author: Eric Boggs
	 *
	 */
public class Fragment_ShowRecipe extends Fragment_AbstractTop {
	/** Class Field Declarations */
    /** Unused at this time 
     * private String 				 token;
     * private String                ingredients;
     */
	private UserFunctions 		 userFunctions;
	private ArrayAdapter<String> recipeAdapter;
 	private TextView 			 textRecipeName; 
 	private TextView 			 textYield;
 	private TextView 			 textCostPerR;
 	private TextView 			 textCostPerS;
 	private TextView 			 textIngredients;
 	private TextView 			 textInstructions;
 	private TextView 			 textNotes;
 	private TextView 			 textSource;
 	private int 				 recipeID;
    /** Called when the view is created, Initializes key Variables, and loads the view with any necessary data */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_recipe,
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
            
        	textRecipeName = (TextView) view.findViewById(R.id.recipeName);
        	textYield = (TextView) view.findViewById(R.id.yield);
        	textCostPerR = (TextView) view.findViewById(R.id.costPerR);
        	textCostPerS = (TextView) view.findViewById(R.id.costPerS);
        	textIngredients = (TextView) view.findViewById(R.id.ingredients);
        	textInstructions = (TextView) view.findViewById(R.id.instructions);
        	textNotes = (TextView) view.findViewById(R.id.notes);
        	textSource = (TextView) view.findViewById(R.id.source);

            /** get active recipeID and query recipe.php for all ingredients and their amounts
             *  parse that data into textInredients */
        	GetIngredients AsyncGetIngredients = new GetIngredients();
        	AsyncGetIngredients.execute();
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
    
      
    /** This class defines an asynchronous task that populates the ingredients
     *  in the recipe into a local String variable
     * 	Called by: LoadIngredientsLists()
     * 
     */
    public class GetIngredients extends AsyncTask<Void, Void, String> 
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
                            		int id = Integer.parseInt(json_recipe.getString("id"));
                            		int yield = Integer.parseInt(json_recipe.getString("yield"));
                            		String instructions = json_recipe.getString("instructions");
                            		float cpr = (float) json_recipe.getDouble("cost_per_recipe");
                            		float cps = (float) json_recipe.getDouble("cost_per_serving");
                            		String source = json_recipe.getString("source");
                            		String notes = json_recipe.getString("notes");
                            		
                            		
                            		if (name != null)
                            		{
                            			Recipe r = new Recipe(Integer.parseInt(json_recipe.getString("id")), json_recipe.getString("name"));
                            			r.yield = yield;
                            			r.instructions = instructions;
                            			r.costPerRecipe = cpr;
                            			r.costPerServing = cps;
                            			r.source = source;
                            			r.notes = notes;
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
            
            
        }
    }
}
