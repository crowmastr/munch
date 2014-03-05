<?php
class ingredient extends API
{
	public function call($data)
	{
		$this->validateExists($data, 'op');

		$op = $data['op'];
		if ($op == 'list')
		{
			$result = $this->db->execute("SELECT * FROM ingredients");
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