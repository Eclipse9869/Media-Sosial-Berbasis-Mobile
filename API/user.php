<?php

require('config.php');

$request = $_GET;
if ($request['action'] == 'view') {
    $sql = "SELECT * FROM users WHERE user_id = '" . $request['user_id'] . "'";
    $result = populate($mysqli->query($sql))[0];
    showSuccess($result);
} elseif ($request['action'] == 'edit') {
    $data = file_get_contents('php://input');
    $json_data = json_decode($data, true);

    if (empty($json_data['user_id'])) {
        showError("user not valid");
        return;
    }

    mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
    $mysqli->begin_transaction();

    try {
        $sql = "UPDATE users 
                    SET 
                        first_name = '" . cleanString($json_data['first_name']) . "', 
                        last_name = '" . cleanString($json_data['last_name']) . "',
                        privacy_setting = '" . cleanString($json_data['privacy_setting']) . "'
                WHERE
                    user_id = '" . $json_data['user_id'] . "'";

        $mysqli->query($sql);
        $mysqli->commit();

        $user = $mysqli->query("SELECT * FROM users WHERE user_id = '" . $json_data['user_id'] . "'");
        $userResult = populate($user)[0];
        showSuccess($userResult);
    } catch (\Exception $e) {
        $mysqli->rollback();
        writeLogError($e);
        showError($e->getMessage());
    }
}
