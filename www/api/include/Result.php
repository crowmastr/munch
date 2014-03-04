<?php
class Result
{
	private $db;
	private $res;
	private $id;

	public function __construct($d, $r)
	{
		$this->db = $d;
		$this->res = $r;
		$this->id = mysql_insert_id($d->con);
	}

	public function ok()
	{
		return $this->res;
	}

	public function getLastId()
	{
		return $this->id;
	}

	public function numRows()
	{
		return mysql_num_rows($this->res);
	}

	public function affectedRows()
	{
		return mysql_affected_rows($this->db->con);
	}

	public function fetch()
	{
		return mysql_fetch_array($this->res);
	}
}
?>