



<?php

$host = 'localhost';
$user='root';
$pwd = 'houssam';
$db = 'firstdatabase';

$conn = mysqli_connect($host, $user, $pwd, $db);

if(!$conn) {
	echo("ERROR in Connection: " . $mysqli_connect_error());
}
$response= array();
$sql_query= "select * from posts";
$result = mysqli_query($conn, $sql_query);

if(mysqli_num_rows($result) > 0){
	
	//$response['success']=1;
	$posts = array();
	while ($row = mysqli_fetch_assoc($result)){
		array_push($posts, $row);
	}
	//$row = mysqli_fetch_array($result)
	$response['post']= $posts;
	//$response['post']= $row;
}
	else {
		$response['success']= 0;
		$response['message']= 'No Data';
	}

echo json_encode($response);
mysqli_close($conn);



?>
