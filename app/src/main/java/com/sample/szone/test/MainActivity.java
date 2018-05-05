package com.sample.szone.test;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyAk5kfRweMvBkQxczSHMjLrQjnsDmM4gP8";


    //>>> Supply YouTube PlayList ID in the form of an array <<<
    private static String[] PLAYLIST_ID = {
            "PLqyIChhuor0yARcccbDi1tzXMEUBPhaqf",
            "PL6CTrxW12Bre4kny-OhqOEQwNjso0VKPc",
            "RDHCjNJDNzw8Y",
            "PLSTz8jpJdr5p2OSr_5TAV8iLIMKlk7Hh7",
            "PLFgquLnL59alCl_2TQvOiD5Vgm1hCaGSI",
            "PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj",
            "RD1seDBXvGYcc&t=52",
            "RDBJeCOo_CRyE",
            "RDFM7MFYoylVs",
            "PL9LUD5Kp855IUocbAjUd6Du9UmvZrbKk7",
            "PLqyIChhuor0yk5dhRjgK7m6r0RrL9Hvle"
                                            };

    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();
    int j;
    YoutubeDataModel dataModel, dataModel1;
    String customKey = null;
    final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        j=0;
        if(!isNetworkConnected()){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            builder1.setMessage("Internet Access Needed!");
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    "Open Settings",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setClassName("com.android.settings",
                                    "com.android.settings.Settings$DataUsageSummaryActivity");
                            startActivity(intent);

                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }

        recyclerView =  findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        initList(mListData);

        new RequestYoutubeAPI().execute();



        OneSignal.startInit(this).setNotificationOpenedHandler(new ExampleNotificationOpenedHandler()).init();

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton2);

        new RequestYoutubeAPI2().execute();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                j=1;
                if(dataModel!=null) {

                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    intent.putExtra("check",j);
                    intent.putExtra(YoutubeDataModel.class.toString(), dataModel);
                    startActivity(intent);
                }
            }
        });


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {

            Animation animation = new TranslateAnimation(0f, 0f, 250f, 0f);
            animation.setDuration(1000);
            animation.setFillAfter(true);
            animation.setInterpolator(new BounceInterpolator());

            ToolTip toolTip = new ToolTip()
                    .setTitle("Introducing LiveTv!")
                    .setDescription("Click on Get Started to Watch...")
                    .setTextColor(Color.parseColor("#ffffff"))
                    .setBackgroundColor(Color.parseColor("#551a8b"))
                    .setShadow(true)
                    .setGravity(Gravity.TOP | Gravity.START)
                    .setEnterAnimation(animation);

            final Overlay overlay = new Overlay();
            final TourGuide mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                    .setPointer(new Pointer())
                    .setToolTip(toolTip)
                    .setOverlay(overlay)
                    .playOn(floatingActionButton);


            overlay.setBackgroundColor(Color.parseColor("#97000000"))
                    .disableClick(true)
                    .setStyle(Overlay.Style.CIRCLE);

            overlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTourGuideHandler.cleanUp();
                }
            });

            settings.edit().putBoolean("my_first_time", false).apply();
        }

    }


    public ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.has("kind")) {
                        if (json.getString("kind").equals("youtube#playlistItem")) {
                            YoutubeDataModel youtubeObject = new YoutubeDataModel();
                            JSONObject jsonSnippet = json.getJSONObject("snippet");
                            String vedio_id = "";
                            if (jsonSnippet.has("resourceId")) {
                                JSONObject jsonResource = jsonSnippet.getJSONObject("resourceId");
                                vedio_id = jsonResource.getString("videoId");

                            }
                            String title = jsonSnippet.getString("title");
                            String description = jsonSnippet.getString("description");
                            String publishedAt = jsonSnippet.getString("publishedAt");
                            String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");
                            String channel = jsonSnippet.getString("channelTitle");
                            if(channel==null)
                                channel = jsonSnippet.getJSONObject("localized").getString("title");
                            if(channel==null)
                                channel = title;

                            youtubeObject.setTitle(title);
                            youtubeObject.setDescription(description);
                            youtubeObject.setPublishedAt(publishedAt);
                            youtubeObject.setThumbnail(thumbnail);
                            youtubeObject.setVideo_id(vedio_id);
                            youtubeObject.setChannel(channel);
                            mList.add(youtubeObject);

                        }
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mList;

    }

    public YoutubeDataModel parseVideoListFromResponse2(JSONObject jsonObject) {

        YoutubeDataModel youtubeObject = new YoutubeDataModel();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");

                JSONObject json = jsonArray.getJSONObject(0);
                if (json.has("kind")) {
                    if (json.getString("kind").equals("youtube#playlistItem")) {
                        JSONObject jsonSnippet = json.getJSONObject("snippet");
                        String vedio_id = "";
                        if (jsonSnippet.has("resourceId")) {
                            JSONObject jsonResource = jsonSnippet.getJSONObject("resourceId");
                            vedio_id = jsonResource.getString("videoId");

                        }
                        String title = jsonSnippet.getString("title");
                        String description = jsonSnippet.getString("description");
                        String publishedAt = jsonSnippet.getString("publishedAt");
                        String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");
                        String channel = jsonSnippet.getString("channelTitle");
                        if(channel==null)
                            channel = jsonSnippet.getJSONObject("localized").getString("title");
                        if(channel==null)
                            channel = title;

                        youtubeObject.setTitle(title);
                        youtubeObject.setDescription(description);
                        youtubeObject.setPublishedAt(publishedAt);
                        youtubeObject.setThumbnail(thumbnail);
                        youtubeObject.setVideo_id(vedio_id);
                        youtubeObject.setChannel(channel);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return youtubeObject;
    }


    @SuppressLint("StaticFieldLeak")
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            Random rand = new Random();

            String CHANNEL_GET_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + PLAYLIST_ID[rand.nextInt(10)] + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY + "";


            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNEL_GET_URL);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                        JSONObject jsonObject = new JSONObject(response);
                        mListData = parseVideoListFromResponse(jsonObject);
                        initList(mListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RequestYoutubeAPI2 extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            String CHANNEL_GET_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + PLAYLIST_ID[10] + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY + "";


            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNEL_GET_URL);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    dataModel = parseVideoListFromResponse2(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RequestYoutubeAPI3 extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            String CHANNEL_GET_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + customKey + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY + "";

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNEL_GET_URL);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    dataModel1 = parseVideoListFromResponse2(jsonObject);

                    if(dataModel1!=null) {
                        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                        intent.putExtra("check",j);
                        intent.putExtra(YoutubeDataModel.class.toString(), dataModel1);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void initList(ArrayList<YoutubeDataModel> mListData) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        MoviesAdaptor adapter = new MoviesAdaptor(getApplicationContext(), mListData, new OnItemClickListener() {
            @Override
            public void onItemClick(YoutubeDataModel item) {

                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                intent.putExtra("check",j);
                intent.putExtra(YoutubeDataModel.class.toString(), item);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

    }

    class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;

            j=2;

            if (data != null) {
                customKey = data.optString("playlistId");
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken)
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

            new RequestYoutubeAPI3().execute();

        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("youtube.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}