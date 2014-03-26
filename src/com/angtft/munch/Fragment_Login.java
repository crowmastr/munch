//this needs to check if already logged in show a different fragment (aka already_logged_in)

package com.angtft.munch;

import android.os.AsyncTask;

 
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.UserFunctions;
 
public class Fragment_Login extends Fragment_AbstractTop {
    Button btnLogin;
    Button btnLinkToRegister;
    EditText inputEmail;
    EditText inputPassword;
    TextView loginErrorMsg;
      
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View loginView = inflater.inflate(R.layout.fragment_login,
    		container, false);
 
    	// Importing all assets like buttons, text fields
        inputEmail = (EditText) loginView.findViewById(R.id.loginEmail);
        inputPassword = (EditText) loginView.findViewById(R.id.loginPassword);
        btnLogin = (Button) loginView.findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) loginView.findViewById(R.id.btnLinkToRegisterScreen);
        loginErrorMsg = (TextView) loginView.findViewById(R.id.login_error); 
     // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
            	//if you want to keep the loader from Ravi
                //showProgress(true);
                //and then, you add the AsyncTask
                UserLoginTask AsyncLogin = new UserLoginTask();
                AsyncLogin.execute();
                
            }
        });
 
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
            	loginErrorMsg.setText("");
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
    
    public class UserLoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
        	String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(email, password);
            String res = "";
            

            // check for login response
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    res = json.getString(KEY_SUCCESS); 
                    if(Integer.parseInt(res) == 1){
                        // user successfully logged in
                        // Store user details in SQLite Database
                    	String token;
                    	token = json.getString(KEY_TOKEN);
                    	                         
                        DatabaseHandler db = new DatabaseHandler(getActivity());
                        JSONObject json_user = json.getJSONObject("user");
                         
                        // Clear all previous data in database
                        userFunction.logoutUser(getActivity());
                        db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT), token);                        
                         
                        // Launch Home Screen
                        Fragment fragment = new Fragment_Home();
	                    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
	                    ft.replace(R.id.frame_container, fragment);
	                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	                    ft.addToBackStack(null);
	                    ft.commit();
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
            	loginErrorMsg.setText("Incorrect username/password");
            }
            else {
            	loginErrorMsg.setText("");
            }
        }
    }
}