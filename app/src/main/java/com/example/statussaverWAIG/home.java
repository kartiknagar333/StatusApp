package com.example.statussaverWAIG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import io.alterac.blurkit.BlurLayout;


public class home extends AppCompatActivity {
    private CardView WAbtn,IGbtn,OTbtn;
    private ImageView menu,download;
    private static SharedPreferences ad = null;
    private boolean flag = false;
    private BlurLayout blurLayout;

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        blurLayout = findViewById(R.id.blurlayout);
        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ad = getSharedPreferences("app_details", MODE_PRIVATE);

        final Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        final RelativeLayout heading = findViewById(R.id.topbanner);

        final int resourceId1 = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId1 > 0) {
            int height1 = getResources().getDimensionPixelSize(resourceId1);
            ad.edit().putInt("status_bar_height",height1).commit();
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params1.setMargins(0, height1, 0, 0);
            heading.setLayoutParams(params1);
        }

        menu = findViewById(R.id.homemenu);
        download = findViewById(R.id.download);
        WAbtn = findViewById(R.id.whatsapp);
        IGbtn = findViewById(R.id.insta);
        OTbtn = findViewById(R.id.other);


        WAbtn.setBackgroundResource(R.drawable.select_wa);
        final Intent intent = getIntent();
        final String action = intent.getAction();
        final String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                if (intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
                    flag = true;
                    ad.edit().putString("sharelink",intent.getStringExtra(Intent.EXTRA_TEXT)).commit();
                    ad.edit().putString("EnterByInstagram","true").commit();
                }
            }
        }

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("WHATSAPP"));
        tabLayout.addTab(tabLayout.newTab().setText("INTSAGRAM"));
        tabLayout.addTab(tabLayout.newTab().setText("OTHER"));
        final ViewPager viewPager =(ViewPager)findViewById(R.id.view_pager);
        com.example.statussaverWAIG.TabsAdapter tabsAdapter = new com.example.statussaverWAIG.TabsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabsAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                final int finalTab = tab.getPosition();
                if(finalTab ==0){
                    WAbtn.setBackgroundResource(R.drawable.select_wa);
                    IGbtn.setBackgroundResource(R.drawable.unselect);
                    OTbtn.setBackgroundResource(R.drawable.unselect);
                }else if(finalTab==1){
                    WAbtn.setBackgroundResource(R.drawable.unselect);
                    IGbtn.setBackgroundResource(R.drawable.select_insta);
                    OTbtn.setBackgroundResource(R.drawable.unselect);
                } else if(finalTab==2){
                    WAbtn.setBackgroundResource(R.drawable.unselect);
                    IGbtn.setBackgroundResource(R.drawable.unselect);
                    OTbtn.setBackgroundResource(R.drawable.select_sh);
                }

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if(flag){
            flag = false;
            viewPager.setCurrentItem(1);
        }
        WAbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent dt = new Intent(home.this,downloads.class);
                startActivity(dt);
            }
        });

        IGbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });
        OTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(2);
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomMenu();
            }
        });

    }


    private void ratedialog(){
        if(!ad.getBoolean("firstrate",false)){
            try {
                final ReviewManager manager = ReviewManagerFactory.create(this);
                Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<ReviewInfo> task) {
                        if(task.isSuccessful()){
                            ReviewInfo reviewInfo = task.getResult();
                            Task<Void> flow = manager.launchReviewFlow(home.this,reviewInfo);
                            flow.addOnSuccessListener(new OnSuccessListener<Void>(){
                                @Override
                                public void onSuccess(Void result) {

                                }
                            });
                        }else{
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=com.shrilink.android")));
                        }
                    }
                });
            }catch (Exception e){
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=com.shrilink.android")));
            }
            ad.edit().putBoolean("firstrate",true).commit();
        }else{
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.shrilink.android")));
        }


    }
    private void bottomMenu(){
        final BottomSheetDialog bt = new BottomSheetDialog(home.this,R.style.BottomSheetDialogTheme);
        View vi = LayoutInflater.from(home.this).inflate(R.layout.bottom_home_menu,null);
        vi.findViewById(R.id.rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.dismiss();
                ratedialog();
            }
        });
        vi.findViewById(R.id.aboutapp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.dismiss();
                ad.edit().putBoolean("enterbyabout",true).commit();
                startActivity(new Intent(home.this,MainActivity.class));
            }
        });
        vi.findViewById(R.id.shareapp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.dismiss();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,getString(R.string.share_app_msg));
                startActivity(Intent.createChooser(i, "Share App Link"));
            }
        });
        bt.setContentView(vi);
        bt.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        blurLayout.startBlur();
    }
    @Override
    protected void onStop() {
        blurLayout.pauseBlur();
        super.onStop();
    }
}