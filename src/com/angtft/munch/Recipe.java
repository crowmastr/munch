package com.angtft.munch;

import java.util.HashMap;

public class Recipe
{
	public int id;
	public String name;
	public int yield;
	public String instructions;
	public float costPerRecipe;
	public float costPerServing;
	public String source;
	public String notes;
	
	public Recipe(int id, String name)
	{
		this.id = id;
		this.name = name;
		recipes.put(name, this);
	}
	
	public static HashMap<String, Recipe> recipes = new HashMap<String, Recipe>();
}