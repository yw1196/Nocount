<?php

  $movie_id = '136873';

  require_once 'connect.php';

  $sql = "SELECT rno, replyer, reply, rating, likeno, regdate FROM replies_table WHERE movie_ID = '$movie_id' ORDER BY likeno DESC";

  $response = mysqli_query($conn, $sql);

  $result = array();
  $result['read'] = array();

  if($response){

    while($row = mysqli_fetch_assoc($response)){

      $h['rno'] = $row['rno'];
      $h['replyer']  = $row['replyer'];
      $h['reply'] = $row['reply'];
      $h['rating'] = $row['rating'];
      $h['likeno'] = $row['likeno'];
      $h['regdate'] = $row['regdate'];

      array_push($result["read"], $h);


    }

    $result["success"] = "1";
    echo json_encode($result);
    mysqli_close($conn);

  }else{

    $result["success"] = "0";
    $result["message"] = "error";
    echo json_encode($result);
    mysqli_close($conn);

  }







 ?>
