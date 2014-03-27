package com.angtft.munch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;
	 


import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.UserFunctions;
	 
	public class Fragment_Inventory extends Fragment_AbstractTop {

	    String token;
		UserFunctions userFunctions;
	    Button btnLogout;
	    String ingredients;
	    
	    /**
	     *  Variables for finding Ingredients from Server and adding to list of active ingredients
	     */
		ArrayAdapter<String> activeIngredientAdapter;
	    Button btnFilter;    // Used to submit the filter in the edit text 
	    Spinner spinnerFood; // contains all ingredients that pass by filter
	    static List<String> ingredientList = new ArrayList<String>(); //List of all ingredients
	    static List<String> spnIngredientList = new ArrayList<String>(); //List of ingredients in the spinner
	    static int initialized = 0;
	    public static final List<String> activeIngredientList = new ArrayList<String>(); //List of chosen ingredients
	    String selectedIngredient;

	    Button btnAddIngredient; // Used to select the spinner's item and add to the Active list
	    Button btnRemIngredient; // Used to remove the selected ingredient from Active List
	 	ListView activeIngredientListView;
	    boolean filter = false;
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.fragment_iventory,
	                container, false);
	       
	        DatabaseHandler db = new DatabaseHandler(container.getContext());
	        HashMap<String,String> user = new HashMap<String,String>();
	        user = db.getUserDetails();
	        token = user.get(KEY_TOKEN);
	        Context context = container.getContext();
	        int duration = Toast.LENGTH_LONG;
	        
	        Toast toast = Toast.makeText(context, token, duration);
	        toast.show();
	        
	     
	        // Check login status in database
	        userFunctions = new UserFunctions();
	        if(userFunctions.isUserLoggedIn(container.getContext())){
	       // user already logged in show databoard
	            //setContentView(R.layout.dashboard);
	 
	            
	            /* Load up the spinner with necessary information 
	             * Commented out because it bugs out,
	             * When ready to test, change PopulateSpinner to accept List<String>
	             * Also, uncomment the PopulateSpinner Call within LoadSpinner()
	             */ 
	        	
	          
	            /*
	            //PopulateSpinner();
	            /**
	             * Dashboard Screen for the application
	             * */ 
	            
	            btnLogout = (Button) view.findViewById(R.id.btnLogout);
	            
	             
	            btnLogout.setOnClickListener(new View.OnClickListener() 
	            {
	                 
	                public void onClick(View arg0) {
	                    // TODO Auto-generated method stub
	                	Context context = getActivity();
	                    userFunctions.logoutUser(context);
	                    /*
	                    Intent login = new Intent(container.getContext(), LoginActivity.class);
	                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                    startActivity(login);
	                    // Closing dashboard screen
	                    finish(); */
	                    Fragment fragment = new Fragment_Login();

	                    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
	                    ft.replace(R.id.frame_container, fragment);
	                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	                    ft.addToBackStack(null);
	                    ft.commit();
	                }
	            });
	            
	            btnFilter = (Button) view.findViewById(R.id.btnFilterIngredients);
	            btnFilter.setOnClickListener(new View.OnClickListener()
	            {
					
					@Override
					public void onClick(View v) 
					{
						// TODO Auto-generated method stub
						filter = true;
						PopulateSpinner();
						
					}
				});
	            
	            spinnerFood = (Spinner) view.findViewById(R.id.spinIngredients);


	            activeIngredientListView = (ListView) view.findViewById(R.id.activeIngredientListView);
	            activeIngredientListView.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						/**
						 * Find selected ingredient Index
						 */
						selectedIngredient = parent.getAdapter().getItem(position).toString();
						Log.i("ActiveIngredientListener", selectedIngredient);
					}
	            	
	            });
	            btnAddIngredient = (Button) view.findViewById(R.id.addActiveIngredient);
	            btnAddIngredient.setOnClickListener(new View.OnClickListener()
	            {
	            
	            	@Override
	            	public void onClick(View v)
	            	{
	            		Log.d("AddIngredient", "Attempting to add Ingredient");
	                	Log.i("AddIngredient", "Selected Spinner: " + spinnerFood.getSelectedItem().toString());
	            		AddActiveIngredient(spinnerFood.getSelectedItem().toString());
	            	}
	            	
	            }
	            );
	            
	            btnRemIngredient = (Button) view.findViewById(R.id.removeActiveIngredient);
	            btnRemIngredient.setOnClickListener(new View.OnClickListener()
	            {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(selectedIngredient.isEmpty())
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
	            
	            LoadIngredients();
	        }else{
	            /* // user is not logged in show login screen
	            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
	            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(login);
	            // Closing dashboard screen
	            finish(); */
	            
	            Fragment fragment = new Fragment_Login();

                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
	        }
	        return view;
	    }
	    
	    /**
	     * Adding spinner data
	     *	List<String> ingredientList
	     */
	    public void PopulateSpinner() 
	    {
	    	FilterIngredients();
	    	try
	    	{
	    	Log.i("PopulateSpinner", "Entering PopulateSpinner");
	    	
	    	
	        // Creating adapter for spinner
	        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
	                android.R.layout.simple_spinner_item);


	        for( int i = 0; i < spnIngredientList.size(); ++i)
	        	spinnerAdapter.add(spnIngredientList.get(i));

	        
	        
	        // Drop down layout style - list view with radio button
	        spinnerAdapter
	                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     
	        // attaching data adapter to spinner, should populate
	        spinnerFood.setAdapter(spinnerAdapter);
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    } 
	    
	    public void AddActiveIngredient(String ingredient)
	    {
	    	
	    	for(String activeIngredient : Ingredients.inventoryList)
	    	{
	    		if(activeIngredient.equals(ingredient))
	    			return;
	    	}
	    	Log.i("PopulateActiveIngredientView", "Entering PopulateActiveIngredientView");


	    	Ingredients.inventoryList.add(ingredient);
	    	
	    	activeIngredientAdapter = new ArrayAdapter<String>(getActivity(), 
	    			android.R.layout.simple_list_item_1,
	    			Ingredients.inventoryList);
	    	
	    	activeIngredientListView.setAdapter(activeIngredientAdapter);
	    	activeIngredientAdapter.notifyDataSetChanged();
	    	
	    }
	    
	    public void RemoveIngredient()
	    {
	    	Log.i("RemoveActievIngredient", "Attempting to remove ingredient");
	    	for(String activeIngredient : Ingredients.inventoryList)
	    	{
	    		if(activeIngredient.equals(selectedIngredient))
	    		{
	    			Log.d("RemoveIngredient-Function" , "Ingredient found");
	    			Ingredients.inventoryList.remove(activeIngredient);
	    			activeIngredientAdapter.notifyDataSetChanged();
	    		}
	    	}
	    	
	    }
	    
	    public void FilterIngredients()
	    {
	    	spnIngredientList.clear();
	    	
	    	if(filter)
	    	{
		    	EditText filterEditText = (EditText) getActivity().findViewById(R.id.filterEditText);
		    	String filter = filterEditText.getText().toString();
		    	for(String ingredientName : Ingredients.allIngredients)
		    	{
		    		if (ingredientName.toLowerCase().contains(filter.toLowerCase()))
		    			spnIngredientList.add(ingredientName);    		
		    	}
	    	}
	    	else
	    		for(String ingredientName : Ingredients.allIngredients)
	    			spnIngredientList.add(ingredientName);
		    		
	    }
	    
	    

	    
	    public class LoadIngredients extends AsyncTask<Void, Void, String> 
	    {
	       
	        @Override   
	        protected String doInBackground(Void... params) {
	        	
	            UserFunctions userFunction = new UserFunctions();
	            android.util.Log.w("Before listIngredients","We are about to enter listIngredients");
	            JSONObject json = userFunction.listIngredients();
	            String res = "";
	            
	            Integer counterIngredients = 0;
	            // check for json response
	            try {
	                if (json.getString(KEY_SUCCESS) != null){
	                	android.util.Log.w("Succesful Get String", "We were able to successfully get jsonString");
	                    res = json.getString(KEY_SUCCESS); 
	                    if(Integer.parseInt(res) == 1){


	                    	Iterator<?> keys = json.keys();
	                    	while( keys.hasNext() ){
	                            String key = (String)keys.next();
	                            int i = 1;
	                        	JSONObject json_ingredient = new JSONObject();


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
	                            	//android.util.Log.i("successful Get Key", json.getString());
	                            	//Load the json name key into list
	                            	try
	                            	{
	                            		String name = new String(json_ingredient.getString("name"));
	                            		if (!name.equals(null))
	                            			Ingredients.allIngredients.add(name);
	                            		//System.out.println("Trying to add " + json_ingredient.getString("name") + "\nCount is: " + ++counterIngredients);
	                                	//Log.i("LoadSpinner", "Successfully added" + json_ingredient.getString("name") + " to ingredientNameList ");
	                            	}
	                            	catch(JSONException e)
	                            	{
	                            		Log.e("LoadSpinner","Could not get string");
	                            		//e.printStackTrace();
	                            	}

	                            }
	                            
	                        }

	                    	
	                    }else{
	                        // Error in login
	                    	//do nothing here, action is done in onPostExecute
	                    }
	                }
	            } catch (JSONException e) {
	            	android.util.Log.w("JSON Exception", "Something went wrong in the try");
	                e.printStackTrace();
	            }
	            

	            return res;
	        }
	        

	        
	        
	        @Override
	        protected void onPostExecute (String logged){
	            super.onPostExecute(logged);
	            
	            Log.i("LoadSpinner()-onPostExecute", "Entering Populate Spinner");
	            PopulateSpinner();
	            Log.i("LoadSpinner()-onPostExecute", "Returned from Populate Spinner");
	            //you can pass params, launch a new Intent, a Toast...     
	            if (!logged.equals("1")) {
	            	
	            }
	            else {
	            	
	            }
	        }
	    }
	    
	    private void LoadIngredients()
	    {
	    	/**
	    	 * If the inventory List of ingredients is not empty, initialize The adapter that loads the chosen ingredients
	    	 * into the list view display.
	    	 */
	    	if (!Ingredients.inventoryList.isEmpty())
	    	{
		    	activeIngredientAdapter = new ArrayAdapter<String>(getActivity(), 
		    			android.R.layout.simple_list_item_1,
		    			Ingredients.inventoryList);
		    	
		    	activeIngredientListView.setAdapter(activeIngredientAdapter);
		    	activeIngredientAdapter.notifyDataSetChanged();
	    	}
	    	
	    	if (Ingredients.allIngredients.isEmpty())
	    	{
	            LoadIngredients AsyncIngredientSpinner = new LoadIngredients();
	            AsyncIngredientSpinner.execute();
	    	}
	    	else
	    		PopulateSpinner();	    	
	    }
	}
