<?php
   $con=mysqli_connect("localhost","root","houssam","firstdatabase");
   if (mysqli_connect_errno($con)) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
   }
	
   $username = $_POST['username'];
   $password = $_POST['password'];
   $result = mysqli_query($con,"SELECT * FROM users where username='$username'");
   
   if (mysqli_num_rows($result) != 0){
   $num = mysqli_num_rows($result);
   
   if($num ==1){
	   $row= mysqli_fetch_assoc($result);
	   if (password_verify($password, $row['password']))
	   echo "True";
	   else echo "Wrong Password";
	   
   }else echo "Error: Multiple Users With This Username Exist";
   
   }
    else echo "This Username Doesn't Exist";
	
   mysqli_close($con);
   
?>




  