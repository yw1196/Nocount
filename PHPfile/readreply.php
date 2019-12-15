<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST'){

  $movie_id = $_POST['movie_id'];

  require_once 'connect.php';

  $sql = "SELECT * FROM replies_table WHERE movie_id='$movie_id' ";

  $response = mysqli_query($conn, $sql);

  $result = array();
  $result['read'] = array();


      while($row = mysqli_fetch_assoc($response)) {

          $h['name']  = $row['name'];
          $h['email'] = $row['email'];

          array_push($result["read"], $h); // 배열 result['read']에 $h값을 넣어줌

          $result["success"] = "1";
          echo json_encode($result);
          mysqli_close($conn);

      }


}else {

    $result["success"] = "0";
    $result["message"] = "Error!";
    echo json_encode($result);
    mysqli_close($conn);

}



 ?>
