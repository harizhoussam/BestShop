<?php
   $con=mysqli_connect("localhost","root","houssam","firstdatabase");
   if (mysqli_connect_errno($con)) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
   }
   
 $ImageData = $_POST['ConvertImage'];
 $PostText = $_POST['text'];
 $Username = $_POST['username'];
 $Longitude=$_POST['longitude'];
 $Latitude=$_POST['latitude'];

   
  $GetOldIdSQL ="SELECT id FROM posts ORDER BY id DESC";
 
 $Query = mysqli_query($con,$GetOldIdSQL);
 
$row = mysqli_fetch_array($Query);

 $DefaultId = $row['id']; 
 $file = "$DefaultId.png";
  
  $result = mysqli_query($con,"INSERT INTO posts (id, username, text, image, longitude, latitude) VALUES ($DefaultId + 1,'$Username','$PostText','$DefaultId.png','$Longitude', '$Latitude')");
   
   if ($result) {
	  
	   file_put_contents("PostsImages/UserPosts/$file",base64_decode($ImageData));
            echo "Posted";
        } else echo "Post Failed, Try Again Later";
  
  
  mysqli_close($con);
   
?>

