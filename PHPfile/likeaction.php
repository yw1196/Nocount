<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST'){

  $rno = $_POST['rno'];
  $id = $_POST['id'];

  require_once 'connect.php';

  $sql = "SELECT * FROM like_table WHERE rno ='$rno' AND id = '$id' ";

  $response = mysqli_query($conn, $sql);

  if(mysqli_num_rows($response) == 0){

    $sql = "INSERT INTO like_table (rno, id) values ('$rno', '$id')";

    if(mysqli_query($conn, $sql) ){

      $sql = "UPDATE replies_table SET likeno = likeno + 1 WHERE rno = '$rno'";

      if(mysqli_query($conn, $sql) ){
        $result["success"] = "1"; // 좋아요~
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

    $sql = "UPDATE replies_table SET likeno = likeno - 1 WHERE rno = '$rno'";

    if(mysqli_query($conn, $sql) ){

      $sql = "DELETE FROM like_table where rno = '$rno' and id = '$id'";

      if(mysqli_query($conn, $sql)){

        $result["success"] = "2"; // 좋아요 취소~
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
