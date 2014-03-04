<?php
class ilist extends API
{
	private function validIngredient($id)
	{
		$res = $this->db->execute("SELECT * FROM `ingredients` WHERE `id` = %d", $id);
		if (!$res->ok() || $res->numRows() == 0)
			throw new Exception("Invalid ingredient id: " + $id);
	}

	private function listHasIngredient($list, $uid, $ingredId)
	{
		$res = $this->db->execute("SELECT * FROM `user_ingredient_list` WHERE `type` = '%s' AND `user` = '%s' AND `ingredient` = '%d'",
			$list, $uid, $ingredId);
		return $res->numRows() > 0;
	}

	private function addIngredient($list, $uid, $ingredId)
	{
		$res = $this->db->execute("INSERT INTO `user_ingredient_list` (`type`, `user`, `ingredient`, `created`) VALUES('%s', '%s', '%s', %d)",
			$list, $uid, $ingredId, time());
		$id = $res->getLastId();
		if ($id <= 0)
			throw new Exception("Invalid last id while inserting ingredient");
		return $id;
	}

	private function removeIngredient($list, $uid, $ingredUID)
	{
		$res = $this->db->execute("DELETE FROM `user_ingredient_list` WHERE `type` = '%s' AND `user` = '%s' AND `id` = %d",
			$list, $uid, $ingredUID);
		if ($res->affectedRows() <= 0)
			throw new Exception("Unable to remove ingredient from list, it does not exist");
	}

	public function call($data)
	{
		$this->validateExists($data, 'auth');
		$this->validateExists($data, 'list');
		$this->validateExists($data, 'op');

		$uid = $this->validateAuthToken($data['auth']);

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
		else
			throw new Exception("Unsupported operation");
	}
}
?>