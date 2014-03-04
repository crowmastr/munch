<?php
/**
 * File to handle all API requests
 *
 * Each request will be identified by TAG
 * Response will be JSON data
 */

if (!isset($_POST['tag']) || empty($_POST['tag']))
	die;

$tag = $_POST['tag'];

if (preg_match("/^[a-z]+$/", $tag) === false)
	die;

require("include/API.php");
require("include/Database.php");
require("include/Util.php");

require("calls/".$tag.".php");
$c = new $tag();
$c->db = Database::get();

try
{
	$res = $c->call($_POST);
	echo json_encode($res);
}
catch (Exception $e)
{
	die($e->getMessage());
}

?>