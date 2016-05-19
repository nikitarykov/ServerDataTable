package rykov.serverdatatable;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Nikita Rykov on 15.05.2016.
 */
public class DataLoader extends AsyncTask<MainActivity, Void, List<HashMap<String,String>>> {
    MainActivity activity;

    @Override
    protected List<HashMap<String,String>> doInBackground(MainActivity... params) {
        List<HashMap<String,String>> result = new ArrayList<>();
        activity = params[0];
        HttpURLConnection urlConnection = null;
        String data = null;
        try {
            URL url = new URL("http://mobile165.hr.phobos.work/list");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(true);
            InputStream inputStream;
            if (isConnected()) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                HttpResponseCache cache = HttpResponseCache.getInstalled();
                if (cache != null) {
                    cache.flush();
                }
            } else {
                int maxStale = 60 * 60 * 24 * 28;
                urlConnection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int b = inputStream.read();
            while(b != -1) {
                outputStream.write(b);
                b = inputStream.read();
            }
            data = outputStream.toString();
            JSONObject json = new JSONObject(data);
            HashMap<String, String> item = new HashMap<>();
            JSONObject user = json.getJSONObject("user");
            item.put("type", "user");
            item.put("balance", user.getString("balance"));
            item.put("miles", user.getString("miles"));
            result.add(item);
            JSONObject feed = json.getJSONObject("feed");
            Iterator<String> feedIt = feed.keys();
            while (feedIt.hasNext()) {
                String year = feedIt.next();
                HashMap<String, String> yearItem = new HashMap<>();
                yearItem.put("type", "year");
                yearItem.put("year", year);
                result.add(yearItem);
                JSONArray array = feed.getJSONArray(year);
                for (int i = 0; i < array.length(); ++i) {
                    HashMap<String, String> feedItem = new HashMap<>();
                    JSONObject obj = array.getJSONObject(i);
                    feedItem.put("type", "feed");
                    feedItem.put("details", obj.getString("details"));
                    feedItem.put("comment", obj.getString("comment"));
                    feedItem.put("cost", obj.getJSONObject("money").getString("amount") + " " +
                            obj.getJSONObject("money").getString("curerncy_code"));
                    feedItem.put("category", obj.getJSONObject("category").getString("name"));
                    feedItem.put("icon", obj.getJSONObject("category").getString("icon"));
                    result.add(feedItem);
                }
            }
            inputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    protected boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    protected void onPostExecute(List<HashMap<String,String>> result) {
        activity.getAdapter().setData(result);
        activity.setRefreshing(false);
    }
}
