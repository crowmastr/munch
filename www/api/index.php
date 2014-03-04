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

$res = array();
$res["tag"] = $tag;
if (isset($_POST['cookie']))
	$res["cookie"] = $_POST['cookie'];

try
{
	$r = $c->call($_POST);

	// We succeed
	$res["success"] = 1;

	// Merge r into res
	$r = array_merge($r, $res);

	echo json_encode($r);
}
catch (Exception $e)
{
	$res["error"] = 1;
	$res["error_msg"] = $e->getMessage();

	echo json_encode($res);
}

?>