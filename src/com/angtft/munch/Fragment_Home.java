package com.angtft.munch;

import java.util.HashMap;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
 
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.UserFunctions;
 
public class Fragment_Home extends Fragment_AbstractTop {

    String token;
	UserFunctions userFunctions;
   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,
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
        if(!userFunctions.isUserLoggedIn(context)){
            Fragment fragment = new Fragment_Login();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frame_container, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        }    
        
        return view;
    }
}