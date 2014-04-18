package com.angtft.munch;

import java.util.HashMap;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
 
import com.angtft.munch.library.DatabaseHandler;
import com.angtft.munch.library.UserFunctions;
 
public class Fragment_About extends Fragment_AbstractTop {

    String token;
	UserFunctions userFunctions;
   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about,
                container, false);
       
        ((MainActivity)getActivity()).setTitle("About");
        return view;
    }
}