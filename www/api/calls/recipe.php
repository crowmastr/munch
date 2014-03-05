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

			$result = $this->db->execute("SELECT * FROM recipe_ingredients WHERE `recipe` = %d", $id);
			if (!$result->ok() || $result->numRows() == 0)
				throw new Exception("Invalid recipe id");

			$resp = array();
			while (($row = $result->fetch()))
				$resp[] = $row;
			return $resp;
		}
		else
			throw new Exception("Unsupported op " . $op);
	}
}
?>