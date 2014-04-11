package com.angtft.munch.library;

import java.util.HashMap;

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
	
}