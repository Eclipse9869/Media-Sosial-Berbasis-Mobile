<?php
require('config.php');

$request = $_GET;

$sql = "SELECT 
            m2.url_image,
            SUM(m1.jumlah_like) AS total_like, 
            CASE 
                WHEN m2.privacy_setting = 1 THEN 
                    CONCAT(LEFT(m2.first_name, 3), REPEAT('*', LENGTH(m2.first_name) - 4), REPEAT('*', LENGTH(m2.last_name)))
                ELSE
                    CONCAT(m2.first_name, ' ', m2.last_name)
            END AS full_name
        FROM 
            memes m1
            INNER JOIN users m2 ON m1.user_id = m2.user_id
        GROUP BY
            m1.user_id
        ORDER BY 
            m1.jumlah_like DESC";
$result = populate($mysqli->query($sql));
if ($result) {
    showSuccess($result);
} else {
    showError($mysqli->error);
}
