package com.track.trackxtreme.rest.data;

import android.content.Context;
import android.util.Log;

import com.track.trackxtreme.data.track.Track;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by marko on 23/04/2017.
 */

public class HttpRetriever {

    private final static String TAG = "Http";
    private static final int RESPONSE_CODE_FAILED = 400;
    private static final int RESPONSE_CODE_OK = 200;
    private HttpURLConnection mConnection;
    private int mResponseCode;
    private int mUnexpectedStatusLineCount;

    public HttpRetriever(Context applicationContext) {

    }

    public List<Track>  getTracks() {
        return null;
    }



    /**
     * Post.
     *
     * @param json   the json
     * @param apiUrl the api url
     */
    public void post(String json, String apiUrl) {
        try {
            mConnection = (HttpURLConnection) ((new java.net.URL("locahost:8080" + apiUrl).openConnection()));
            mConnection.setDoOutput(true);
            mConnection.setRequestProperty("Content-Type", "application/json");
            mConnection.setRequestProperty("Accept", "application/json");
            mConnection.setRequestMethod("POST");
            mConnection.connect();

            OutputStream os = mConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(json);
            writer.close();
            os.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Get.
     *
     * @param cookie the cookie
     * @param apiUrl the api url
     */
    public void get(String cookie, String apiUrl) {
        try {
            mConnection = (HttpURLConnection) ((new URL("locahost:8080" + apiUrl).openConnection()));
            mConnection.setRequestProperty("Cookie", cookie);
            mConnection.connect();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read response string.
     *
     * @return the string
     */
    public String readResponse() {
        String result = "";
        try {
            mResponseCode = RESPONSE_CODE_FAILED;
            mResponseCode = mConnection.getResponseCode();

            BufferedReader br;
            if (mResponseCode == RESPONSE_CODE_OK) {
                br = new BufferedReader(new InputStreamReader(
                        mConnection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(
                        mConnection.getErrorStream()));

            }
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            result = sb.toString();

            mUnexpectedStatusLineCount = 0;

        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "UnsupportedEncodingException");
            e.printStackTrace();
        } catch (IOException e) {
            if (mUnexpectedStatusLineCount < 3) {
                Log.d(TAG, "IOException, have to get data again!");
                mUnexpectedStatusLineCount++;
            } else {
                mUnexpectedStatusLineCount = 0;
            }
            e.printStackTrace();
        }

        return result;
    }


}
