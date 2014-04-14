package com.angtft.munch;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.angtft.munch.Fragment_Register.UserRegisterTask;
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.UserFunctions;

/**  Fragment_Login 
 *	 This Module is used to log a user into the service.
 *	 The data returned from the JSON query includes: Name, Email, UserID, Date Key Created, Token
 *	 The data return is stored in a local SQLite database for quick and easy retrieval by other classes
 *	 The token is used to ensure the user requesting access to the Munch API is a logged in user.
 * 
 *	 Created: 		  2/19/2014
 *	 Latest Revision: 3/27/2014
 *
 *	 Author: Eric Boggs
 *
 */

public class Fragment_Login extends Fragment_AbstractTop {
	/** Class Field Declarations */
	Button btnLogin;
    Button btnLinkToRegister;
    EditText inputEmail;
    EditText inputPassword;
    TextView loginErrorMsg;

    /** Called when the view is created, Initializes key Variables, and loads the view with any necessary data */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View loginView = inflater.inflate(R.layout.fragment_login,
    		container, false);
 
    	/** Importing all assets like buttons, text fields */
        inputEmail = (EditText) loginView.findViewById(R.id.loginEmail);
        inputPassword = (EditText) loginView.findViewById(R.id.loginPassword);
        btnLogin = (Button) loginView.findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) loginView.findViewById(R.id.btnLinkToRegisterScreen);
        loginErrorMsg = (TextView) loginView.findViewById(R.id.login_error); 
        
        /** login button listener */
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
            	Context context = getActivity();
            	if (!inputEmail.getText().toString().isEmpty())
            	{
            		if (android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches())
                	{
            			if (!inputPassword.getText().toString().isEmpty())
                    	{
            				/** create async task to get data from remote server.  android requires any data retrieved over
                        	a network connection be async to prevent the app from freezing in the case of network or data issues */
                            UserLoginTask AsyncLogin = new UserLoginTask();
                            AsyncLogin.execute();
                    	}
            			else
            			{
            				int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, "You must enter a password", duration);
                            toast.show();
            			}
                	}
            		else 
            		{
            			int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, "You must enter a valid email", duration);
                        toast.show();
            		}
            		
            	}
            	else 
            	{
            		int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, "You must enter an email", duration);
                    toast.show();
            	}
            }
        });
 
        /** register button listener */
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
            	/** clear error message textview */
            	loginErrorMsg.setText("");
            	
            	/** change to register fragment view */
            	Fragment fragment = new Fragment_Register();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        
        return loginView;
    }
    
    /** This class defines an asynchronous task that attempts to log
     *  the user in
     * 	Called by: btnLogin
     * 
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
        	/** get input from user */
        	String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            
            /** create network connection and retrieve response */
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(email, password);
            String res = "";
            

            /** check for login response */
            try {
                if (json.getString(KEY_SUCCESS) != null) { /** check for null response */
                    res = json.getString(KEY_SUCCESS); /** response is not null, store success value */
                    if(Integer.parseInt(res) == 1){  
                        /** user successfully logged in
                        Store user details in SQLite Database */
                    	
                    	/** get unique token for user for future authentication for this session */
                    	String token;
                    	token = json.getString(KEY_TOKEN);
                    	                         
                    	/** get response user object from json */
                        DatabaseHandler db = new DatabaseHandler(getActivity());
                        JSONObject json_user = json.getJSONObject("user");
                         
                        /** Clear all previous data in local database */
                        userFunction.logoutUser(getActivity());
                        
                        /** add data from json to local database for future reference */
                        db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT), token);                        
                         
                        /** Load Home Screen fragment */
                        Fragment fragment = new Fragment_Home();
	                    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
	                    ft.replace(R.id.frame_container, fragment);
	                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	                    ft.addToBackStack(null);
	                    ft.commit();
                    }else{
                        /** Error in login
                    	do nothing here, action is done in onPostExecute */
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                	//login failed get error message
                	res = json.getString(KEY_ERROR_MSG); 
                }
                catch (JSONException e2)
                {
                	res = "Unknown Error";
                }
            }
            return res; /** string containing success string value */
        }
        
        @Override
        protected void onPostExecute (String logged){
            super.onPostExecute(logged);
            try
            {
            	/** check if login failed */
                if (!logged.equals("1")) {
                	loginErrorMsg.setText("Incorrect username/password");
                }
                else {
                	loginErrorMsg.setText("");
                }
            }
            catch (Exception e) {
            	Context context = getActivity();
            	int duration = Toast.LENGTH_LONG;
            	Toast toast = Toast.makeText(context, logged, duration);
            	toast.show();
            }
            
        }
    }
}