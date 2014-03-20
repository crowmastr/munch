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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
	private Object spinIngredients;
        
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
  
        /**
         * Dashboard Screen for the application
         * */       
        // Check login status in database
        userFunctions = new UserFunctions();
        if(userFunctions.isUserLoggedIn(getApplicationContext())){
       // user already logged in show databoard
            setContentView(R.layout.dashboard);
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
        new LoadSpinner();
    }
    /**
     * Adding spinner data
     *
     */
    private void PopulateSpinner(List<String> ingredientNames) {

    	
    	
        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ingredientNames);
     
        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     
        // attaching data adapter to spinner
        Spinner spinnerFood = (Spinner) this.findViewById(R.id.spinIngredients);
        spinnerFood.setAdapter(spinnerAdapter);
    } 
    
    public class LoadSpinner extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
        	
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.listIngredients();
            String res = "";
            //String id = "";
            //String name = "";
            

            // check for login response
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    res = json.getString(KEY_SUCCESS); 
                    if(Integer.parseInt(res) == 1){

                        List<String> ingredientNameList = new ArrayList<String>();
                    	Iterator<?> keys = json.keys();
                    	while( keys.hasNext() ){
                            String key = (String)keys.next();
                            if( json.get(key) instanceof JSONObject ){
                            	//Load the json name key into list
                            	ingredientNameList.add(json.getString("id")); 	
                            }
                        }
                    	PopulateSpinner(ingredientNameList);
                    	
                    	
                    	
                        // Launch Dashboard Screen
                        Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                        //dashboard.putExtra("TOKEN", token);
                         
                        // Close all views before launching Dashboard
                        dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dashboard);
                        
                        // Close Login Screen
                        finish();
                    }else{
                        // Error in login
                    	//do nothing here, action is done in onPostExecute
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return res;
        }
        
        @Override
        protected void onPostExecute (String logged){
            super.onPostExecute(logged);
            
            //your stuff
            //you can pass params, launch a new Intent, a Toast...     
            if (!logged.equals("1")) {
            	
            }
            else {
            	
            }
            //new LoadSpinner();
        }
    }
}