package com.catatanku.ontime;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.dataListener {
    // Deklarasi Variable
    private ImageView gambar;
    private Button btnAddNew;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab,fab1,fab2,reward;
    private TextView logo;
    private ImageView ic_cari;
    private SearchView etsearch;


    private RewardedAd mRewardedAd;
    private final String TAG = "MainActivity";

    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    public String descdoes;

    //UID
    private FirebaseAuth mAuth;
    public String UID = mAuth.getInstance().getUid();

    Animation fabOpen,fabClose,rotateForward, rotateBackward;

    boolean isOpen = false; // by default it is false


    //Deklarasi Variable Database Referesi dan Arraylist dengan parameter model
    private DatabaseReference reference;
    DatabaseReference getReference; // ini yg jadi dipake
    private ArrayList<data_kegiatan> dataKegiatan;
    private ArrayList<Object> dataKegiatanFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.datalist);
        gambar = findViewById(R.id.gambar);
        logo = findViewById(R.id.logo);




        // Admob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        // Reward Ads
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad was shown.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.d(TAG, "Ad failed to show.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad was dismissed.");
                                mRewardedAd = null;
                            }
                        });
                    }
                });
        //banner
        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        // Referensi https://www.youtube.com/watch?v=HGQ-8pjI7HM
        // FAB Button
        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        reward = findViewById(R.id.reward);
        //animation
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_foward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        // iklan reward
        reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedAd != null) {
                    Activity activityContext = MainActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Toast.makeText(MainActivity.this,"Anda mendapatkan pahala", Toast.LENGTH_SHORT).show();
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                        }
                    });
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                }
            }
        });
        // fungsi ketika di click floating nya
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });
        // interstial
        setAds();
        // Logout
        // referensi https://technicalsaurya.blogspot.com/2021/04/how-to-implement-admob-interstitial-ads.html

        // menampilkan iklan interstial sebelum logout
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user != null){
                                LogoutBerhasil();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(MainActivity.this, ActivityLogin.class);
                                startActivity(intent);
                            }else{
                                andaHarusLogin();
                                Intent intent = new Intent(MainActivity.this, ActivityLogin.class);
                                startActivity(intent);
                            }
                            mInterstitialAd = null;
                            setAds();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when fullscreen content failed to show.

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            mInterstitialAd = null;

                        }
                    });

                } else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null){
                        LogoutBerhasil();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, ActivityLogin.class);
                        startActivity(intent);
                    }else{
                        andaHarusLogin();
                        Intent intent = new Intent(MainActivity.this, ActivityLogin.class);
                        startActivity(intent);
                    }
                }
            }
        });
        // add task
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NewTaskAct.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // mengatur agar ketika mengklik tombol kembali tidak looping pada activity sebelumnya
                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.datalist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        logo = findViewById(R.id.logo);

        // Search view
        etsearch = findViewById(R.id.etsearch);
        etsearch.setVisibility(View.GONE);
        etsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GetData(query.toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // searchview agar menampilkan data per kata yang cocok
                Query query =getReference.child("MyNote").child("Does").orderByChild("descdoes").startAt(newText).endAt(newText+"\uf8ff");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataKegiatan = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            data_kegiatan kegiatan = snapshot.getValue(data_kegiatan.class);
                            kegiatan.setKey(snapshot.getKey());


                            if(kegiatan.getUID() != null){
                                if(kegiatan.getUID().equals(UID)){
                                    dataKegiatan.add(kegiatan);
                                }
                            }
                        }

                        adapter = new RecyclerViewAdapter(dataKegiatan, MainActivity.this);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                return true;
            }
        });
        ic_cari=findViewById(R.id.ic_cari);
        ic_cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etsearch.setVisibility(View.VISIBLE);
                ic_cari.setVisibility(View.GONE);
                logo.setVisibility(View.GONE);
            }
        });
//        addItemsFromJSON("");
        MyRecyclerView();
        GetData("");

    }

