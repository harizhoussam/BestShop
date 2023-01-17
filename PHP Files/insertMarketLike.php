<?php
$con=mysqli_connect("localhost","root","houssam","firstdatabase");
   if (mysqli_connect_errno($con)) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
   }
   $username = $_POST['username'];
   $likedPost = $_POST['likedPost'];
   $pos = (int)$likedPost;
  
   
   $result = mysqli_query($con,"SELECT * FROM market_likes where 
   username='$username' and post_id=$pos");
   
  if (mysqli_num_rows($result) == 0) {
   
   $insert = mysqli_query($con,"INSERT INTO market_likes (post_id, username) VALUES 
   ($pos,'$username')")or die( mysqli_error($con));
   
  if ($insert) {
            echo "liked";
        } else echo "Like failed"; 
  } 
  else
  {$delete = mysqli_query($con,"DELETE FROM market_likes WHERE username='$username' and post_id=$pos")or die( mysqli_error($con));
  if ($delete) {
            echo "Deleted";
        } else echo "Delete failed";
  }
   

   mysqli_close($con);
?>