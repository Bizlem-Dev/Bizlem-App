<?php 

$json = array(); 

if(isset($_FILES)){ 
      $json['code'] = 'FAILED'; 
      $filename = md5(microtime()).'.jpg';
      $uploaddir = 'uploads/';
      $uploadfile = $uploaddir . $filename;  


      if (isset($_FILES['file']) && move_uploaded_file($_FILES['file']['tmp_name'], $uploadfile)){
          $msg = "File is valid, and was successfully uploaded.\n";
          $json['code'] = 'SUCCESS';
      } else {
          $msg = "Possible file upload attack!\n";
      } 

      $json['msg'] = $msg; 
      $json['src'] = 'uploads/'.$filename;
       
 }

header('Content-Type: application/json');
echo json_encode($json);
exit;

?>