<?php
$con=mysqli_connect("localhost","root","houssam","firstdatabase");
   if (mysqli_connect_errno($con)) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
   }
   $username = $_POST['username'];
 
 $likes = array();
   
   $result = mysqli_query($con,"SELECT post_id FROM market_likes where username = '$username' ") or die( mysqli_error($con));
if(mysqli_num_rows($result) > 0){
	
	
	while ($row = mysqli_fetch_assoc($result)){
		array_push($likes, $row);
	}


	
	echo json_encode (array('likes' => $likes));
}
else {
array_push($likes, (object)['post_id' => '0']);
echo json_encode (array('likes' => $likes));
}

mysqli_close($con);
	
   
?>