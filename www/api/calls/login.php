<?php
class login extends API
{
	/**
	 * Get user by email and password
	 */
	public function getUserByEmailAndPassword($email, $password)
	{
		$result = $this->db->execute("SELECT * FROM users WHERE email = '%s'", $email);
		if ($result->numRows() == 0)
			// user not found
			return false;

		$result = $result->fetch();

		//$result = mysql_query("SELECT * FROM users WHERE email = '$email'") or die(mysql_error());
		// check for result

		$salt = $result['salt'];
		$encrypted_password = $result['encrypted_password'];
		$hash = Util::checkhashSSHA($salt, $password);
		// check for password equality
		if ($encrypted_password == $hash)
			// user authentication details are correct
			return $result;

		return false;
	}

	public function call($data)
	{
		// Request type is check Login
		$email = $_POST['email'];
		$password = $_POST['password'];

		$this->validateExists($data, "email");
		$this->validateExists($data, "password");

		// check for user
		$user = $this->getUserByEmailAndPassword($email, $password);
		if ($user != false)
		{
			// user found
			// echo json with success = 1
			$response["success"] = 1;
			$response["uid"] = $user["unique_id"];
			$response["user"]["name"] = $user["name"];
			$response["user"]["email"] = $user["email"];
			$response["user"]["created_at"] = $user["created_at"];
			$response["user"]["updated_at"] = $user["updated_at"];
			return $response;
		}
		else
		{
			// user not found
			// echo json with error = 1
			$response["error"] = 1;
			$response["error_msg"] = "Incorrect email or password!";
			return $response;
		}
	}
}
?>