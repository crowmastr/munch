<?php
class recipe extends API
{
	public function call($data)
	{
		$this->validateExists($data, 'op');

		$op = $data['op'];
		if ($op == 'list')
		{
			$result = $this->db->execute("SELECT * FROM recipes");
			$resp = array();
			while (($row = $result->fetch()))
				$resp[] = $row;
			return $resp;
		}
		else if ($op == 'ingredients')
		{
			$this->validateExists($data, 'id');
			$id = $data['id'];

			$result = $this->db->execute("SELECT name, recipe_ingredients.amt, recipe_ingredients.info FROM ingredients RIGHT JOIN recipe_ingredients ON ingredients.id=recipe_ingredients.ingredient WHERE `recipe` = %d", $id);
			if (!$result->ok() || $result->numRows() == 0)
				throw new Exception("Invalid recipe id");

			$resp = array();
			while (($row = $result->fetch()))
				$resp[] = $row;
			return $resp;
		}
		else if ($op == 'search')
		{
			$this->validateExists($data, 'ingredients');
			$ingredients = explode(",", $data['ingredients']);
			
			$possible_recipes = array();
			$definite_recipes = array();
			
			foreach ($ingredients as $i)
			{
				$result = $this->db->execute("SELECT recipe FROM recipe_ingredients WHERE `ingredient` = %d", $i);
				
				$recipes = array();
				while (($row = $result->fetch()))
					$recipes[] = $row['recipe'];
					
				// recipes is all possible recipes we can create from this ingredient.
				
				if (empty($possible_recipes))
					$possible_recipes = $recipes;
				else
					$possible_recipes = array_intersect($possible_recipes, $recipes);
			}
			
			// possible_recipes contains recipes that we maybe can make, now lets see if we really have all of the stuff
			foreach ($possible_recipes as $k => $r)
			{
				$result = $this->db->execute("SELECT ingredient FROM recipe_ingredients WHERE `recipe` = %d", $r);
				
				$ok = true;
				while (($row = $result->fetch()))
				{
					$ing = $row['ingredient'];
					
					if (array_search($ing, $ingredients) === false)
					{
						$ok = false;
						break;
					}
				}
				
				if ($ok)
				{
					unset($possible_recipes[$k]);
					$definite_recipes[] = $r;
				}
			}
			
			$res = array();
			$res["possible_recipes"] = $possible_recipes;
			$res["definite_recipes"] = $definite_recipes;
			return $res;
		}
		else
			throw new Exception("Unsupported op " . $op);
	}
}
?>