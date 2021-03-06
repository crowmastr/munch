<?php
class register extends API
{
	/**
	 * Storing new user
	 * returns user details
	 */
	private function storeUser($name, $email, $password)
	{
		$uuid = uniqid('', true);

		$hash = Util::hashSSHA($password);
		$encrypted_password = $hash["encrypted"]; // encrypted password
		$salt = $hash["salt"]; // salt

		$result = $this->db->execute("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES('%s', '%s', '%s', '%s', '%s', NOW())",
			$uuid, $name, $email, $encrypted_password, $salt);

		// check for successful store
		if (!$result->ok())
			return false;

		// get user details
		$uid = $result->getLastId(); // last inserted id

		$result = $this->db->execute("SELECT * FROM users WHERE uid = '%d'", $uid);
		return $result->fetch();
	}

	/**
	 * Check if the email exists or not
	 */
	public function doesEmailExist($email)
	{
		$result = $this->db->execute("SELECT email from users WHERE email = '%s'", $email);
		return $result->numRows() > 0;
	}

	public function call($data)
	{
		// Request type is Register new user
		$name = $data['name'];
		$email = $data['email'];
		$password = $data['password'];

		$this->validateExists($data, "name");
		$this->validateExists($data, "email");
		$this->validateExists($data, "password");

		// check if user is already exists
		if ($this->doesEmailExist($email))
		{
			// user is already exists - error response
			throw new Exception("User already exists");
		}

		// store user
		$user = $this->storeUser($name, $email, $password);
		if ($user)
		{
			// user stored successfully
			$token = Util::createAuthToken($this, $user["uid"], $user["unique_id"]);

			$response["uid"] = $user["unique_id"];
			$response["token"] = $token;
			$response["user"]["name"] = $user["name"];
			$response["user"]["email"] = $user["email"];
			$response["user"]["created_at"] = $user["created_at"];
			$response["user"]["updated_at"] = $user["updated_at"];
			return $response;
		}

		throw new Exception("Error occurred in registration");
	}
}
?>