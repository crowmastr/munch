<?php
class login extends API
{
	/**
	 * Get user by email and password
	 */
	private function getUserByEmailAndPassword($email, $password)
	{
		$result = $this->db->execute("SELECT * FROM users WHERE email = '%s'", $email);
		if ($result->numRows() == 0)
			// user not found
			return false;

		$result = $result->fetch();

		$salt = $result['salt'];
		$encrypted_password = $result['encrypted_password'];
		$hash = Util::checkhashSSHA($salt, $password);
		// check for password equality
		if ($encrypted_password == $hash)
			// user authentication details are correct
			return $result;

		return false;
	}

	private function createAuthToken($id, $uid)
	{
		$token = sha1($id.$uid.rand());
		$this->db->execute("INSERT INTO user_auth_tokens (uid, token, created) VALUES('%s', '%s', %d)", $uid, $token, time());
		return $token;
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

			// insert auth token
			$token = $this->createAuthToken($user["uid"], $user["unique_id"]);

			$response["uid"] = $user["unique_id"];
			$response["token"] = $token;
			$response["user"]["name"] = $user["name"];
			$response["user"]["email"] = $user["email"];
			$response["user"]["created_at"] = $user["created_at"];
			$response["user"]["updated_at"] = $user["updated_at"];
			return $response;
		}
		else
		{
			throw new Exception("Incorrect email or password!");
		}
	}
}
?>