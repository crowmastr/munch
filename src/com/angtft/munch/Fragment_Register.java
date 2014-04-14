package com.angtft.munch;


import org.json.JSONException;
import org.json.JSONObject;
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.UserFunctions;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.widget.Toast;
 
public class Fragment_Register extends Fragment_AbstractTop {
    Button btnRegister;
    Button btnLinkToLogin;
    EditText inputFullName;
    EditText inputEmail;
    EditText inputPassword;
    TextView registerErrorMsg;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_register,
          container, false);
 
   // Importing all assets like buttons, text fields
      inputFullName = (EditText) view.findViewById(R.id.registerName);
      inputEmail = (EditText) view.findViewById(R.id.registerEmail);
      inputPassword = (EditText) view.findViewById(R.id.registerPassword);
      btnRegister = (Button) view.findViewById(R.id.btnRegister);
      btnLinkToLogin = (Button) view.findViewById(R.id.btnLinkToLoginScreen);
      registerErrorMsg = (TextView) view.findViewById(R.id.register_error);
         
      // Register Button Click event
      btnRegister.setOnClickListener(new View.OnClickListener() {         
          public void onClick(View view) {
        	Context context = getActivity();
        	if (inputFullName.getText().toString().isEmpty())
        	{
        		int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, "You must enter a name", duration);
                toast.show();
        	}
        	else if (inputEmail.getText().toString().isEmpty())
        	{
        		int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, "You must enter an email", duration);
                toast.show();
        	}
        	else if (inputPassword.getText().toString().isEmpty())
        	{
        		int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, "You must enter a password", duration);
                toast.show();
        	}
        	else
        	{
        		UserRegisterTask AsyncLogin = new UserRegisterTask();
        		AsyncLogin.execute();
        	}
          }
      });
 
      
      //TODO build out fragments for replacement
      btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
    	  public void onClick(View view){
    		  Fragment fragment = new Fragment_Login();
              android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
              ft.replace(R.id.frame_container, fragment);
              ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
              ft.addToBackStack(null);
              ft.commit();
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
          
            if (Integer.parseInt(logged) != 1) {
            	registerErrorMsg.setText("Error occured in registration");
            }
            else {
            	registerErrorMsg.setText("");
            }
        }
    }
}