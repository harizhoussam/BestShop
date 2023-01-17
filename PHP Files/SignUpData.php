<?php
   $con=mysqli_connect("localhost","root","houssam","firstdatabase");
   if (mysqli_connect_errno($con)) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
   }
	
   $username = $_POST['username'];
   $password = $_POST['password'];
   $fullname = $_POST['fullname'];
   
   $hashedPass= password_hash($password, PASSWORD_DEFAULT);

	if (!empty($_POST['fullname'])  && !empty($_POST['username']) && !empty($_POST['password']))
	{
		$checkUniqueInUsers = mysqli_query($con,"SELECT * FROM users where username='$username'");
    $checkUniqueInMarkets = mysqli_query($con,"SELECT * FROM markets where username='$username'");
	
    if (mysqli_num_rows($checkUniqueInUsers) != 0 || mysqli_num_rows($checkUniqueInMarkets) != 0) {
				echo "Username taken, please choose new one"; }
			else{
				
			   $result = mysqli_query($con,"INSERT INTO users (fullname, username, password) VALUES 
			   ('$fullname','$username','$hashedPass')");
			   
				if ($result) {
						echo "SignUp Successful";
				} else echo "SignUp Failed, Try Again Later";
					
				} 
	}else echo "Please Enter All Information";
		
   mysqli_close($con);
   
?>




  