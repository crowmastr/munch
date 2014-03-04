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
}

?>
