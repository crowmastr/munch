<?php
/**
 * index.php
 *
 * Entry point for all API requests. API requests are executed by sending
 * HTTP POST data with the value 'tag' set to the API call desired.
 * Additionally, the caller may send a 'cookie' value which we will
 * return to them in the response exactly as we received it.
 *
 * 3/28/14
 *
 * Adam Treharne
 */

 // 'tag' is the API procedure being called, so it should exist
if (!isset($_POST['tag']) || empty($_POST['tag']))
	die;

$tag = $_POST['tag'];

// Security check to be sure it contains only lowercase letters
if (preg_match("/^[a-z]+$/", $tag) === false)
	die;

require("include/API.php");
require("include/Database.php");
require("include/Util.php");

// Include the correct module for this API call or die if it does not exist
require("calls/".$tag.".php");
$c = new $tag();
$c->db = Database::get();

// This is the result that will be returned
$res = array();
$res["tag"] = $tag;

// 'cookie' is a special value the sender will set and we will send it back as-is
if (isset($_POST['cookie']))
	$res["cookie"] = $_POST['cookie'];

try
{
	/* make the actual API call
	 *
	 * the returns the data to be caller on success, and throws and
	 * exception on error with the reason for the exception
	 */
	$r = $c->call($_POST);

	// We succeed because call did not throw, so mark the response as good
	$res["success"] = 1;

	// Merge r into res, giving res precedence for duplicate keys
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