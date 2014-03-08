package com.angtft.munch;

import android.os.AsyncTask;

 
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.UserFunctions;
 
public class LoginFragment extends AbstractTopFragment {
    Button btnLogin;
    Button btnLinkToRegister;
    EditText inputEmail;
    EditText inputPassword;
    TextView loginErrorMsg;

 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login,
            container, false);
 
        // Importing all assets like buttons, text fields
        inputEmail = (EditText) getView().findViewById(R.id.loginEmail);
        inputPassword = (EditText) getView().findViewById(R.id.loginPassword);
        btnLogin = (Button) getView().findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) getView().findViewById(R.id.btnLinkToRegisterScreen);
        loginErrorMsg = (TextView) getView().findViewById(R.id.login_error); 
 
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
//TODO convert to Fragment transaction
            public void onClick(View view) {
//            	loginErrorMsg.setText("");
  //              Intent i = new Intent(getApplicationContext(),
   //                     RegisterActivity.class);
     //           startActivity(i);
       //         finish();
                
            }
        });
        return view;
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
                         
                        // Launch Dashboard Screen
                        //TODO convert Intent to fragment management
                        Intent dashboard = new Intent(getActivity(), DashboardActivity.class);
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
            	loginErrorMsg.setText("Incorrect username/password");
            }
            else {
            	loginErrorMsg.setText("");
            }
        }
    }
}