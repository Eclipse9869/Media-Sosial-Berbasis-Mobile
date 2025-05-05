<?php

require('config.php');

$request = $_GET;
if ($request['action'] == 'memes') {
    $sql = "SELECT 
                *, 
                (SELECT COUNT(*) FROM meme_comments WHERE meme_comments.id_meme = memes.id_meme) AS total_comment,
                (SELECT COUNT(*) FROM memes_like WHERE memes_like.id_meme = memes.id_meme AND memes_like.user_id = '".$request['user_id']."') AS is_like  
            FROM 
                memes 
            ORDER BY 
                id_meme DESC";
    $result = populate($mysqli->query($sql));
    showSuccess($result);
} elseif ($request['action'] == 'comments') {
    $sql = "SELECT 
                m1.*,
                CASE 
                    WHEN (m3.first_name IS NULL OR m3.first_name = '') AND (m3.last_name IS NULL OR m3.last_name = '') THEN
                        'No Name'
                    ELSE
                        CASE 
                            WHEN m3.privacy_setting = 1 THEN 
                                CONCAT(LEFT(m3.first_name, 3), REPEAT('*', LENGTH(m3.first_name) - 4), REPEAT('*', LENGTH(m3.last_name)))
                            ELSE
                                CONCAT(m3.first_name, ' ', m3.last_name)
                        END
                END AS full_name 
            FROM 
                meme_comments m1 
                INNER JOIN memes m2 ON m1.id_meme = m2.id_meme 
                INNER JOIN users m3 ON m1.user_id = m3.user_id 
            WHERE 
                m1.id_meme = '" . $request['id_meme'] . "' 
            ORDER BY 
                m1.comment_id DESC";

    $result = populate($mysqli->query($sql));
    showSuccess($result);
} elseif ($request['action'] == 'postComments') {
    $data = file_get_contents('php://input');
    $json_data = json_decode($data, true);

    if (empty($json_data['id_meme']) || empty($json_data['user_id']) || empty($json_data['comment'])) {
        showError("Comment Not Valid");
        return;
    }

    mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
    $mysqli->begin_transaction();

    try {
        $sql = "INSERT INTO meme_comments (comment, comment_date, user_id, id_meme) ";
        $values = "VALUES (
            '" . cleanString($json_data['comment']) . "',
            NOW(),
            '" . $json_data['user_id'] . "',
            '" . $json_data['id_meme'] . "'
        )";

        $mysqli->query($sql . $values);
        $user_id = $mysqli->insert_id;

        $mysqli->commit();

        showSuccess([], 'Comment Posted!');
    } catch (\Exception $e) {
        $mysqli->rollback();
        writeLogError($e);
        showError($e->getMessage());
    }
} elseif ($request['action'] == 'postMemes') {
    $data = file_get_contents('php://input');
    $json_data = json_decode($data, true);

    if (empty($json_data['url_meme']) || empty($json_data['teks_atas']) || empty($json_data['user_id'])) {
        showError("Memes Not Valid");
        return;
    }

    mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
    $mysqli->begin_transaction();

    try {
        $sql = "INSERT INTO memes (url_meme, teks_atas, teks_bawah, user_id, created_date) ";
        $values = "VALUES (
            '" . cleanString($json_data['url_meme']) . "',
            '" . cleanString($json_data['teks_atas']) . "',
            '" . cleanString($json_data['teks_bawah']) . "',
            '" . cleanString($json_data['user_id']) . "',
            NOW()
        )";

        $mysqli->query($sql . $values);
        $user_id = $mysqli->insert_id;

        $mysqli->commit();

        showSuccess([], 'Memes Posted!');
    } catch (\Exception $e) {
        $mysqli->rollback();
        writeLogError($e);
        showError($e->getMessage());
    }
} elseif ($request['action'] == 'my') {
    $sql = "SELECT 
                *, 
                (SELECT COUNT(*) FROM meme_comments WHERE id_meme = memes.id_meme) AS total_comment 
            FROM 
                memes 
            WHERE 
                user_id = '" . $request['user_id'] . "' 
            ORDER BY 
                id_meme DESC";
    $result = populate($mysqli->query($sql));
    showSuccess($result);
} elseif ($request['action'] == 'like') {
    $data = file_get_contents('php://input');
    $json_data = json_decode($data, true);

    if (empty($json_data['id_meme']) || empty($json_data['user_id'])) {
        showError("Like not valid");
        return;
    }

    mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
    $mysqli->begin_transaction();

    try {
        $sql = "INSERT INTO memes_like (user_id, id_meme) ";
        $values = "VALUES (
            '" . $json_data['user_id'] . "',
            '" . $json_data['id_meme'] . "'
        )";

        $mysqli->query($sql . $values);
        $mysqli->query("UPDATE memes SET jumlah_like = (jumlah_like + 1) WHERE id_meme = '" . $json_data['id_meme'] . "'");

        $mysqli->commit();

        showSuccess([], 'Post Liked!');
    } catch (\Exception $e) {
        $mysqli->rollback();
        writeLogError($e);
        showError($e->getMessage());
    }
}
