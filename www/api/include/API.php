<?php

abstract class API
{
	public $db;

	public abstract function call($data);

	public function validateExists($array, $key)
	{
		if (!isset($array[$key]))
			throw new Exception("Input failed to validate: No such key ".$key);
	}

	public function validateAuthToken($token)
	{
		$result = $this->db->execute("SELECT uid FROM user_auth_tokens WHERE `token` = '%s'", $token);
		if (!$result->ok() || $result->numRows() == 0)
			throw new Exception("Invalid auth token");
		$row = $result->fetch();
		return $row['uid'];
	}
}

?>
