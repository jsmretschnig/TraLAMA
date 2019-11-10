<?php

echo 'TraLAMA: ';
echo "\r\n";
echo $_SERVER["REQUEST_METHOD"];
echo "\r\n, ";
echo $_SERVER["CONTENT_TYPE"];
echo "\r\n, ";

if($_SERVER["REQUEST_METHOD"] == "POST" && $_SERVER["CONTENT_TYPE"] == "application/json")
{
    echo "got json post\r\n, ";
    
    //parse json data
    $json = file_get_contents('php://input');
    //print_r($json);
    echo "got json data\r\n, ";
    $dataArr = json_decode($json,true);
    //print_r($dataArr);
    
    //https://stackoverflow.com/questions/36933321/cant-read-json-using-file-get-contents
    switch (json_last_error()) {
        case JSON_ERROR_NONE:
            echo ' - No errors';
        break;
        case JSON_ERROR_DEPTH:
            echo ' - Maximum stack depth exceeded';
        break;
        case JSON_ERROR_STATE_MISMATCH:
            echo ' - Underflow or the modes mismatch';
        break;
        case JSON_ERROR_CTRL_CHAR:
            echo ' - Unexpected control character found';
        break;
        case JSON_ERROR_SYNTAX:
            echo ' - Syntax error, malformed JSON';
        break;
        case JSON_ERROR_UTF8:
            echo ' - Malformed UTF-8 characters, possibly incorrectly encoded';
        break;
        default:
            echo ' - Unknown error';
        break;
    }

    echo "\r\n, json decoded\r\n, ";
    
    //Compare with existing data
    $infoJson = file_get_contents("../data/info.json");
    $infoJsonArray = json_decode($infoJson, true);
    if (strcmp($infoJsonArray["date"], $dataArr["date"]) == 0) {
        //append
        $csvFile = fopen("../data/post.csv", "a");
    } else {
        //new file
        $csvFile = fopen("../data/post.csv", "w");
        $txt = "latitude,longitude\n";
        fwrite($csvFile, $txt);
    }
    
    //perform parsing
    for ($i = 0; $i < count($dataArr["simulationOutput"]); $i++) {
//        echo $dataArr[$i]["lat"];
//        echo ", ";
//        echo $dataArr[$i]["long"];
//        echo "\r\n";
        
        try {
            $txt = $dataArr["simulationOutput"][$i]["lat"] . "," . $dataArr["simulationOutput"][$i]["long"] . "\n";
            fwrite($csvFile, $txt);
        } catch (Exception $e) {
            echo "Couldn't parse data. JSON format might differ from the expected.";
        }
        
//        $csv[$i] = array($dataArr[$i]["lat"], $dataArr[$i]["long"]);
    }

    fclose($csvFile);

    unset($dataArr["simulationOutput"]);
    
    //write to info file
    $jsonFile = fopen("../data/info.json", "w");
    $txt = "user";
    fwrite($jsonFile, json_encode($dataArr));
    fclose($jsonFile);    
    
    
//    $fp = fopen('php://output', 'wb');
//    foreach ($csv as $line) {
//        fputcsv($fp, $line, ',');
//    }
//    fclose($fp);
    
    echo "parsing done";
}
