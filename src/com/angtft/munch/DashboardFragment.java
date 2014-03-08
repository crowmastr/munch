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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
 
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.UserFunctions;
 
public class DashboardFragment extends AbstractTopFragment {

    String token;
	UserFunctions userFunctions;
    Button btnLogout;
    String ingredients;
        
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register,
                container, false);
       
        DatabaseHandler db = new DatabaseHandler(getActivity());
        HashMap<String,String> user = new HashMap<String,String>();
        user = db.getUserDetails();
        token = user.get(KEY_TOKEN);
        Context context = getActivity();
        int duration = Toast.LENGTH_LONG;
        
        Toast toast = Toast.makeText(context, token, duration);
        toast.show();
  
        /**
         * Dashboard Screen for the application
         * */       
        // Check login status in database
        userFunctions = new UserFunctions();
        if(userFunctions.isUserLoggedIn(getActivity())){
       // user already logged in show databoard
        	//TODO load dashboard fragment
  //          setContentView(R.layout.dashboard);
            btnLogout = (Button) getView().findViewById(R.id.btnLogout);
            
             
            btnLogout.setOnClickListener(new View.OnClickListener() {
                 
                public void onClick(View arg0) {
                    // TODO Convert to fragment switch
                    userFunctions.logoutUser(getActivity());
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    // Closing dashboard screen
                   // finish();
                }
            });
             
        }else{
            // user is not logged in show login screen
        	//TODO swap in login fragment
            Intent login = new Intent(getActivity(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            // Closing dashboard screen
            //finish();
        }    
        return view;
    }
    /**
     * Adding spinner data
     * 
    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();
         
        for (int i = 0; i < categoriesList.size(); i++) {
            lables.add(categoriesList.get(i).getName());
        }
     
        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);
     
        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     
        // attaching data adapter to spinner
        spinnerFood.setAdapter(spinnerAdapter);
    } */
    
    public class UserLoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
        	
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.listIngredients();
            String res = "";
            String id = "";
            String name = "";
            

            // check for login response
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    res = json.getString(KEY_SUCCESS); 
                    if(Integer.parseInt(res) == 1){

                    	Iterator<?> keys = json.keys();
                    	while( keys.hasNext() ){
                            String key = (String)keys.next();
                            if( json.get(key) instanceof JSONObject ){
                            	//this data will need to go in the spinner.
                            	id = json.getString("id");
                            	name = json.getString("name");
                            	
                            }
                        }
                    	
                    	
                    	//TODO load dashboard fragment into main ui
                        // Launch Dashboard Screen
                        Intent dashboard = new Intent(getActivity(), DashboardFragment.class);
                        //dashboard.putExtra("TOKEN", token);
                         
                        // Close all views before launching Dashboard
                        dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dashboard);
                        
                        // Close Login Screen
                        //finish();
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
        }
    }
}