<?php
header("Access-Control-Allow-Origin: *");
header('Content-Type: application/json; charset=utf-8');

date_default_timezone_set("Asia/Bangkok");

$hostmysql = "localhost";
$username = "native_160719028";
$password = "ubaya";
$database = "native_160719028";

$mysqli = new mysqli($hostmysql, $username, $password, $database);

if (mysqli_connect_errno()) {
    $out = json_encode(array(
        'status' => 'error',
        'message' => mysqli_connect_error()
    ));
    die($out);
}

function out($out) {
    echo $out;
}

function tojson($args) {
    return json_encode($args);
}

function outjson($status, $message, $data) {
    $out = [];
    $out = [
        'status' => $status,
        'message' => $message,
        'data' => $data
    ];
    out(tojson($out));
}

function get($args) {
    if (isset($_POST[$args])) {
        $arg = $_POST[$args];
    } elseif (isset($_GET[$args])) {
        $arg = $_GET[$args];
    } else {
        $arg = '';
    }
    return $arg;
}

function populate($result) {
    $rows = array();
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            array_push($rows, json_decode(json_encode($row), true));
        }
    }
    return $rows;
}

function showSuccess($data, $message = '') {
    http_response_code(200);
    outjson(200, (!empty($message) ? $message : 'Successfully.'), $data);
}

function showError($message) {
    http_response_code(500);
    outjson(500, $message, []);
}

function writeLog($log_msg) {
    $log_filename = $_SERVER['DOCUMENT_ROOT'] . "/log";
    if (!file_exists($log_filename)) {
        mkdir($log_filename, 0777, true);
    }
    $log_file_data = $log_filename . '/log_' . date('d-m-Y') . '.log';

    if (is_array($log_msg)) {
        $log_msg = json_encode($log_msg);
    }

    $log_msg = date('Y-m-d H:i:s') . ' >>> ' . $log_msg;
    file_put_contents($log_file_data, $log_msg . "\n", FILE_APPEND);
}

function writeLogError(\Exception $exception) {
    $log_filename = $_SERVER['DOCUMENT_ROOT'] . "/log";
    if (!file_exists($log_filename)) {
        mkdir($log_filename, 0777, true);
    }
    $log_file_data = $log_filename . '/error_log_' . date('d-m-Y') . '.log';

    $date = date('Y-m-d H:i:s');
    $log_msg = $date . ' Error File >>> ' . $exception->getFile() . "\n";
    $log_msg .= str_pad('', strlen($date), " ", STR_PAD_RIGHT) . ' Error Line >>> ' . $exception->getLine() . "\n";
    $log_msg .= str_pad('', strlen($date), " ", STR_PAD_RIGHT) . ' Error Message >>> ' . $exception->getMessage() . "\n";
    $log_msg .= str_pad('', strlen($date), " ", STR_PAD_RIGHT) . ' Stacktrace >>> ' . $exception . "\n";

    file_put_contents($log_file_data, $log_msg . "\n", FILE_APPEND);
}

function cleanString($value){
    return trim($value, '"');
}
