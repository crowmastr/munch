<?php

require_once('config.php');
require('Result.php');

class Database
{
	public $con;

	public function __construct()
	{
		// connecting to mysql
		$this->con = mysql_connect(DB_HOST, DB_USER, DB_PASSWORD);
		// selecting database
		mysql_select_db(DB_DATABASE, $this->con);
	}

	public function __destruct()
	{
		mysql_close($this->con);
	}

	public function execute($query)
	{
		$args = func_get_args();
		$escapedArgs = array();
		for ($i = 1; $i < sizeof($args); $i++)
			$escapedArgs[] = mysql_real_escape_string($args[$i], $this->con);
		$query = vsprintf($query, $escapedArgs);
		$res = mysql_query($query, $this->con);// or die mysql_error($this->con);
		return new Result($this, $res);
	}

	private static $db;

	public static function get()
	{
		if (self::$db == null)
			self::$db = new Database();
		return self::$db;
	}
}

?>