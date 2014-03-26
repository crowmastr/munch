/*
 * AbstractTopFragment.java
 * 
 * Base class for DashboardFragment
 * 				  RegisterFragment
 *  			  LoginFragment 
 */

package com.angtft.munch;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class Fragment_AbstractTop extends Fragment {
	
	// JSON Response node names
	protected static String KEY_SUCCESS = "success";
	protected static String KEY_ERROR = "error";
	protected static String KEY_ERROR_MSG = "error_msg";
	protected static String KEY_UID = "uid";
	protected static String KEY_NAME = "name";
	protected static String KEY_EMAIL = "email";
	protected static String KEY_TOKEN = "token";
	protected static String KEY_CREATED_AT = "created_at";
	
	@Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

}
