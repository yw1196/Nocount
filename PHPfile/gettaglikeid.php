<?php

if($_SERVER['REQUEST_METHOD'] == 'POST'){

  $tno = $_POST['tno'];

  require_once 'connect.php';

  $sql = "SELECT id FROM tlike_table WHERE tno = '$tno'";

  $response = mysqli_query($conn, $sql);

  $result = array();
  $result['read'] = array();

  if($response){

    while($row = mysqli_fetch_assoc($response)){

      $h['id'] = $row['id'];

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

}else{

  $result["success"] = "0";
  $result["message"] = "Error!";
  echo json_encode($result);
  mysqli_close($conn);


}

?>
