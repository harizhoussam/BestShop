<?php
   $con=mysqli_connect("localhost","root","houssam","firstdatabase");
   if (mysqli_connect_errno($con)) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
   }
   $longitude = $_POST['longitude'];
   $latitude = $_POST['latitude'];
  

   //$result = mysqli_query($con,"SELECT * FROM posts ORDER BY id ASC") or die( mysqli_error($con));
   //$result = mysqli_query($con,"SELECT * FROM posts ORDER BY ABS(longitude + latitude -$longitude - $latitude) ASC") or die( mysqli_error($con));
   $result = mysqli_query($con,"SELECT * FROM marketposts ORDER BY 
   SQRT(power($longitude - longitude,2) + power($latitude - latitude,2)) ASC" )or die( mysqli_error($con));


if(mysqli_num_rows($result) > 0){
	
	$posts = array();
	while ($row = mysqli_fetch_assoc($result)){
		array_push($posts, $row);
	}
	echo json_encode (array('posts' => $posts));
}
 
    
   mysqli_close($con);
   
?>