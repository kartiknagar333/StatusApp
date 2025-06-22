package com.example.statussaverWAIG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OpenChat extends AppCompatActivity{
    private CountryCodePicker countryCodePicker;
    private com.example.statussaverWAIG.EntityViewModel entityViewModel;
    private static EntityDatabase instance;
    private RecyclerView recyclerView;
    private com.example.statussaverWAIG.NumberAdapter adapter;
    private TextView mtv;
    private static SharedPreferences ad = null;
    private ImageView centerimg,back,removeall,next;
    private EditText number,name;

    private static String locale,phonenumbe="",countrycode="",te;
    private static String[] value = new String[5];
    private static Date currentTime;
    private DAO entityDAO;
    private static com.example.statussaverWAIG.Entity Edituser;
    private int identity = 1;
    private static boolean isedit = false,isrun = false;
    private ProgressBar pbar;
    private List<Entity> oldlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_chat);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        final Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ad = getSharedPreferences("app_details", MODE_PRIVATE);
        if (ad.getInt("status_bar_height",0) > 0) {
            final LinearLayout heading = findViewById(R.id.topbanner);
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params1.setMargins(0, ad.getInt("status_bar_height",0), 0, 0);
            heading.setLayoutParams(params1);
        }
        number = findViewById(R.id.number);
        recyclerView = findViewById(R.id.image_recycleview);
        mtv = findViewById(R.id.msg_img);
        countryCodePicker = findViewById(R.id.ccp);
        centerimg = findViewById(R.id.noimg_image);
        name = findViewById(R.id.pname);
        next = findViewById(R.id.next);
        removeall = findViewById(R.id.removedownloads);
        back = findViewById(R.id.backactivity);
        pbar = findViewById(R.id.pbar);

        recyclerView.scheduleLayoutAnimation();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        instance = EntityDatabase.getInstance(this);
        entityDAO = instance.userDAO();
        adapter = new com.example.statussaverWAIG.NumberAdapter();
        recyclerView.setAdapter(adapter);

        entityViewModel = new ViewModelProvider(this).get(com.example.statussaverWAIG.EntityViewModel.class);
        entityViewModel.getAlluser().observe(this, new Observer<List<Entity>>() {
            @Override
            public void onChanged(List<Entity> entities) {
                if(entities.isEmpty()){
                    removeall.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    mtv.setVisibility(View.VISIBLE);
                    centerimg.setVisibility(View.VISIBLE);
                }else{
                    oldlist = entities;
                    removeall.setVisibility(View.VISIBLE);
                    mtv.setVisibility(View.GONE);
                    centerimg.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.submitList(entities);
                    identity = entities.get(0).getId() + 1;
                }
            }
        });

        countrycode = countryCodePicker.getDefaultCountryCode();
        locale = countryCodePicker.getDefaultCountryNameCode().toUpperCase();
        currentTime = Calendar.getInstance().getTime();

        showkeyboard(number);


        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country country) {
                countrycode = country.getPhoneCode();
                locale = country.getIso().toUpperCase();
            }
        });

        adapter.setOnItemClickListener(new com.example.statussaverWAIG.NumberAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.example.statussaverWAIG.Entity user) {
                clickitem(user,String.valueOf(user.getCountry_code())+String.format("%.0f", user.getPhone_number()));
            }
        });

        number.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                fun();
                return true;
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fun();
            }
        });
        removeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isedit = false;
                dispdialog();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void fun(){
        pbar.setVisibility(View.VISIBLE);
        next.setVisibility(View.GONE);
        phonenumbe = number.getText().toString();
        if(isedit){
            closeKeyboard(OpenChat.this,number);
            for(int k = 0;k<oldlist.size();k++){
                te = String.valueOf(oldlist.get(k).getCountry_code()) + String.format("%.0f", oldlist.get(k).getPhone_number());
                if(te.equals(countrycode+phonenumbe) && oldlist.get(k).getId()!=Edituser.getId()){
                    isedit = false;
                    pbar.setVisibility(View.GONE);
                    next.setVisibility(View.VISIBLE);
                    k = oldlist.size() + 2;
                    number.getText().clear();
                    name.getText().clear();
                    Toast.makeText(OpenChat.this, "This Number is already exists.", Toast.LENGTH_SHORT).show();
                }else if(k==oldlist.size()-1){
                    editnumbercheck();
                    pbar.setVisibility(View.GONE);
                    next.setVisibility(View.VISIBLE);
                }
            }
        }else{
            if(centerimg.getVisibility()==View.VISIBLE){
                checknumber();
                pbar.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
            }else{
                for(int k = 0;k<oldlist.size();k++){
                    te = String.valueOf(oldlist.get(k).getCountry_code()) + String.format("%.0f", oldlist.get(k).getPhone_number());
                    if(te.equals(countrycode+phonenumbe)){
                        pbar.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                        k = oldlist.size() + 2;
                        Toast.makeText(OpenChat.this, "This Number is already exists.", Toast.LENGTH_SHORT).show();
                    }else if(k==oldlist.size()-1){
                        checknumber();
                        pbar.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private static class populateDB extends AsyncTask<String,Void,Void> {
        private DAO userdao;
        private int id;
        private populateDB(EntityDatabase entityDatabase,int id){
            userdao = entityDatabase.userDAO();
            this.id = id;
        }
        @Override
        protected Void doInBackground(String... strings) {
            userdao.insert(new Entity(id,Integer.parseInt(strings[0]),Double.parseDouble(strings[1]),strings[2] ,strings[3],strings[4]));
            return null;
        }
    }
    private static class DeleteEntity extends AsyncTask<Void,Void,Void>{
        private DAO dao;
        private com.example.statussaverWAIG.Entity user;
        private DeleteEntity(DAO dao,com.example.statussaverWAIG.Entity user){
            this.dao = dao;
            this.user = user;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            dao.delete(user);
            return null;
        }
    }
    private static class UpdateEntity extends AsyncTask<Void,Void,Void>{
        private DAO dao;
        private com.example.statussaverWAIG.Entity user;
        private UpdateEntity(DAO dao,com.example.statussaverWAIG.Entity user){
            this.dao = dao;
            this.user = user;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            dao.update(user);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            isedit = false;
        }
    }
    private static class DeleteAllEntity extends AsyncTask<Void,Void,Void>{
        private DAO dao;
        private DeleteAllEntity(DAO dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteAlluser();
            return null;
        }
    }

    private String getDatetime() {
        String datetime = currentTime.getYear()+"-"+currentTime.getMonth()+"-"+currentTime.getDate()+" "+
                currentTime.getHours()+":"+currentTime.getMinutes()+":"+currentTime.getSeconds();
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(datetime);
            format = new SimpleDateFormat("dd MMM yy  h:m a");
            String date = format.format(newDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return datetime;
        }
    }

    private void editnumbercheck(){
        if(phonenumbe.isEmpty()){
            Toast.makeText(OpenChat.this, "Check Country Code with Phone Number", Toast.LENGTH_SHORT).show();
            name.getText().clear();
            number.getText().clear();
            isedit = false;
            Toast.makeText(OpenChat.this, "Try Again.", Toast.LENGTH_SHORT).show();
        }else{
            Edituser.setName(name.getText().toString());
            Edituser.setCountry_code(Integer.parseInt(countrycode));
            Edituser.setPhone_number(Double.parseDouble(phonenumbe));
            Edituser.setIso(locale);
            Edituser.setDatetime(getDatetime());
            new UpdateEntity(entityDAO,Edituser).execute();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "SAVED", Toast.LENGTH_SHORT).show();
            number.setText("");
            name.getText().clear();
        }
    }
    private void checknumber(){
        if(phonenumbe.isEmpty()){
            Toast.makeText(OpenChat.this, "Check Country Code with Phone Number", Toast.LENGTH_SHORT).show();
        }else{

        }
    }

    private void bottomMenu(){
        final BottomSheetDialog bt = new BottomSheetDialog(OpenChat.this,R.style.BottomSheetDialogTheme);
        View vi = LayoutInflater.from(OpenChat.this).inflate(R.layout.bottom_chat_menu,null);
        vi.findViewById(R.id.savenum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new populateDB(instance,identity).execute(value);
                bt.dismiss();
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(0);
                    }
                });
                name.getText().clear();
                number.getText().clear();
            }
        });
        vi.findViewById(R.id.openchat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/"+countrycode+phonenumbe));
                startActivity(browserIntent);
                new populateDB(instance,identity).execute(value);
                bt.dismiss();
                name.getText().clear();
                number.getText().clear();
            }
        });
        bt.setContentView(vi);
        bt.show();
    }

    private void clickitem(final com.example.statussaverWAIG.Entity user, final String temp){
        final BottomSheetDialog bt = new BottomSheetDialog(OpenChat.this,R.style.BottomSheetDialogTheme);
        View vi = LayoutInflater.from(OpenChat.this).inflate(R.layout.bottom_numitem_menu,null);
        vi.findViewById(R.id.removenum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isedit = false;
                bt.dismiss();
                deletenumberdispdialog(user);
            }
        });
        vi.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri u = Uri.parse("tel:" + Uri.parse(temp));
                Intent i = new Intent(Intent.ACTION_DIAL, u);
                try {
                    startActivity(i);
                }
                catch (SecurityException s) {
                    Toast.makeText(OpenChat.this, "We can't", Toast.LENGTH_LONG).show();
                }
            }
        });
        vi.findViewById(R.id.msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", temp, null)));
            }
        });
        vi.findViewById(R.id.copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.dismiss();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Phone Number", temp);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(OpenChat.this, "Phone Number Copied", Toast.LENGTH_SHORT).show();
            }
        });
        vi.findViewById(R.id.editnum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.dismiss();
                Edituser = user;
                if(!user.getName().isEmpty()){
                    name.setText(user.getName());
                }
                number.setText(String.format("%.0f", user.getPhone_number()));
                number.setSelection(number.getText().toString().length());
                countryCodePicker.setCountryForNameCode(user.getIso());
                isedit = true;
                showkeyboard(number);
            }
        });
        vi.findViewById(R.id.sharenum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.dismiss();
                Intent intent = new Intent(Intent.ACTION_SEND);
                String shareBody = "Phone Number : +"+user.getCountry_code()+String.format("%.0f", user.getPhone_number())+"\nName : "+user.getName()+"\nShare By StatusApp\n";
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Share Phone Number");
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent,"Share By Status App"));
            }
        });

        vi.findViewById(R.id.openchat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.dismiss();
                isedit = false;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/"+user.getCountry_code()+String.format("%.0f", user.getPhone_number()).toString()));
                startActivity(browserIntent);
            }
        });
        bt.setContentView(vi);
        bt.show();
    }

    private void deletenumberdispdialog(final com.example.statussaverWAIG.Entity user){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are You Sure");
        builder.setMessage("Delete This Phone Number?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteEntity(entityDAO,user).execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void dispdialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are You Sure");
        builder.setMessage("Delete All Phone Numbers?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteAllEntity(entityDAO).execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showkeyboard(final View view){
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    private void closeKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}