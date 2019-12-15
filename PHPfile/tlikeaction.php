<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST'){

  $tno = $_POST['tno'];
  $id = $_POST['id'];

  require_once 'connect.php';

  $sql = "SELECT * FROM tlike_table WHERE tno ='$tno' AND id = '$id' ";

  $response = mysqli_query($conn, $sql);

  if(mysqli_num_rows($response) == 0){

    $sql = "INSERT INTO tlike_table (tno, id) values ('$tno', '$id')";

    if(mysqli_query($conn, $sql) ){

      $sql = "UPDATE tags_table SET tag_like = tag_like + 1 WHERE tno = '$tno'";

      if(mysqli_query($conn, $sql) ){
        $result["success"] = "1"; // 공감 ~
        $result["message"] = "success";

        echo json_encode($result);
        mysqli_close($conn);
      } else {
        $result["success"] = "0";
        $result["message"] = "error";

        echo json_encode($result);
        mysqli_close($conn);
      }
    }else{
      $result["success"] = "0";
      $result["message"] = "error";

      echo json_encode($result);
      mysqli_close($conn);
    }


  }else{

    $sql = "UPDATE tags_table SET tag_like = tag_like - 1 WHERE tno = '$tno'";

    if(mysqli_query($conn, $sql) ){

      $sql = "DELETE FROM tlike_table where tno = '$tno' and id = '$id'";

      if(mysqli_query($conn, $sql)){

        $result["success"] = "2"; // 공감 취소~
        $result["message"] = "success";

        echo json_encode($result);
        mysqli_close($conn);

      }else{
        $result["success"] = "0";
        $result["message"] = "error";

        echo json_encode($result);
        mysqli_close($conn);
      }

    } else {
      $result["success"] = "0";
      $result["message"] = "error";

      echo json_encode($result);
      mysqli_close($conn);
    }

  }

}

 ?>
