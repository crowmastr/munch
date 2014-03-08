package com.angtft.munch;


import org.json.JSONException;
import org.json.JSONObject;
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.UserFunctions;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
public class RegisterFragment extends AbstractTopFragment {
    Button btnRegister;
    Button btnLinkToLogin;
    EditText inputFullName;
    EditText inputEmail;
    EditText inputPassword;
    TextView registerErrorMsg;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.register,
          container, false);
 
      // Importing all assets like buttons, text fields
      inputFullName = (EditText) getView().findViewById(R.id.registerName);
      inputEmail = (EditText) getView().findViewById(R.id.registerEmail);
      inputPassword = (EditText) getView().findViewById(R.id.registerPassword);
      btnRegister = (Button) getView().findViewById(R.id.btnRegister);
      btnLinkToLogin = (Button) getView().findViewById(R.id.btnLinkToLoginScreen);
      registerErrorMsg = (TextView) getView().findViewById(R.id.register_error);
         
      // Register Button Click event
      btnRegister.setOnClickListener(new View.OnClickListener() {         
          public void onClick(View view) {
          	UserRegisterTask AsyncLogin = new UserRegisterTask();
            AsyncLogin.execute();
          }
      });
 
      
      //TODO build out fragments for replacement
      btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
    	  public void onClick(View view){
    		  FragmentTransaction ftm = getFragmentManager().beginTransaction();

    		  ftm.commit();
    	  }
      });
      return view;
    }
    
    
    public class UserRegisterTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
        	String name = inputFullName.getText().toString();
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.registerUser(name, email, password);
            String res = "";

            // check for login response
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    res = json.getString(KEY_SUCCESS); 
                    if(Integer.parseInt(res) == 1){
                    	// user successfully registered
                        // Store user details in SQLite Database
                    	
                    	//String token;
                    	//token = json.getString(KEY_TOKEN);
                        DatabaseHandler db = new DatabaseHandler(getActivity());
                        JSONObject json_user = json.getJSONObject("user");
                         
                        // Clear all previous data in database
                        userFunction.logoutUser(getActivity());
                        db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT), "token not implemented in register yet");                        
                         
                        
                        //TODO replace Intent switch with fragment switching
                        // Launch Dashboard Screen
                        Intent dashboard = new Intent(getActivity(), DashboardActivity.class);
                        //dashboard.putExtra("TOKEN", token);

                        // Close all views before launching Dashboard
                        dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dashboard);
                        // Close Registration Screen
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
          
            if (Integer.parseInt(logged) != 1) {
            	registerErrorMsg.setText("Error occured in registration");
            }
            else {
            	registerErrorMsg.setText("");
            }
        }
    }
}