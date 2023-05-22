package com.example.project;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;

public class JsonTask extends AsyncTask<String, String, String> {

    private static final String TAG = "JsonTask";

    public interface JsonTaskListener {
        void onPostExecute(String json);
    }

    private HttpURLConnection connection = null;
    private BufferedReader reader = null;
    private final JsonTaskListener listener;

    @SuppressWarnings("deprecation")
    public JsonTask(JsonTaskListener listener) {
        this.listener = listener;
    }

    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            Log.d(TAG, "Connecting to URL: " + url);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder builder = new StringBuilder();
            String line;
            Log.d(TAG, "Received JSON: " + builder.toString());
            while ((line = reader.readLine()) != null && !isCancelled()) {
                builder.append(line).append("\n");

            }
            return builder.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed URL: ", e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get JSON from: ", e);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String json) {
        listener.onPostExecute(json);
    }
}