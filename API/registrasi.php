<?php

require('config.php');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = file_get_contents('php://input');
    $json_data = json_decode($data, true);

    if (empty($json_data['username']) || empty($json_data['password'])) {
        showError("Username or Password Is Required");
        return;
    }

    mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
    $mysqli->begin_transaction();

    try {
        $sql = "INSERT INTO users (username, user_pass, regis_date) ";
        $values = "VALUES ( 
            '" . cleanString($json_data['username']) . "',
            '" . cleanString($json_data['password']) . "',
            NOW()
        )";
        
        $mysqli->query($sql . $values);
        $user_id = $mysqli->insert_id;

        $mysqli->commit();

        $user = $mysqli->query("SELECT * FROM users WHERE user_id = '" . $user_id . "'");
        $userResult = populate($user)[0];
        showSuccess($userResult, 'User Has Been Registered');
    } catch (\Exception $e) {
        $mysqli->rollback();
        writeLogError($e);
        showError($e->getMessage());
    }
} else {
    showError("Method Not Allowed");
}
