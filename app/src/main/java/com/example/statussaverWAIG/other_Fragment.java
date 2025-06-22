package com.example.statussaverWAIG;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class other_Fragment extends Fragment{
    private RecyclerView imageRecycleView,catelist;
    private TextView mtv;
    private EditText et;
    private static SharedPreferences ad = null;
    private ImageView close,mimg;
    private image_model IMG;
    private ImageAdapter imgAdapter = null;
    private ArrayList<image_model> ImgList;
    private String url;
    private static int page = 1,position = 0,TYPE=4;
    private boolean Search = false,onlist = false;
    private String thumb,original,large2x,large,medium,small,portrait,landscape,query;
    private static final String unsplash_access_key = "N5mzufKM_7Z2CxllYnrNzKGRairerUx_6UApuIzKk3U";
    private static final String pexels_access_key = "563492ad6f91700001000001095982d29e8a45d487166158e86309b3";
    private static String Get_Home_IMG = null;
    private boolean process = false,autospinner = false,TRY = false;
    private ArrayList<String> categories;
    private categoryAdapter categoryAdapter;
    private FloatingActionButton btn_se,btnsite;
    private ConnectivityManager cm;
    private LinearLayout serview;
    private NetworkInfo activeNetwork;
    private ListView listView;
    private ArrayAdapter adapter;

    private static Database myDB;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        final View otView = inflater.inflate(R.layout.fragment_activity, viewGroup, false);
        ad = getContext().getSharedPreferences("app_details", 0);
        myDB = new Database(getContext());


        cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        imageRecycleView = otView.findViewById(R.id.image_recycleview);
        mtv = otView.findViewById(R.id.tryagain);
        mimg = otView.findViewById(R.id.noimg_image);
        btn_se = otView.findViewById(R.id.btn_search);
        et = otView.findViewById(R.id.edit_url);
        serview = otView.findViewById(R.id.serview);
        close = otView.findViewById(R.id.close);
        btnsite = otView.findViewById(R.id.btn_site);
        ImgList = new ArrayList<>();
        imageRecycleView.setHasFixedSize(true);
        imageRecycleView.scheduleLayoutAnimation();
        listView = otView.findViewById(R.id.searh_list);

        catelist = otView.findViewById(R.id.cate_list);
        catelist.setHasFixedSize(true);
        catelist.scheduleLayoutAnimation();
        final LinearLayoutManager layoutManager = new  LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        catelist.setLayoutManager(layoutManager);
        categoryAdapter = new categoryAdapter(null,other_Fragment.this,false);
        catelist.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();
        catelist.scheduleLayoutAnimation();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(null);
                listView.setVisibility(View.GONE);
                serview.setVisibility(View.GONE);
                closeKeyboard(getActivity(),et);
            }
        });
        et.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                btn_se.performClick();
                return false;
            }
        });
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(et.getText().toString().isEmpty()){
                    listView.setAdapter(null);
                    listView.setVisibility(View.GONE);
                }else{
                    if(TYPE==4){
                        getSearchsuggestion(et.getText().toString(),false);
                    }else if(TYPE==5 || TYPE==6){
                        getPexelsSearchsuggestion(et.getText().toString(),false);
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        btn_se.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serview.getVisibility()==View.VISIBLE){
                    if(!et.getText().toString().isEmpty()) {
                        activeNetwork = cm.getActiveNetworkInfo();
                        if (activeNetwork != null) {
                            closeKeyboard(getActivity(),et);
                            clicksearchbtn();
                        }else{
                            Toast.makeText(getContext(), "Check your connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    View v = et;
                    v.requestFocus();
                    InputMethodManager inputMethodManager =
                            (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow(
                            v.getApplicationWindowToken(),
                            InputMethodManager.SHOW_FORCED, 0);
                    serview.setVisibility(View.VISIBLE);
                }
            }
        });
        btnsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomMenu();
            }
        });
        TYPE = 4;
        Get_Home_IMG = "https://api.unsplash.com/photos/?client_id=" + unsplash_access_key+"&per_page=10";
        activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            getUnsplashImage(false);
        }else{
            mtv.setText("Check Your Connection!\nTap To try again");
        }
        mtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    if(TYPE==4){
                        getUnsplashImage(false);
                    }else if(TYPE==5){
                        getPexelsImage(false);
                    }else if(TYPE==6){
                        getPexelsVideo(false);
                    }
                }
            }
        });
        setdefaultcategory();
        return otView;
    }

    protected void clickcategoryItem(final String catetext){
        onlist = true;
        query = catetext;
        if(TYPE==4){
            getSearchsuggestion(query,true);
        }else{
            getPexelsSearchsuggestion(query,true);
        }
        clicksearchbtn();
    }
    private void getSearchsuggestion(final String text,final boolean cate){
        categories = new ArrayList<>();
        String sugg_url = "https://unsplash.com/nautocomplete/" + text;
        final StringRequest suggrequest = new StringRequest(Request.Method.GET,sugg_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                final String[] searchlist;
                try {
                    final JSONObject object = new JSONObject(response);
                    final JSONArray jsonArray = object.getJSONArray("autocomplete");
                    if(jsonArray.length()>0){
                        searchlist = new String[jsonArray.length()];
                        for(int k = 0;k<searchlist.length;k++){
                            categories.add(jsonArray.getJSONObject(k).getString("query"));
                            searchlist[k] = jsonArray.getJSONObject(k).getString("query");
                        }
                        if(cate){
                            if(categories.size()>1){
                                categoryAdapter = new categoryAdapter(categories,other_Fragment.this,true);
                                catelist.setAdapter(categoryAdapter);
                                categoryAdapter.notifyDataSetChanged();
                                catelist.setVisibility(View.VISIBLE);
                                catelist.scheduleLayoutAnimation();
                            }
                        }else{
                            adapter = new ArrayAdapter<String>(getContext(), R.layout.search_item, searchlist);
                            listView.setAdapter(adapter);
                            listView.setVisibility(View.VISIBLE);
                            listView.scheduleLayoutAnimation();
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    onlist = true;
                                    query = (String) parent.getItemAtPosition(position);
                                    btn_se.performClick();
                                }
                            });
                        }
                    }else{
                        listView.setAdapter(null);
                        listView.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        }) {};
        final RequestQueue requestimage = Volley.newRequestQueue(getContext());
        requestimage.add(suggrequest);
        suggrequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }
    protected void getUnsplashImage(final boolean more){
        listView.setAdapter(null);
        listView.setVisibility(View.GONE);
        if(!process){
            process = true;
            if(more){
                page++;
                ad.edit().putInt("page",page).commit();
                if(Search){
                    Get_Home_IMG = "https://api.unsplash.com/search/photos?page="+ page +"&query="+url+"&client_id=N5mzufKM_7Z2CxllYnrNzKGRairerUx_6UApuIzKk3U";
                }else{
                    Get_Home_IMG = "https://api.unsplash.com/photos/?page="+ page + "&client_id=N5mzufKM_7Z2CxllYnrNzKGRairerUx_6UApuIzKk3U";
                }
            }else{
                imageRecycleView.setVisibility(View.GONE);
                mtv.setVisibility(View.VISIBLE);
                mimg.setVisibility(View.VISIBLE);
                mtv.setText("Loading...");
            }
            final StringRequest imagerequest = new StringRequest(Request.Method.GET,Get_Home_IMG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mtv.setVisibility(View.GONE);
                    mimg.setVisibility(View.GONE);
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
                            ImgList.add(IMG);
                        }
                        if(more){
                            imgAdapter.notifyItemInserted(ImgList.size() - 1);
                            imgAdapter.notifyDataSetChanged();
                        }else {
                            final LinearLayoutManager layoutManager = new  LinearLayoutManager(getContext());
                            layoutManager.setOrientation(RecyclerView.VERTICAL);
                            imageRecycleView.setLayoutManager(layoutManager);
                            imgAdapter = new ImageAdapter(ImgList,other_Fragment.this,null,4);
                            imageRecycleView.setAdapter(imgAdapter);
                            imgAdapter.notifyDataSetChanged();
                            imageRecycleView.setVisibility(View.VISIBLE);
                            imageRecycleView.scheduleLayoutAnimation();
                        }
                        process = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        process = false;
                        imageRecycleView.setVisibility(View.GONE);
                        mtv.setVisibility(View.VISIBLE);
                        mimg.setVisibility(View.VISIBLE);
                        mtv.setText("Plase try again.");
                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    process = false;
                    imageRecycleView.setVisibility(View.GONE);
                    mtv.setVisibility(View.VISIBLE);
                    mimg.setVisibility(View.VISIBLE);
                    mtv.setText("Plase try agian");
                }
            })
            {
            };
            //progressDialog.dismiss();
            final RequestQueue requestimage = Volley.newRequestQueue(getContext());
            requestimage.add(imagerequest);
            imagerequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            );
        }
    }

    private void getPexelsSearchsuggestion(final String text,final boolean cate){
        categories = new ArrayList<>();
        String sugg_url = "https://www.pexels.com/api/v3/search/suggestions/" + text;
        final StringRequest suggpexelrequest = new StringRequest(Request.Method.GET,sugg_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] searchlist;
                try {
                    final JSONObject object = new JSONObject(response);
                    final JSONArray jsonArray = object.getJSONObject("data").getJSONObject("attributes").getJSONArray("suggestions");
                    if(jsonArray.length()>0){
                        searchlist = new String[jsonArray.length()];
                        for(int k = 0;k<searchlist.length;k++){
                            categories.add(jsonArray.getString(k));
                            searchlist[k] = jsonArray.getString(k);
                        }
                        if(cate){
                            if(categories.size()>1){
                                categoryAdapter = new categoryAdapter(categories,other_Fragment.this,true);
                                catelist.setAdapter(categoryAdapter);
                                categoryAdapter.notifyDataSetChanged();
                                catelist.setVisibility(View.VISIBLE);
                                catelist.scheduleLayoutAnimation();
                            }
                        }else {
                            adapter = new ArrayAdapter<String>(getContext(), R.layout.search_item, searchlist);
                            listView.setAdapter(adapter);
                            listView.setVisibility(View.VISIBLE);
                            listView.scheduleLayoutAnimation();
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    onlist = true;
                                    query = (String) parent.getItemAtPosition(position);
                                    btn_se.performClick();
                                }
                            });
                        }
                    }else{
                        listView.setAdapter(null);
                        listView.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        }) {};
        final RequestQueue requestimage = Volley.newRequestQueue(getContext());
        requestimage.add(suggpexelrequest);
        suggpexelrequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }
    protected void getPexelsImage(final boolean more){
        listView.setAdapter(null);
        listView.setVisibility(View.GONE);
        if(!process){
            process = true;
            if(more){
                page++;
                ad.edit().putInt("page",page).commit();
                if(Search){
                    Get_Home_IMG = "https://api.pexels.com/v1/search?query=" + url + "?page="+ page + "&per_page=10";
                }else{
                    Get_Home_IMG = "https://api.pexels.com/v1/curated?page=" + page;
                }
            }else{
                imageRecycleView.setVisibility(View.GONE);
                mtv.setVisibility(View.VISIBLE);
                mimg.setVisibility(View.VISIBLE);
                mtv.setText("Loading...");
            }
            final StringRequest request = new StringRequest(Request.Method.GET, Get_Home_IMG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mtv.setVisibility(View.GONE);
                    mimg.setVisibility(View.GONE);
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
                            ImgList.add(IMG);
                        }
                        if(more){
                            imgAdapter.notifyItemInserted(ImgList.size() - 1);
                            imgAdapter.notifyDataSetChanged();
                        }else{
                            final LinearLayoutManager layoutManager = new  LinearLayoutManager(getContext());
                            layoutManager.setOrientation(RecyclerView.VERTICAL);
                            imageRecycleView.setLayoutManager(layoutManager);
                            imgAdapter = new ImageAdapter(ImgList,other_Fragment.this,null,5);
                            imageRecycleView.setAdapter(imgAdapter);
                            imgAdapter.notifyDataSetChanged();
                            imageRecycleView.setVisibility(View.VISIBLE);
                            imageRecycleView.scheduleLayoutAnimation();
                        }
                        process = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        imageRecycleView.setVisibility(View.GONE);
                        mtv.setVisibility(View.VISIBLE);
                        mimg.setVisibility(View.VISIBLE);
                        mtv.setText("Please try again.");
                        process = false;
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    process = false;
                    imageRecycleView.setVisibility(View.GONE);
                    mtv.setVisibility(View.VISIBLE);
                    mimg.setVisibility(View.VISIBLE);
                    mtv.setText("Please try again.");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", pexels_access_key);
                    return params;
                }

            };
            final RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
            queue.add(request);
        }
    }

    protected void getPexelsVideo(final boolean more){
        listView.setAdapter(null);
        listView.setVisibility(View.GONE);
        if(!process){
            process = true;
            if(more){
                page++;
                ad.edit().putInt("page",page).commit();
                if(Search){
                    Get_Home_IMG = "https://api.pexels.com/videos/search?query=" + url + "&per_page=10&page=" + page;
                }else{
                    Get_Home_IMG = "https://api.pexels.com/videos/popular?page=" + page;
                }
            }else{
                imageRecycleView.setVisibility(View.GONE);
                mtv.setVisibility(View.VISIBLE);
                mimg.setVisibility(View.VISIBLE);
                mtv.setText("Loading...");
            }
            final StringRequest request = new StringRequest(Request.Method.GET, Get_Home_IMG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mtv.setVisibility(View.GONE);
                    mimg.setVisibility(View.GONE);
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
                            ImgList.add(IMG);
                        }
                        if(more){
                            imgAdapter.notifyItemInserted(ImgList.size() - 1);
                            imgAdapter.notifyDataSetChanged();
                        }else{
                            final LinearLayoutManager layoutManager = new  LinearLayoutManager(getContext());
                            layoutManager.setOrientation(RecyclerView.VERTICAL);
                            imageRecycleView.setLayoutManager(layoutManager);
                            imgAdapter = new ImageAdapter(ImgList,other_Fragment.this,null,6);
                            imageRecycleView.setAdapter(imgAdapter);
                            imgAdapter.notifyDataSetChanged();
                            imageRecycleView.setVisibility(View.VISIBLE);
                            imageRecycleView.scheduleLayoutAnimation();
                        }
                        process = false;
                    } catch (JSONException e) {
                        process = false;
                        e.printStackTrace();
                        imageRecycleView.setVisibility(View.GONE);
                        mtv.setVisibility(View.VISIBLE);
                        mimg.setVisibility(View.VISIBLE);
                        mtv.setText("Please try again");
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    process = false;
                    imageRecycleView.setVisibility(View.GONE);
                    mtv.setVisibility(View.VISIBLE);
                    mimg.setVisibility(View.VISIBLE);
                    mtv.setText("Please try again");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", pexels_access_key);
                    return params;
                }

            };
            final RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
            queue.add(request);
        }
    }
    private void setdefaultcategory(){
        categoryAdapter = new categoryAdapter(null,other_Fragment.this,false);
        catelist.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();
        catelist.setVisibility(View.VISIBLE);
        catelist.scheduleLayoutAnimation();
    }

    private void clicksearchbtn(){
        Search = true;
        ad.edit().putBoolean("Search",true).commit();
        position = 0;
        if(onlist){
            onlist = false;
            url = query;
            ad.edit().putString("url",query).commit();
        }else {
            url = et.getText().toString();
            ad.edit().putString("url",url).commit();
            if(categories.size()>1){
                categoryAdapter = new categoryAdapter(categories,other_Fragment.this,true);
                catelist.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();
                catelist.setVisibility(View.VISIBLE);
                catelist.scheduleLayoutAnimation();
            }
        }
        ImgList = new ArrayList<>();
        page = 1;
        ad.edit().putInt("page",1).commit();
        if(TYPE==4){
            Get_Home_IMG = "https://api.unsplash.com/search/photos?query="+url+"&client_id=N5mzufKM_7Z2CxllYnrNzKGRairerUx_6UApuIzKk3U";
            getUnsplashImage(false);
        }else if(TYPE==5){
            Get_Home_IMG = "https://api.pexels.com/v1/search?query=" + url + "&per_page=10";
            getPexelsImage(false);
        }else if(TYPE==6){
            Get_Home_IMG = "https://api.pexels.com/videos/search?query=" + url + "&per_page=10";
            getPexelsVideo(false);
        }
        listView.setAdapter(null);
        listView.setVisibility(View.GONE);
        serview.setVisibility(View.GONE);
        et.getText().clear();
    }

    private void closeKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void bottomMenu(){
        final BottomSheetDialog bt = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
        View vi = LayoutInflater.from(getContext()).inflate(R.layout.bottom_site_menu,null);
        vi.findViewById(R.id.unsplahphoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.dismiss();
                setdefaultcategory();
                TYPE = 4;
                et.getText().clear();
                Search = false;
                ad.edit().putBoolean("Search",false).commit();
                ad.edit().putString("url","").commit();
                position = 0;
                ImgList = new ArrayList<>();
                page = 1;
                ad.edit().putInt("page",1).commit();
                Get_Home_IMG = "https://api.unsplash.com/photos/?client_id=" + unsplash_access_key+"&per_page=10";
                et.setHint("Search Photos");
                getUnsplashImage(false);
            }
        });
        vi.findViewById(R.id.pexelsphoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.dismiss();
                setdefaultcategory();
                TYPE = 5;
                et.getText().clear();
                Search = false;
                ad.edit().putBoolean("Search",false).commit();
                ad.edit().putString("url","").commit();
                position = 0;
                ImgList = new ArrayList<>();
                page = 1;
                ad.edit().putInt("page",1).commit();
                Get_Home_IMG = "https://api.pexels.com/v1/curated?per_page=10";
                et.setHint("Search Photos");
                getPexelsImage(false);
            }
        });
        vi.findViewById(R.id.pexelsvideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.dismiss();
                setdefaultcategory();
                TYPE = 6;
                et.getText().clear();
                Search = false;
                ad.edit().putBoolean("Search",false).commit();
                ad.edit().putString("url","").commit();
                position = 0;
                ImgList = new ArrayList<>();
                page = 1;
                ad.edit().putInt("page",1).commit();
                Get_Home_IMG = "https://api.pexels.com/videos/popular?per_page=10";
                et.setHint("Search Videos");
                getPexelsVideo(false);
            }
        });
        bt.setContentView(vi);
        bt.show();
    }

}