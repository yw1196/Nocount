<?php

if($_SERVER['REQUEST_METHOD'] =='POST'){

  $movie_id = $_POST['movie_id'];
  $replyer = $_POST['replyer'];
  $reply = $_POST['reply'];
  $rating = $_POST['rating'];

  require_once 'connect.php';

  $sql = "INSERT INTO replies_table (movie_id, replyer, reply, rating) values ('$movie_id', '$replyer', '$reply', '$rating')";

  if(mysqli_query($conn, $sql) ){
    $result["success"] = "1";
    $result["message"] = "success";

    echo json_encode($result);
    mysqli_close($conn);
  } else {
    $result["success"] = "0";
    $result["message"] = "error";

    echo json_encode($result);
    mysqli_close($conn);
  }
}

 ?>
