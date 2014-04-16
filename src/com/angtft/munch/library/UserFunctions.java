	package com.angtft.munch.library;

import java.util.ArrayList;
import java.util.List;

 

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

 

import android.content.Context;
import android.util.Log;
 
public class UserFunctions {
     
    private JSONParser jsonParser;
     
    // Testing in localhost using wamp or xampp 
    // use http://10.0.2.2/ to connect to your localhost ie http://localhost/
    private static String loginURL = "http://munchapp.org/api/";
    private static String registerURL = "http://munchapp.org/api/";
    private static String recipeURL = "http://munchapp.org/api2/";
    private static String ilistURL = "http://munchapp.org/api/";
    private static String ingredientURL = "http://munchapp.org/api2/";
     
    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String recipe_tag = "recipe";
    private static String ilist_tag = "ilist";
    private static String ingredient_tag = "ingredient";
    private static String recipe_op_list = "list";
    private static String recipe_op_search = "search";
    private static String recipe_op_ingredients = "ingredients";
    private static String ilist_shopping = "shopping";
    private static String ilist_inventory = "inventory";
    private static String ilist_op_add = "add";
    private static String ilist_op_del = "del";
    private static String ilist_op_list = "list";
    private static String ingredient_op_list = "list";
    
    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }
     
    /**
     * function make Login Request
     * @param email
     * @param password
     * */
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
     
    /**
     * function make Register User Request
     * @param name
     * @param email
     * @param password
     * */
    public JSONObject registerUser(String name, String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
         
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        // return json
        return json;
    }
     
    /**
     * Function make get Login status
     * */
    public boolean isUserLoggedIn(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }
     
    /**
     * Function to logout user
     * Reset Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }
    
    /**
     * function make list recipes Request
     * */
    public JSONObject listRecipes(){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", recipe_tag));
        params.add(new BasicNameValuePair("op", recipe_op_list));
                
        JSONObject json = jsonParser.getJSONFromUrl(recipeURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
    
    /**
     * function make get ingredients for specific recipe Request
     * @param recipeID
     * */
    public JSONObject ingredientsRecipe(int recipeID){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", recipe_tag));
        params.add(new BasicNameValuePair("op", recipe_op_ingredients));
        params.add(new BasicNameValuePair("id", Integer.toString(recipeID)));
        
        JSONObject json = jsonParser.getJSONFromUrl(recipeURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
    
    //Notes
    
    /**
     * function make get ingredients for specific recipe Request
     * @param ingredients[]
     * */
    public JSONObject searchRecipe(List<String> ingredients){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", recipe_tag));
        params.add(new BasicNameValuePair("op", recipe_op_search));
        
        StringBuffer sb = new StringBuffer();
        for (String s : ingredients)
        {
        	if (sb.length() == 0)
        		sb.append(';');
        	sb.append(s);
        }
        params.add(new BasicNameValuePair("ingredients", sb.toString()));
        
        JSONObject json = jsonParser.getJSONFromUrl(recipeURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
    
    /**
     * function make add ingredient to shopping list Request
     * @param ingredient
     * */
    public JSONObject addIngredientShopping(int ingredient, String token){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", ilist_tag));
        params.add(new BasicNameValuePair("op", ilist_op_add));
        params.add(new BasicNameValuePair("auth", token));
        params.add(new BasicNameValuePair("list", ilist_shopping));
        params.add(new BasicNameValuePair("id", Integer.toString(ingredient)));
        
        JSONObject json = jsonParser.getJSONFromUrl(ilistURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
    
    /**
     * function make delete ingredient from shopping list Request
     * @param ingredient
     * */
    public JSONObject delIngredientShopping(int ingredient, String token){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", ilist_tag));
        params.add(new BasicNameValuePair("op", ilist_op_del));
        params.add(new BasicNameValuePair("auth", token));
        params.add(new BasicNameValuePair("list", ilist_shopping));
        params.add(new BasicNameValuePair("id", Integer.toString(ingredient)));
        Log.i("delIngredientShopping", Integer.toString(ingredient));
        
        JSONObject json = jsonParser.getJSONFromUrl(ilistURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
    
    /**
     * function make list ingredient from shopping list Request
     * */
    public JSONObject listIngredientShopping(String token){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", ilist_tag));
        params.add(new BasicNameValuePair("op", ilist_op_list));
        params.add(new BasicNameValuePair("auth", token));
        params.add(new BasicNameValuePair("list", ilist_shopping));
        
        JSONObject json = jsonParser.getJSONFromUrl(ilistURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
    
    /**
     * function make add ingredient to Inventory list Request
     * @param ingredient
     * */
    public JSONObject addIngredientInventory(int ingredient, String token){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", ilist_tag));
        params.add(new BasicNameValuePair("op", ilist_op_add));
        params.add(new BasicNameValuePair("auth", token));
        params.add(new BasicNameValuePair("list", ilist_inventory));
        params.add(new BasicNameValuePair("id", Integer.toString(ingredient)));
        
        JSONObject json = jsonParser.getJSONFromUrl(ilistURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
    
    /**
     * function make delete ingredient from Inventory list Request
     * @param ingredient
     * */
    public JSONObject delIngredientInventory(int ingredient, String token){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", ilist_tag));
        params.add(new BasicNameValuePair("op", ilist_op_del));
        params.add(new BasicNameValuePair("auth", token));
        params.add(new BasicNameValuePair("list", ilist_inventory));
        params.add(new BasicNameValuePair("id", Integer.toString(ingredient)));
        
        JSONObject json = jsonParser.getJSONFromUrl(ilistURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
    
    /**
     * function make list ingredient from Inventory Request
     * */
    public JSONObject listIngredientInventory(String token){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", ilist_tag));
        params.add(new BasicNameValuePair("op", ilist_op_list));
        params.add(new BasicNameValuePair("auth", token));
        params.add(new BasicNameValuePair("list", ilist_inventory));
        
        JSONObject json = jsonParser.getJSONFromUrl(ilistURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
    
    /**
     * function make list all ingredient Request
     * */
    public JSONObject listIngredients(){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", ingredient_tag));
        params.add(new BasicNameValuePair("op", ingredient_op_list));
        
        android.util.Log.w("listIngredients()","Before the jsonParser");
        JSONObject json = jsonParser.getJSONFromUrl(ingredientURL, params);
        // return json
        // Log.e("JSON", json.toString());
        android.util.Log.w("listIngredients()","Before the return");
        return json;
    }
     
}