//    private void addItemsFromJSON(String filter) {
//        try {
//
//            String jsonDataString = readJSONDataFromFile();
//            JSONArray jsonArray = new JSONArray(jsonDataString);
//            if (filter.isEmpty() == true) {
//
//                for (int i=0; i<jsonArray.length(); ++i) {
//
//                    JSONObject itemObj = jsonArray.getJSONObject(i);
//
//
//                    String arti = itemObj.getString("arti");
//                    String asma = itemObj.getString("asma");
//                    String audio = itemObj.getString("audio");
//                    String ayat = itemObj.getString("ayat");
//                    String keterangan = itemObj.getString("keterangan");
//                    String nama = itemObj.getString("nama");
//                    String nomor = itemObj.getString("nomor");
//                    String rukuk = itemObj.getString("rukuk");
//                    String type = itemObj.getString("type");
//                    String urut = itemObj.getString("urut");
//
//                    Surat surat = new Surat(arti, asma, audio, ayat, keterangan, nama, nomor, rukuk, type, urut);
//                    dataKegiatan.add(surat);
//
//
//                    //                Toast.makeText(ListQuranActivity.this,arti.toString(),Toast.LENGTH_SHORT).show();
//
//
//                }
//            }else{
//                myReclerViewFilter();
//                for (int i=0; i<jsonArray.length(); ++i) {
//
//                    JSONObject itemObj = jsonArray.getJSONObject(i);
//
//
//                    String arti = itemObj.getString("arti");
//                    String asma = itemObj.getString("asma");
//                    String audio = itemObj.getString("audio");
//                    String ayat = itemObj.getString("ayat");
//                    String keterangan = itemObj.getString("keterangan");
//                    String nama = itemObj.getString("nama");
//                    String nomor = itemObj.getString("nomor");
//                    String rukuk = itemObj.getString("rukuk");
//                    String type = itemObj.getString("type");
//                    String urut = itemObj.getString("urut");
//
//                    if (nama.toLowerCase().contains(filter) || nama.contains(filter) ){
//
//                        Surat surat = new Surat(arti, asma, audio, ayat, keterangan, nama, nomor, rukuk, type, urut);
//                        listSuratFilter.add(surat);
//                    }
//
//
//                    //                Toast.makeText(ListQuranActivity.this,arti.toString(),Toast.LENGTH_SHORT).show();
//
//
//                }
//
//            }
//
//
//
//
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//
//        }
//
//    }

    private void myReclerViewFilter() {
    }


    // Inter ads
    public void setAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

    }

    private void LogoutBerhasil() {
        Toast.makeText(this,"Logout Succesfull", Toast.LENGTH_SHORT).show();

    }

    private void andaHarusLogin() {
        Toast.makeText(this,"You have to login first", Toast.LENGTH_SHORT).show();

    }

    // fungsi animasi pada floating button
   private void animateFab(){
        if (isOpen){
            fab.startAnimation(rotateForward);
            fab.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            reward.startAnimation(fabClose);
            fab.setClickable(false);
            fab2.setClickable(false);
            reward.setClickable(false);
            isOpen=false;
        }else{
            fab.startAnimation(rotateBackward);
            fab.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            reward.startAnimation(fabOpen);
            fab.setClickable(true);
            fab2.setClickable(true);
            reward.setClickable(true);
            isOpen=true;
        }
   }



    // Get data berfungsi mengambil data juga mencari data
    public void GetData(String cari){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        getReference = database.getReference();

        if (cari.isEmpty() == true) {

            MyRecyclerView();
            getReference.child("MyNote").child("Does")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataKegiatan = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                data_kegiatan kegiatan = snapshot.getValue(data_kegiatan.class);
                                kegiatan.setKey(snapshot.getKey());


                                if(kegiatan.getUID() != null){
                                    if(kegiatan.getUID().equals(UID)){
                                        dataKegiatan.add(kegiatan);
                                    }
                                }
                            }
                            // Agar data yang baru diposisi paling atas

                            adapter = new RecyclerViewAdapter(dataKegiatan, MainActivity.this);
                            recyclerView.setAdapter(adapter);

                            int size = dataKegiatan.size();
                            if(size>0){
                                gambar.setVisibility(View.GONE);
                            }else{
                                gambar.setVisibility(View.VISIBLE);
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Task failed to load", Toast.LENGTH_SHORT).show();
                            Log.e("MainActivity",databaseError.getDetails()+" "+databaseError.getMessage());

                        }
                    });
        }else{
            getReference.child("MyNote").child("Does").orderByChild("titledoes").startAt(cari).endAt(cari)
             .addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                     dataKegiatan = new ArrayList<>();
                     for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                         data_kegiatan kegiatan = snapshot.getValue(data_kegiatan.class);
                         kegiatan.setKey(snapshot.getKey());


                         if(kegiatan.getUID() != null){
                             if(kegiatan.getUID().equals(UID)){
                                 dataKegiatan.add(kegiatan);
                             }
                         }
                     }

                     adapter = new RecyclerViewAdapter(dataKegiatan, MainActivity.this);
                     recyclerView.setAdapter(adapter);

                     int size = dataKegiatan.size();
                     if(size>0){
                         gambar.setVisibility(View.GONE);
                     }else{
                         gambar.setVisibility(View.VISIBLE);
                     }

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {
                     Toast.makeText(getApplicationContext(), "Task failed to load", Toast.LENGTH_SHORT).show();
                     Log.e("MainActivity",databaseError.getDetails()+" "+databaseError.getMessage());

                 }
             });
        }
    }

//    private String readJSONDataFromFile() {
//    }


    private void MyRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // Agar data yang ada di recyleview menampilkan data yang terbaru
        // ref https://www.youtube.com/watch?v=OaJ0FVc0_sU
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


    }
    // metod delete data
    @Override
    public void onDeleteData(data_kegiatan data, int position) {
        /*
         * Kode ini akan diambil ketika method onDeleteData
         * dipanggil dari adapter pada RecyleView melalui Interface
         * kemudian akan menghapus data berdasarkan primary key dari data tersebut
         * jika berhasil, maka akan memunculkan toast
         * */
        if(getReference != null){
            getReference.child("MyNote")
                    .child("Does")
                    .child(data.getKey())
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(MainActivity.this,"Task deleted successfully",Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


    }
}