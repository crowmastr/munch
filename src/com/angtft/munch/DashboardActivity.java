package com.angtft.munch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.angtft.munch.LoginActivity.UserLoginTask;
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.UserFunctions;
 
public class DashboardActivity extends Activity {
	
	// JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_TOKEN = "token";
    private static String KEY_CREATED_AT = "created_at";
    String token;
    
	UserFunctions userFunctions;
    Button btnLogout;
    String ingredients;
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        HashMap<String,String> user = new HashMap<String,String>();
        user = db.getUserDetails();
        token = user.get(KEY_TOKEN);
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        
        Toast toast = Toast.makeText(context, token, duration);
        toast.show();
        
     
        // Check login status in database
        userFunctions = new UserFunctions();
        if(userFunctions.isUserLoggedIn(getApplicationContext())){
       // user already logged in show databoard
            setContentView(R.layout.dashboard);
            
            /* Load up the spinner with necessary information 
             * Commented out because it bugs out,
             * When ready to test, change PopulateSpinner to accept List<String>
             * Also, uncomment the PopulateSpinner Call within LoadSpinner()
             */ 
            LoadSpinner AsyncIngredientSpinner = new LoadSpinner();
            AsyncIngredientSpinner.execute();
          
            /*
            //PopulateSpinner();
            /**
             * Dashboard Screen for the application
             * */  
            
            
            btnLogout = (Button) findViewById(R.id.btnLogout);
            
             
            btnLogout.setOnClickListener(new View.OnClickListener() {
                 
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    userFunctions.logoutUser(getApplicationContext());
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    // Closing dashboard screen
                    finish();
                }
            });

        }else{
            // user is not logged in show login screen
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            // Closing dashboard screen
            finish();
        }

    }
    /**
     * Adding spinner data
     *	List<String> ingredientList
     */
    private void PopulateSpinner() 
    {
    	List<String> testString = new ArrayList<String>();
    	testString.add("First ingredient");
    	testString.add("Second Ingredient");
    	
        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
     
        /* Comment out when LoadSpinner is being tested. Remove when LoadSpinner is fixed */
    	for( int i = 0; i < testString.size(); ++i)
    		spinnerAdapter.add(testString.get(i));
        /* Commented out for testing, this requires that populate Spinner receives a list. When LoadSpinner is called,
         * This should be active.

        if(ingredientList.isEmpty())
        {
        	for( int i = 0; i < testString.size(); ++i)
        		spinnerAdapter.add(testString.get(i));
        }
        else
        {
	        for( int i = 0; i < ingredientList.size(); ++i)
	        	spinnerAdapter.add(ingredientList.get(i));
        }
        
        */
        
        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     
        // attaching data adapter to spinner, should populate
        Spinner spinnerFood = (Spinner) this.findViewById(R.id.spinIngredients);
        spinnerFood.setAdapter(spinnerAdapter);
    } 
    
    public class LoadSpinner extends AsyncTask<Void, Void, String> 
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

                        List<String> ingredientNameList = new ArrayList<String>();
                    	Iterator<?> keys = json.keys();
                    	while( keys.hasNext() ){
                            String key = (String)keys.next();
                            JSONObject json_ingredient = json.getJSONObject(key);
                            
                            System.out.println("This is the key string: " + key);
                            if( json_ingredient != null){ 
                            	//android.util.Log.i("successful Get Key", json.getString());
                            	//Load the json name key into list
                            	try
                            	{
                            		ingredientNameList.add(json_ingredient.getString("name"));
                            		System.out.println("Trying to add " + json_ingredient.getString("name") + "\nCount is: " + ++counterIngredients);
                                	Log.i("LoadSpinner", "Successfully added ingredient to ingredientNameList ");
                            	}
                            	catch(JSONException e)
                            	{
                            		Log.e("LoadSpinner","Could not get string");
                            		//e.printStackTrace();
                            	}

                            }
                        }
                    	//PopulateSpinner(ingredientNameList);
                    	Log.i("LoadSpinner","Returned from PopulateSpinner()");
                    	
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
            
            //your stuff
            PopulateSpinner();
            //you can pass params, launch a new Intent, a Toast...     
            if (!logged.equals("1")) {
            	
            }
            else {
            	
            }
        }
    }
}