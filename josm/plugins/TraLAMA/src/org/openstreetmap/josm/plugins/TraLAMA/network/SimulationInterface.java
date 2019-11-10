package org.openstreetmap.josm.plugins.TraLAMA.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Copyright (C) 2019 Jakob Smretschnig
 * This program is made available under the terms of the GNU General Public License v3.0
 * which accompanies this distribution
 *
 * @file SimulationInterface.java
 * @author Jakob Smretschnig <jakob.smretschnig@tum.de>
 * @date 2019-08-01
 * @version 2.0
 */
public class SimulationInterface {

    /**
     * For Testing only
     *
     * @param targetUrl trying to set up a connection to this url
     */
    public static boolean isReachable(String targetUrl) throws IOException {
        HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(
                targetUrl).openConnection();
        httpUrlConnection.setRequestMethod("HEAD");

        try {
            int responseCode = httpUrlConnection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (UnknownHostException noInternetConnection) {
            return false;
        }
    }

    private static boolean sendRequest(HttpURLConnection conn) {
        try {
            if (conn.getResponseCode() != 200) {
                System.out.println("Failed : HTTP error code : "
                        + conn.getResponseCode());
                return false;
                //throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.print("Output from Server: ");
            while ((output = br.readLine()) != null) {
                System.out.print(output);
                if (output.equals("-1")) {
                    return false;
                }
            }
            conn.disconnect();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return false;
    }

    public static boolean get(String urlString) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            System.out.println("Sending HTTP Get Request: " + urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            return sendRequest(conn);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return false;
    }

    public static boolean post(String urlString, String jsonInput) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            //System.out.println("Sending POST Request");
            String msg = "Sending HTTP Post Request: " + urlString + "\n" + "with json: ";
            if (jsonInput.length() > 40) {
                System.out.println(msg+jsonInput.substring(0, Math.min(jsonInput.length(), 1000)) + " ... " + jsonInput.substring(jsonInput.length() - 37));
            } else {
                System.out.println(msg+jsonInput);
            }
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("charset", "utf-8");

            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(jsonInput);
            wr.flush();
            wr.close();

            return sendRequest(conn);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return false;
    }
}
