package com.angtft.munch.library;

import java.util.HashMap;

public class Ingredient
{
	public int id;
	public String name;
	
	public Ingredient(int id, String name)
	{
		this.id = id;
		this.name = name;
		
		ingredients.put(name, this);
	}
	
	public static HashMap<String, Ingredient> ingredients = new HashMap<String, Ingredient>();
}