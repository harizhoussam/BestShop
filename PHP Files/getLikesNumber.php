<?php
$con=mysqli_connect("localhost","root","houssam","firstdatabase");
   if (mysqli_connect_errno($con)) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
   }


/*$result = mysqli_query($con,"SELECT * from likes where post_id = 2");
   
   echo mysqli_num_rows($result);*/
   
  
   $result = mysqli_query($con,"SELECT COUNT(id) as 'count', post_id from likes GROUP BY post_id");
   
   $likesNum = array();
	while ($row = mysqli_fetch_assoc($result)){
		array_push($likesNum, $row);
	}
	
   echo json_encode(array('likesNum' => $likesNum));
   
   

mysqli_close($con);
	
   
?>