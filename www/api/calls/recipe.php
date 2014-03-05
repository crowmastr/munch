<?php
class recipe extends API
{
	public function call($data)
	{
		$this->validateExists($data, 'op');

		$op = $data['op'];
		if ($op == 'list')
		{
			$this->validateExists($data, 'ts');
			$ts = $data['ts'];

			$result = $this->db->execute("SELECT * FROM recipes WHERE `last_modified` > %d", $ts);
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