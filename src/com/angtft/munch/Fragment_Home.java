package com.angtft.munch;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
 



import com.angtft.munch.library.DataArrays;
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.Ingredient;
import com.angtft.munch.library.Recipe;
import com.angtft.munch.library.UserFunctions;
import com.angtft.munch.slidingmenu.adapter.NavDrawerListAdapter;
 
public class Fragment_Home extends Fragment_AbstractTop {

    String userName;
	UserFunctions userFunctions;
	static boolean userHintFlag = true;
	private TextView welcomeText; /** Welcome Text Object */	
	private String               token;
   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,
                container, false);
        Context context = getActivity();
        ((MainActivity)getActivity()).setTitle("Home");
        /** Set welcome message */
        DatabaseHandler db = new DatabaseHandler(getActivity());
        HashMap<String,String> user = new HashMap<String,String>();
        user = db.getUserDetails();
        userName = user.get(KEY_NAME);
        token = user.get(KEY_TOKEN);
        welcomeText = (TextView) view.findViewById(R.id.welcome);
        welcomeText.setText("Welcome to Munch\n" + userName + "!");
        
        if (userHintFlag){
        	
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, context.getString(R.string.useMenu), duration);
            toast.show();
            userHintFlag = false;
        }
        
        
  
        /**
         * Dashboard Screen for the application
         * */       
        // Check login status in database
        userFunctions = new UserFunctions();
        if(!userFunctions.isUserLoggedIn(context)){
            Fragment fragment = new Fragment_Login();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frame_container, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
            

        }  
        else
        {
        	if(Ingredient.ingredients.isEmpty())
        	{
        		Log.i("Home Create View", "Ingredients need to be loaded");
        		new LoadIngredients().execute();
        	}
        	//update nav bar counters
        	((MainActivity)getActivity()).navDrawerItems.get(2).setCount(Integer.toString(DataArrays.inventoryList.size()));
            ((MainActivity)getActivity()).navDrawerItems.get(4).setCount(Integer.toString(DataArrays.shoppingList.size()));
    	 
    	    NavDrawerListAdapter adapter = new NavDrawerListAdapter(getActivity(),
    	    		((MainActivity)getActivity()).navDrawerItems);
    	    ((MainActivity)getActivity()).mDrawerList.setAdapter(adapter);
    	    
    	    if(Recipe.recipes.isEmpty())
    	    	new LoadRecipes().execute();
        }
        
        
        return view;
    }
    
    public class LoadIngredients extends AsyncTask<Void, Void, String> 
    {
       
        @Override   
        protected String doInBackground(Void... params) 
        {
        	
        	/** Call function defined in project library to retrieve ingredients from server */
            UserFunctions userFunction = new UserFunctions();
            android.util.Log.w("Before listIngredients","We are about to enter listIngredients");
            JSONObject json = userFunction.listIngredients();
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
                        	JSONObject json_ingredient = null;


                            try
                            {
                            	json_ingredient = json.getJSONObject(key);
                            	Log.i("HomePage", "Success" + i + "Retreiving: " + json_ingredient.getString("name") + ": " + json_ingredient.getString("id"));
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
                            		String name = Html.fromHtml(json_ingredient.getString("name")).toString();
                            		int id = Integer.parseInt(json_ingredient.getString("id"));
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
            Log.i("Intermediation", "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            Log.i("LoadShoppingList", "Entering LoadShoppingList");
        	/** Call function defined in project library to retrieve ingredients from server */
            userFunction = new UserFunctions();
            android.util.Log.w("Before listIngredients","We are about to enter listIngredients");
            json = userFunction.listIngredientShopping(token);
            res = "";
            
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
                            	Log.i("GetKey", "Success" + i + "Retreiving: " + json_ingredient.getString("ingredient"));
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
                    	    		for(String ingredientName : Ingredient.ingredients.keySet())
                    	    		{
                    	    			boolean status = true;
                    	    			if(Ingredient.ingredients.get(ingredientName).GetId() == json_ingredient.getInt("ingredient"))
                    	    			{
                    	    				for(int j = 0; j < DataArrays.shoppingList.size(); ++j)
                    						{
                    							Log.i("AddToShoppingList", "DataArrays.shoppingList.get(j): " + DataArrays.shoppingList.get(j));
                    							if(DataArrays.shoppingList.get(j) == ingredientName)
                    							{
                    								Log.w("AddToShoppingList", "Selected ingredient = ShoppingListIngredient(" + j + ").");
                    								status = false;
                    								break;
                    							}
                    						}
                    							
                    	    				if (status)
                    	    				{
	                    	    				Log.i("LoadShopping", Ingredient.ingredients.get(ingredientName).GetId() + " matches " + json_ingredient.getString("ingredient"));
	                    	    				DataArrays.shoppingList.add(ingredientName);
	                    	    				Integer myInt = json_ingredient.getInt("id");
	                    	    				DataArrays.shoppingListID.add(myInt);
	                    	    				Log.i("LoadShopping", json_ingredient.getInt("id") + " Added to idlist"); 
                    	    				}
                    	    				
                    	    			}
                    	    		}
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
        @Override
        protected void onPostExecute (String logged)
        {
            super.onPostExecute(logged);
            
            //update nav bar counters
            try{
                ((MainActivity)getActivity()).navDrawerItems.get(2).setCount(Integer.toString(DataArrays.inventoryList.size()));
                ((MainActivity)getActivity()).navDrawerItems.get(4).setCount(Integer.toString(DataArrays.shoppingList.size()));
           	 
        	    NavDrawerListAdapter adapter = new NavDrawerListAdapter(getActivity(),
        	    		((MainActivity)getActivity()).navDrawerItems);
        	    ((MainActivity)getActivity()).mDrawerList.setAdapter(adapter);
            	
            }
            catch(NullPointerException e)
            {
            	Log.w("Home-onPoseExecute", "Could not set Nav drawers, lists are empty");
            }


        }
    }

    
    @Override
    public void onResume()
    {
        super.onResume();
        new LoadIngredients().execute();
      //update nav bar counters
        try{
            ((MainActivity)getActivity()).navDrawerItems.get(2).setCount(Integer.toString(DataArrays.inventoryList.size()));
            ((MainActivity)getActivity()).navDrawerItems.get(4).setCount(Integer.toString(DataArrays.shoppingList.size()));
       	 
    	    NavDrawerListAdapter adapter = new NavDrawerListAdapter(getActivity(),
    	    		((MainActivity)getActivity()).navDrawerItems);
    	    ((MainActivity)getActivity()).mDrawerList.setAdapter(adapter);
        	
        }
        catch(NullPointerException e)
        {
        	Log.w("Home-onPoseExecute", "Could not set Nav drawers, lists are empty");
        }
        //Toast.makeText(this, "ON RESUME!!!!", Toast.LENGTH_LONG).show();
    }
    
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
                            		String name = Html.fromHtml(json_recipe.getString("name")).toString();
                            		int id = Integer.parseInt(json_recipe.getString("id"));
                            		int yield = Integer.parseInt(json_recipe.getString("yield"));
                            		String instructions = json_recipe.getString("instructions");
                            		float cpr = (float) json_recipe.getDouble("cost_per_recipe");
                            		float cps = (float) json_recipe.getDouble("cost_per_serving");
                            		String source = json_recipe.getString("source");
                            		String notes = json_recipe.getString("notes");
                            		
                            		
                            		if (name != null)
                            		{
                            			Recipe r = new Recipe(id, name);
                            			r.yield = yield;
                            			r.instructions = instructions;
                            			r.costPerRecipe = cpr;
                            			r.costPerServing = cps;
                            			r.source = source;
                            			r.notes = notes;
                            			Log.i("LoadArray", "Loaded: " + name + ": " + id);
     
                            		}
                            	}
                            	catch(JSONException e)
                            	{
                            		/** Print warning to console. Program may functionally continue, but will be missing
                            		 *  whatever ingredient erred. This intentionally catches the pair (tag , success)
                            		 */
                            		Log.w("BrowseRecipes-LoadRecipes","Could not find recipe name, string must be null");
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
