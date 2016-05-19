package rykov.serverdatatable;

import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeRefreshLayout;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        ListView listView = (ListView) findViewById(R.id.list);
        adapter = new CustomAdapter(this, R.id.text);
        listView.setAdapter(adapter);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
        } else {
            //noinspection deprecation
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                File cacheDir = new File(getApplicationContext().getCacheDir(), "http");
                long cacheSize = 10 * 1024 * 1024;
                HttpResponseCache.install(cacheDir, cacheSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                File cacheDir = new File(getApplicationContext().getCacheDir(), "http");
                long cacheSize = 10 * 1024 * 1024;
                Class.forName("android.net.http.HttpResponseCache")
                        .getMethod("install", File.class, long.class)
                        .invoke(null, cacheDir, cacheSize);
            } catch (ClassNotFoundException e) {
                    e.printStackTrace();
            } catch (NoSuchMethodException e)  {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        new DataLoader().execute(this);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new DataLoader().execute(this);
    }

    public void setRefreshing(boolean refresh) {
        swipeRefreshLayout.setRefreshing(refresh);
    }

    public CustomAdapter getAdapter() {
        return adapter;
    }
}
