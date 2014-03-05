<?php
class Util
{
	/**
	 * Encrypting password
	 * @param password
	 * returns salt and encrypted password
	 */
	public static function hashSSHA($password)
	{
		$salt = sha1(rand());
		$salt = substr($salt, 0, 10);
		$encrypted = base64_encode(sha1($password . $salt, true) . $salt);
		return array("salt" => $salt, "encrypted" => $encrypted);
	}

	/**
	 * Decrypting password
	 * @param salt, password
	 * returns hash string
	 */
	public static function checkhashSSHA($salt, $password)
	{
		return base64_encode(sha1($password . $salt, true) . $salt);
	}


	public static function createAuthToken($id, $uid)
	{
		$token = sha1($id.$uid.rand());
		$this->db->execute("INSERT INTO user_auth_tokens (uid, token, created) VALUES('%s', '%s', %d)", $uid, $token, time());
		return $token;
	}
}
?>