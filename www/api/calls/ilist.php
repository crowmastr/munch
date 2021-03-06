<?php
/*
 * ilist.php
 *
 * This class handles the API calls for managing a list of ingredients.
 * The fields 'auth', 'list', and 'op' must be provided. 'auth' is the
 * authentication taken generated by the 'login' or 'register' command.
 * 'list' is the ingredient list to modify, which should either be
 * 'shopping' or 'inventory'. op should be 'add', 'del', or 'list'
 *
 * 3/28/14
 *
 * Adam Treharne
 */
 
class ilist extends API
{
	/*
	 * check if a given ingredient id is valid
	 * @param id the ingredient id
	 * @throws Exception if the ingredient is not valid
	 */
	private function validIngredient($id)
	{
		$res = $this->db->execute("SELECT * FROM `ingredients` WHERE `id` = %d", $id);
		if (!$res->ok() || $res->numRows() == 0)
			throw new Exception("Invalid ingredient id: " + $id);
	}

	/* check if the given list has a given ingredient
	 * @param list the list name to check
	 * @param uid the uid of the user
	 * @param ingredId the id of the ingredient
	 * @return true if the ingredient is on the list, else false
	 */
	private function listHasIngredient($list, $uid, $ingredId)
	{
		$res = $this->db->execute("SELECT * FROM `user_ingredient_list` WHERE `type` = '%s' AND `user` = '%s' AND `ingredient` = '%d'",
			$list, $uid, $ingredId);
		return $res->numRows() > 0;
	}

	/* add an ingredient to the given list
	 * @param list the list name to check
	 * @param uid the uid of the user
	 * @param ingredId the id of the ingredient to add
	 * @throws Exception if unable to insert ingredient, such as a db constraint does not hold
	 * @return the unique id for the list,uid,ingredient pair
	 */
	private function addIngredient($list, $uid, $ingredId)
	{
		$res = $this->db->execute("INSERT INTO `user_ingredient_list` (`type`, `user`, `ingredient`, `created`) VALUES('%s', '%s', '%s', %d)",
			$list, $uid, $ingredId, time());
		$id = $res->getLastId();
		if ($id <= 0)
			throw new Exception("Invalid last id while inserting ingredient");
		return $id;
	}

	/* remove an ingredient from the given list
	 * @param list The list to remove the ingredient from
	 * @param uid The uid of the user
	 * @param ingredUID The UID of the list,uid,ingredient pair to remove
	 */
	private function removeIngredient($list, $uid, $ingredUID)
	{
		$res = $this->db->execute("DELETE FROM `user_ingredient_list` WHERE `type` = '%s' AND `user` = '%s' AND `id` = %d",
			$list, $uid, $ingredUID);
		if ($res->affectedRows() <= 0)
			throw new Exception("Unable to remove ingredient from list, it does not exist");
	}

	/* the actual API call */
	public function call($data)
	{
		$this->validateExists($data, 'auth');
		$this->validateExists($data, 'list');
		$this->validateExists($data, 'op');

		// Validate the auth token given to be sure this user is logged in, and retrieve their uid
		$uid = $this->validateAuthToken($data['auth']);

		// We expect list to be either 'shopping' or 'inventory'
		$l = $data['list'];
		if ($l != "shopping" && $l != "inventory")
			throw new Exception("Invalid list");

		$op = $data['op'];
		if ($op == "add")
		{
			$this->validateExists($data, 'id');

			$id = $data['id']; // ingredient id being added
			$this->validIngredient($id);

			if ($this->listHasIngredient($l, $uid, $id))
				throw new Exception("Ingredient already exists");

			$ingredId = $this->addIngredient($l, $uid, $id); // This returns a unique id for this entry in the given list.
															 // Which is used later for removal.

			$resp["ingredient_id"] = $ingredId;
			return $resp;
		}
		else if ($op == "del")
		{
			$this->validateExists($data, 'id');

			$id = $data['id']; // unique id in the list being deleted

			$this->removeIngredient($l, $uid, $id);
			return array();
		}
		else if ($op == "list")
		{
			$res = $this->db->execute("SELECT `id`,`ingredient`,`created` FROM `user_ingredient_list` WHERE `type` = '%s' AND `user` = '%s'",
				$l, $uid);
			// this returns an array of arrays, each with an id, ingredient, and created fields
			$response = array();
			$num = 0;
			while (($row = $res->fetch()))
			{
				$num++;
				$response[] = array("id" => $row['id'], "ingredient" => $row['ingredient'], "created" => $row['created']);
			}
			$response["num"] = $num;
			return $response;
		}
		else
			throw new Exception("Unsupported operation");
	}
}
?>