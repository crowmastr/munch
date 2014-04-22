/*
 * Fragment_AbstractTop.java
 * 
 * Base class for Fragment_About
 * 				  Fragment_BrowseRecipes
 *  			  Fragment_Home
 *  			  Fragment_Inventory
 *  			  Fragment_Login
 *  			  Fragment_NearbyStores
 *  			  Fragment_Register
 *  			  Fragment_ShoppingList
 */

package com.angtft.munch;

import android.app.Fragment;
import android.os.Bundle;

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
	
	
}

