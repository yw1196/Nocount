<?php

if($_SERVER['REQUEST_METHOD'] == 'POST'){

  $movie_id = $_POST['movie_id'];

  require_once 'connect.php';

  $sql = "SELECT tno, tagger, tag_text, tag_like FROM tags_table WHERE movie_ID = '$movie_id' ORDER BY tag_like DESC";

  $response = mysqli_query($conn, $sql);

  $result = array();
  $result['read'] = array();

  if($response){

    while($row = mysqli_fetch_assoc($response)){

      $h['tno'] = $row['tno'];
      $h['tagger']  = $row['tagger'];
      $h['tag_text'] = $row['tag_text'];
      $h['tag_like'] = $row['tag_like'];
      array_push($result["read"], $h);


    }

    $result["success"] = "1";
    echo json_encode($result);


  }else{

    $result["success"] = "0";
    $result["message"] = "error";
    echo json_encode($result);
    mysqli_close($conn);

  }



}else{

  $result["success"] = "0";
  $result["message"] = "Error!";
  echo json_encode($result);
  mysqli_close($conn);


}



 ?>
