<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST'){

  $name = $_POST['name'];
  require_once 'connect.php';

  $sql = "SELECT * FROM users_table WHERE name ='$name' ";

  $response = mysqli_query($conn, $sql);

  if(mysqli_num_rows($response) == 0){
    $result["success"] = "1";
    $result["message"] = "success";

    echo json_encode($result);
    mysqli_close($conn);
  }else{
    $result["success"] = "0";
    $result["message"] = "error";

    echo json_encode($result);
    mysqli_close($conn);
  }

}

 ?>
