<?php

if($_SERVER['REQUEST_METHOD'] == 'POST'){

$rno = $_POST['rno'];

require_once 'connect.php';

$sql = "SELECT likeno FROM replies_table WHERE rno = '$rno'";

$response = mysqli_query($conn, $sql);

$result = array();
$result['read'] = array();

if(mysqli_num_rows($response) === 1){

  if($row = mysqli_fetch_assoc($response)) {

    $h['likeno'] = $row['likeno'];

    array_push($result["read"], $h);

    $result["success"] = "1";
    echo json_encode($result);
    mysqli_close($conn);
  }

}

}else {

    $result["success"] = "0";
    $result["message"] = "Error!";
    echo json_encode($result);

    mysqli_close($conn);

}
?>
