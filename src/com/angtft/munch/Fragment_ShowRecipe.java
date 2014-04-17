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
import android.widget.Toast;

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
 	private String 				 stringIngredients = "";
 	
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
        	recipeID = ((MainActivity)getActivity()).recipeID;
        	/** Context context = container.getContext();
	        int duration = Toast.LENGTH_LONG;
	        Toast toast = Toast.makeText(context, "RecipeID: " + recipeID, duration);
		    toast.show(); */
        	
		    Recipe currRecipe = Recipe.findById(recipeID);
		    textRecipeName.setText(currRecipe.name);
        	textYield.setText(Integer.toString(currRecipe.yield));
        	textCostPerR.setText(Float.toString(currRecipe.costPerRecipe));
        	textCostPerS.setText(Float.toString(currRecipe.costPerServing));
        	textInstructions.setText(currRecipe.instructions);
        	textNotes.setText(currRecipe.notes);
        	textSource.setText(currRecipe.source);
		    
        	GetIngredients AsyncGetIngredients = new GetIngredients();
        	AsyncGetIngredients.execute();
        	
        	/** find recipe in recipe objects and get values */
        	
        	
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
     * 	Called by: onCreateView()
     * 
     */
    public class GetIngredients extends AsyncTask<Void, Void, String> 
    {
       
        @Override   
        protected String doInBackground(Void... params) {
        	
        	/** Call function defined in project library to retrieve ingredients from server */
            UserFunctions userFunction = new UserFunctions();
            android.util.Log.w("Before IngredientsRecipe","We are about to load the ingredients for a recipe");
            JSONObject json = userFunction.ingredientsRecipe(recipeID);
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
                        	JSONObject json_recipeIngredients = null;


                            try
                            {
                            	json_recipeIngredients = json.getJSONObject(key);
                            	Log.i("GetKey", "Success" + i + "Retreiving: " + json_recipeIngredients.getString("name") + ": " + json_recipeIngredients.getString("id") + 
                            	 ": " + json_recipeIngredients.getString("info"));
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
                            
                            if( json_recipeIngredients != null)
                            { 
                            	/** Load the json name key into list */
                            	try
                            	{
                            		String info = json_recipeIngredients.getString("info");
                            		
                            		if (info.equals(""))
                            			stringIngredients += json_recipeIngredients.getString("amt") + " : " + json_recipeIngredients.getString("name") + "\n";
                            		else
                            			stringIngredients += json_recipeIngredients.getString("amt") + " : " + json_recipeIngredients.getString("name") + " : " + 
                                            	info + "\n";
                            		
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
            textIngredients.setText(stringIngredients);
            
        }
    }
}
