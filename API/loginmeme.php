<?php
require('config.php');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = file_get_contents('php://input');
    $json_data = json_decode($data, true);

    if(empty($json_data['username']) || empty($json_data['password'])){
        showError("Username or Password Is Required");
        return;
    }

    $sql = "SELECT * FROM users where username = '" . $json_data['username'] . "' and user_pass = '" . $json_data['password'] . "'";
    $result = populate($mysqli->query($sql));

    if (count($result) == 0) {
        showError("Check Your Username or Password Instead");
    } else {
        showSuccess($result[0]);
    }
} else {
    showError("Method Not Allowed");
}
