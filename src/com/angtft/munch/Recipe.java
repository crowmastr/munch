package com.angtft.munch;

import java.util.HashMap;

public class Recipe
{
	public int id;
	public String name;
	
	public Recipe(int id, String name)
	{
		this.id = id;
		this.name = name;
		recipes.put(name, this);
	}
	
	public static HashMap<String, Recipe> recipes = new HashMap<String, Recipe>();
}