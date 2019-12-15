<?php

if($_SERVER['REQUEST_METHOD'] =='POST'){

  $movie_id = $_POST['movie_id'];
  $tagger = $_POST['tagger'];
  $tag_text = $_POST['tag_text'];
  $id = $_POST['id'];

  require_once 'connect.php';

  $sql = "SELECT tag_text FROM tags_table WHERE tag_text = '$tag_text' AND movie_ID = '$movie_id'";

  $response = mysqli_query($conn, $sql);

  if(mysqli_num_rows($response) != 0){

    $result["success"] = "2"; // 이미 태그가 있어요~
    $result["message"] = "success";

    echo json_encode($result);
    mysqli_close($conn);

  }else{

  $sql = "INSERT INTO tags_table (movie_id, tagger, tag_text) values ('$movie_id', '$tagger', '$tag_text')";

  if(mysqli_query($conn, $sql) ){

    $tno = mysqli_insert_id($conn);

    $sql = "INSERT INTO tlike_table(tno, id) values ('$tno', '$id')";

    if(mysqli_query($conn, $sql) ){
      $result["success"] = "1"; // 태그 등록, 좋아요 디비 완료
      $result["message"] = "success";

      echo json_encode($result);
      mysqli_close($conn);
    } else {
      $result["success"] = "3"; // tno 못받아옴
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
