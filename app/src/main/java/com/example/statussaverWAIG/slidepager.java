package com.example.statussaverWAIG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class slidepager extends AppCompatActivity {
    private static Bundle bundle;
    private int type = 0;
    private boolean down = false;
    private static SharedPreferences ad = null;
    private ArrayList<image_model> list;
    private ImageView back;
    private viewholder adapter;
    private image_model IMG;
    private String Get_Home_IMG,url;
    private static int page = 1;
    private ViewPager2 viewp;
    private static final String pexels_access_key = "563492ad6f91700001000001095982d29e8a45d487166158e86309b3";
    private boolean Search = false;
    private LinearLayout swipelayout;
    private String thumb,original,large2x,large,medium,small,portrait,landscape,query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidepager);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ad = getSharedPreferences("app_details", MODE_PRIVATE);
        bundle = getIntent().getExtras();
        assert bundle != null;
        type = bundle.getInt("type");
        down = bundle.getBoolean("downloaded");

        Search = ad.getBoolean("Search",false);
        page = ad.getInt("page",1);
        url = ad.getString("url","");

        back = findViewById(R.id.back);
        viewp = findViewById(R.id.slide);
        swipelayout = findViewById(R.id.swipemsg);
        list = new ArrayList<>();
        list = (ArrayList<image_model>) bundle.getSerializable("filelist");
        adapter = new viewholder(this,list,type,down);
        viewp.setAdapter(adapter);
        viewp.requestTransform();
        viewp.setCurrentItem(bundle.getInt("position"),false);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipelayout.setVisibility(View.GONE);
            }
        }, 2000);
    }
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
    }
    protected void getUnsplashImage(){
        page++;
        if(Search){
            Get_Home_IMG = "https://api.unsplash.com/search/photos?page="+ page +"&query="+url+"&client_id=N5mzufKM_7Z2CxllYnrNzKGRairerUx_6UApuIzKk3U";
        }else{
            Get_Home_IMG = "https://api.unsplash.com/photos/?page="+ page + "&client_id=N5mzufKM_7Z2CxllYnrNzKGRairerUx_6UApuIzKk3U";
        }
            final StringRequest imagerequest = new StringRequest(Request.Method.GET,Get_Home_IMG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONArray jsonArray;
                    try {
                        if(Search){
                            jsonArray = new JSONObject(response).getJSONArray("results");
                        }else{
                            jsonArray = new JSONArray(response);
                        }
                        for(int i=0;i<jsonArray.length();i++){
                            final JSONObject urls = jsonArray.getJSONObject(i).getJSONObject("urls");
                            thumb = urls.getString("thumb");
                            if(urls.has("raw")){
                                original = urls.getString("raw");
                            }
                            if(urls.has("full")){
                                large = urls.getString("full");
                            }
                            if(urls.has("regular")){
                                medium = urls.getString("regular");
                            }
                            if(urls.has("small")){
                                small = urls.getString("small");
                            }
                            IMG = new image_model(thumb,original,"",large,medium,small,"","");
                            list.add(IMG);
                        }
                        adapter.notifyItemInserted(list.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) { }
            })
            {
            };
            final RequestQueue requestimage = Volley.newRequestQueue(this);
            requestimage.add(imagerequest);
            imagerequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            );

    }

    protected void getPexelsImage(){
        page++;
        if(Search){
            Get_Home_IMG = "https://api.pexels.com/v1/search?query=" + url + "?page="+ page + "&per_page=10";
        }else{
            Get_Home_IMG = "https://api.pexels.com/v1/curated?page=" + page;
        }
        final StringRequest request = new StringRequest(Request.Method.GET, Get_Home_IMG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response);
                        final JSONArray jsonArray = jsonObject.getJSONArray("photos");
                        for(int i=0;i<jsonArray.length();i++){
                            final JSONObject urls = jsonArray.getJSONObject(i).getJSONObject("src");
                            if(urls.has("original")){
                                original = urls.getString("original");
                            }
                            if(urls.has("large2x")){
                                large2x = urls.getString("large2x");
                            }
                            if(urls.has("large")){
                                large = urls.getString("large");
                            }
                            if(urls.has("medium")){
                                medium = urls.getString("medium");
                            }
                            if(urls.has("small")){
                                small = urls.getString("small");
                            }
                            if(urls.has("portrait")){
                                portrait = urls.getString("portrait");
                            }
                            if(urls.has("landscape")){
                                landscape = urls.getString("landscape");
                            }
                            if(urls.has("tiny")){
                                thumb = urls.getString("tiny");
                            }
                            IMG = new image_model(thumb,original,large2x,large,medium,small,portrait,landscape);
                            list.add(IMG);
                        }
                        adapter.notifyItemInserted(list.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", pexels_access_key);
                    return params;
                }

            };
            final RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);

    }

    protected void getPexelsVideo(){
        page++;
        ad.edit().putInt("page",page).commit();
        if(Search){
            Get_Home_IMG = "https://api.pexels.com/videos/search?query=" + url + "&per_page=10&page=" + page;
        }else{
            Get_Home_IMG = "https://api.pexels.com/videos/popular?page=" + page;
        }
            final StringRequest request = new StringRequest(Request.Method.GET, Get_Home_IMG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        final JSONObject jsonObject = new JSONObject(response);
                        final JSONArray jsonArray = jsonObject.getJSONArray("videos");
                        for(int i=0;i<jsonArray.length();i++){
                            final JSONObject jo = jsonArray.getJSONObject(i);
                            final JSONArray ja = jo.getJSONArray("video_files");
                            String[][] videosrc = new String[ja.length()][2];
                            int width = Integer.parseInt(ja.getJSONObject(0).getString("width"));
                            for (int l=0;l<ja.length()-1;l++){
                                if(width > Integer.parseInt(ja.getJSONObject(l).getString("width"))){
                                    width = Integer.parseInt(ja.getJSONObject(l).getString("width"));
                                    videosrc[l][0] = videosrc[0][0];
                                    videosrc[l][1] = videosrc[0][1];
                                    videosrc[0][0] = ja.getJSONObject(l).getString("width") + " * " + ja.getJSONObject(l).getString("height");
                                    videosrc[0][1] = ja.getJSONObject(l).getString("link");
                                }else{
                                    videosrc[l][0] = ja.getJSONObject(l).getString("width") + " * " + ja.getJSONObject(l).getString("height");
                                    videosrc[l][1] =  ja.getJSONObject(l).getString("link");
                                }

                            }
                            IMG = new image_model(jo.getString("image"),videosrc);
                            list.add(IMG);
                        }
                        adapter.notifyItemInserted(list.size() - 1);
                    } catch (JSONException e) {
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", pexels_access_key);
                    return params;
                }

            };
            final RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);

    }

    @Override
    protected void onStop() {
        adapter.unregisterBrodcast();
        super.onStop();
    }
}