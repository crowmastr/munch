package com.angtft.munch.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

public class Ingredient
{
	private int id;
	private String name;
	
	public Ingredient(int id, String name)
	{
		this.id = id;
		this.name = name;
		
		ingredients.put(name, this);
	}
	public int GetId()
	{
		return id;
	}
	public String toString()
	{
		return name;
	}
	
	public boolean equals(Ingredient i)
	{
		if (i.GetId() == this.GetId())
			return true;
		else
			return false;
	}
	
	public static HashMap<String, Ingredient> ingredients = new HashMap<String, Ingredient>();
	
	public static List<String> IngredientNameToId(List<String> names){
		List<String> idList = new ArrayList<String>();
		
		for(int i = 0; i < names.size(); ++i){
			Ingredient id = Ingredient.ingredients.get(names.get(i));
			idList.add(Integer.toString(id.GetId()));		
		}
		Log.i("Ingredients-IngredientNametoId", idList.toString());
		return idList;
	}
	
}