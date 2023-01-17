<?php
   $con=mysqli_connect("localhost","root","houssam","firstdatabase");
   if (mysqli_connect_errno($con)) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
   }
   $longitude = $_POST['longitude'];
   $latitude = $_POST['latitude'];
   $searchText=$_POST['searchText'];
  

   
    $result = mysqli_query($con,"SELECT * FROM posts WHERE text LIKE '%$searchText%' 
	ORDER BY SQRT(power($longitude - longitude,2) + power($latitude - latitude,2)) ASC");


if(mysqli_num_rows($result) > 0){
	
	$posts = array();
	while ($row = mysqli_fetch_assoc($result)){
		array_push($posts, $row);
	}
	echo json_encode (array('posts' => $posts));
}else echo "No Result";
 
    
   mysqli_close($con);
   
?>

