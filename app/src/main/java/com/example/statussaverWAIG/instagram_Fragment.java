package com.example.statussaverWAIG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class instagram_Fragment extends Fragment {
    private RecyclerView imageRecycleView;
    private TextView mtv;
    private EditText et;
    private static SharedPreferences ad = null;
    private ImageView close,mimg,clear;
    private image_model IMG;
    private ImageAdapter imgAdapter = null;
    private ArrayList<image_model> ImgList;
    private static boolean process = false;
    private String url;
    private FloatingActionButton btn_add;
    private ConnectivityManager cm;
    private LinearLayout serview;
    private SwipeRefreshLayout refreshLayout;
    private NetworkInfo activeNetwork;
    private static Database myDB;

    @SuppressLint("ApplySharedPref")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        final View instaView = inflater.inflate(R.layout.insta_fragment_activity, viewGroup, false);
        ad = getContext().getSharedPreferences("app_details", 0);
        myDB = new Database(getContext());


        cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        imageRecycleView = instaView.findViewById(R.id.image_recycleview);
        mtv = instaView.findViewById(R.id.msg_img);
        mimg = instaView.findViewById(R.id.noimg_image);
        btn_add = instaView.findViewById(R.id.btn_add);
        et = instaView.findViewById(R.id.edit_url);
        serview = instaView.findViewById(R.id.serview);
        close = instaView.findViewById(R.id.close);
        clear = requireActivity().findViewById(R.id.clear);
        refreshLayout = instaView.findViewById(R.id.refresh);

        ImgList = new ArrayList<>();
        imageRecycleView.setHasFixedSize(true);
        imageRecycleView.scheduleLayoutAnimation();
        if(ad.getString("EnterByInstagram","false").equals("true")){
            url = ad.getString("sharelink","");
            ad.edit().putString("EnterByInstagram","false").commit();
            activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                clicksearchbtn();
            }else{
                Toast.makeText(getContext(), "Check your connection", Toast.LENGTH_SHORT).show();
            }
        }else if(ad.getBoolean("firstinsta",false)){
            url = ad.getString("instalink","");
            activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                clicksearchbtn();
                ad.edit().putBoolean("firstinsta",false).commit();
            }else{
                Toast.makeText(getContext(), "Check your connection", Toast.LENGTH_SHORT).show();
            }
        }else{
            GetInstaData();
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(getActivity(),et);
                serview.setVisibility(View.GONE);
            }
        });
        et.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(!et.getText().toString().isEmpty()) {
                    url = et.getText().toString();
                    activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) {
                        closeKeyboard(getActivity(),et);
                        clicksearchbtn();
                    }else{
                        Toast.makeText(getContext(), "Check your connection", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        et.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(!et.getText().toString().isEmpty()) {
                    url = et.getText().toString();
                    activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) {
                        closeKeyboard(getActivity(),et);
                        clicksearchbtn();
                    }else{
                        Toast.makeText(getContext(), "Check your connection", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serview.getVisibility()==View.VISIBLE){
                    if(!et.getText().toString().isEmpty()) {
                        url = et.getText().toString();
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
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispdialog();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetInstaData();
                refreshLayout.setRefreshing(false);
            }
        });
        return instaView;
    }
    private void clicksearchbtn(){
        if(URLUtil.isValidUrl(url)) {
            if(url.startsWith("https://www.instagram.com")){
                if(url.startsWith("https://www.instagram.com/p/")){
                    final Cursor cursor = myDB.is_exists_insta_post(url);
                    if(cursor.getCount()==0) {
                        instagramfeed();
                    }else{
                        Toast.makeText(getActivity(), "This Link Already Exists.", Toast.LENGTH_SHORT).show();
                    }
                }else if(url.startsWith("https://www.instagram.com/tv/")){
                    final Cursor cursor = myDB.is_exists_insta_post(url);
                    if(cursor.getCount()==0) {
                        reelsOnlyvideo(true);
                    }else{
                        Toast.makeText(getActivity(), "This Link Already Exists.", Toast.LENGTH_SHORT).show();
                    }
                }else if(url.startsWith("https://www.instagram.com/reel/")){
                    final Cursor cursor = myDB.is_exists_insta_post(url);
                    if(cursor.getCount()==0) {
                        reelsOnlyvideo(false);
                    }else{
                        Toast.makeText(getActivity(), "This Link Already Exists.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Enter Only Post,Reels & IGTV Link.", Toast.LENGTH_SHORT).show();
                }
                serview.setVisibility(View.GONE);
                et.getText().clear();
            }else{
                Toast.makeText(getContext(), "Enter only Instagram Link", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getContext(), "Not valid URL", Toast.LENGTH_SHORT).show();
        }
    }
    private void dispdialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are You Sure");
        builder.setMessage("Remove All Instagram Items?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(mtv.getVisibility()==View.VISIBLE){
                            Toast.makeText(getActivity(), "No Items Yet.", Toast.LENGTH_SHORT).show();
                        }else{
                            myDB.deteleinsta();
                            GetInstaData();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void GetInstaData(){
        ImgList = new ArrayList<>();
        final Cursor cursor = myDB.get_insta_post();
        if(cursor.getCount()!=0) {
            clear.setVisibility(View.VISIBLE);
            mimg.setVisibility(View.GONE);
            mtv.setVisibility(View.GONE);
            while (cursor.moveToNext()) {
                String[] temp = cursor.getString(2).split(",");
                final String[][] src = new String[temp.length/2][2];
                if(temp.length==2){
                    src[0][0] = temp[0];
                    src[0][1] = temp[1];
                }else{
                    for(int i=0,k=0;i<src.length;i++,k++){
                        src[i][0] = temp[k].substring(1);
                        k = k + 1;
                        src[i][1] = temp[k].substring(0,temp[k].length()-1);
                    }
                }
                if(cursor.getString(3).equals("1")){
                    IMG = new image_model(cursor.getInt(0),cursor.getString(4),src,true,cursor.getString(1),cursor.getString(5));
                }else{
                    IMG = new image_model(cursor.getInt(0),"",src,false,cursor.getString(0),cursor.getString(5));
                }
                ImgList.add(IMG);
            }
            imageRecycleView.setLayoutManager(new GridLayoutManager(getContext(),3));
            imgAdapter = new ImageAdapter(ImgList,null,this,2);
            imageRecycleView.setAdapter(imgAdapter);
            imgAdapter.notifyDataSetChanged();
            imageRecycleView.setVisibility(View.VISIBLE);
            imageRecycleView.scheduleLayoutAnimation();
        }else{
            clear.setVisibility(View.GONE);
            imageRecycleView.setVisibility(View.GONE);
            mimg.setVisibility(View.VISIBLE);
            mtv.setVisibility(View.VISIBLE);
            mtv.setText("Paste Instagram Post,Reels,IGTV Link & Get Photo & Video");
        }
    }
    private void instagramfeed(){
        if(!process){
            process = true;
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loding...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            String[] arrOfStr = url.split("/p/", 2);
            arrOfStr = arrOfStr[1].split("/", 2);
            final String path = "https://www.instagram.com/p/"+arrOfStr[0]+"/?__a=1";
            final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, path, null,
                    new Response.Listener<JSONObject>()
                    {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            if(ImgList.size()<=0){
                                ImgList = new ArrayList<>();
                            }
                            try {
                                JSONObject ob = response.getJSONObject("graphql").getJSONObject("shortcode_media");
                                if(ob.has("edge_sidecar_to_children")){
                                    final JSONArray jsonArray = ob.getJSONObject("edge_sidecar_to_children").getJSONArray("edges");
                                    final String[][] edges = new String[jsonArray.length()][2];
                                    String t = null;
                                    for(int i=edges.length-1;i>=0;i--){
                                        final JSONObject JO = jsonArray.getJSONObject(i).getJSONObject("node");
                                        if(JO.getString("is_video").equals("true")){
                                            final String[][] src = new String[1][2];
                                            src[0][0] = "Original";
                                            src[0][1] = JO.getString("video_url");
                                            myDB.inert_insta_post(src[0][0]+","+src[0][1],1,JO.getString("display_url"),url);
                                        }else{
                                            final JSONArray JA = JO.getJSONArray("display_resources");
                                            final String[][] src = new String[JA.length()][2];
                                            for(int k=0;k<JA.length();k++){
                                                src[k][0] = JA.getJSONObject(k).getString("config_width") + " * " + JA.getJSONObject(k).getString("config_height");
                                                src[k][1] = JA.getJSONObject(k).getString("src");
                                            }
                                            StringBuilder sb = new StringBuilder();
                                            for (String[] row : src) {
                                                sb.append(Arrays.toString(row))
                                                        .append(",");
                                            }
                                            myDB.inert_insta_post(sb.toString(),0,"",url);
                                        }
                                    }
                                }else{
                                    if(ob.getString("is_video").equals("true")){
                                        final String[][] src = new String[1][2];
                                        src[0][0] = "Original";
                                        src[0][1] = ob.getString("video_url");
                                        myDB.inert_insta_post(src[0][0]+","+src[0][1],1,ob.getString("display_url"),url);
                                    }else{
                                        final JSONArray jsonArray = ob.getJSONArray("display_resources");
                                        final String[][] src = new String[jsonArray.length()][2];
                                        for(int i=0;i<jsonArray.length();i++){
                                            src[i][0] = jsonArray.getJSONObject(i).getString("config_width") + " * " + jsonArray.getJSONObject(i).getString("config_height");
                                            src[i][1] = jsonArray.getJSONObject(i).getString("src");

                                        }
                                        StringBuilder sb = new StringBuilder();
                                        for (String[] row : src) {
                                            sb.append(Arrays.toString(row))
                                                    .append(",");
                                        }
                                        myDB.inert_insta_post(sb.toString(),0,"",url);
                                    }
                                }
                                process = false;
                                GetInstaData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                process = false;
                                Toast.makeText(getActivity(), "Try Again j", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            process = false;
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(getRequest);
        }
    }
    private void reelsOnlyvideo(final boolean IsTV){
        if(!process) {
            process = true;
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loding...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            String path;
            if(IsTV){
                String[] arrOfStr = url.split("/tv/", 2);
                arrOfStr = arrOfStr[1].split("/", 2);
                path = "https://www.instagram.com/tv/"+arrOfStr[0]+"/?__a=1";
            }else{
                String[] arrOfStr = url.split("/reel/", 2);
                arrOfStr = arrOfStr[1].split("/", 2);
                path = "https://www.instagram.com/tv/"+arrOfStr[0]+"/?__a=1";
            }
            final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, path, null,
                    new Response.Listener<JSONObject>()
                    {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            try {
                                final JSONObject ob = response.getJSONObject("graphql").getJSONObject("shortcode_media");
                                final String[][] src = new String[1][2];
                                src[0][0] = "Original";
                                src[0][1] = ob.getString("video_url");
                                myDB.inert_insta_post(src[0][0]+","+src[0][1],1,ob.getString("display_url"),url);
                                process = false;
                                GetInstaData();
                            } catch (JSONException e) {
                                process = false;
                                Toast.makeText(getContext(), "Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            process = false;
                            Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(getRequest);
        }
    }
    private void closeKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}