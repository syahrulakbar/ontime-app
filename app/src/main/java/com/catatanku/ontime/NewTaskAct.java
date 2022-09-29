package com.catatanku.ontime;




import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;

import static android.text.TextUtils.isEmpty;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.catatanku.ontime.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class NewTaskAct extends AppCompatActivity {

    private TextView titlepage, addtitle, adddesc, adddate, addinfo;
    private EditText  descdoes,  infodoes, datedoes;
    private RadioGroup placedoes;
    private Spinner titledoes;
    private LinearLayout barangdoes;
    private CheckBox barang1, barang2, barang3;
    private RadioButton place1, place2;
    private Button btnSaveTask, btnCancel;
    private String getTitledoes,getDatedoes,getDescdoes, getInfodoes, getPlacedoes, getBarangdoes, getUID;
    private ImageView ic_cancel;
    DatabaseReference getReference;
    DatePickerDialog.OnDateSetListener setListener;
    Integer doesNum = new Random().nextInt();
    int year,month,day;
    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker timePicker;
    private int jam, menit;
    private int notificationId = 1;
    //UID
    private FirebaseAuth mAuth;
    public String UID = mAuth.getInstance().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);



//        cobaAlarm();
        mAuth = FirebaseAuth.getInstance();



        titlepage = findViewById(R.id.titlepage);
        ic_cancel = findViewById(R.id.ic_cancel);

        addtitle = findViewById(R.id.addtitle);
        adddesc = findViewById(R.id.adddesc);
        adddate = findViewById(R.id.adddate);
        addinfo = findViewById(R.id.addinfo);

        infodoes  = findViewById(R.id.infodoes);
        titledoes = findViewById(R.id.titledoes);
        descdoes = findViewById(R.id.descdoes);
        datedoes = findViewById(R.id.datedoes);
        place1 = findViewById(R.id.place1);
        place2 = findViewById(R.id.place2);
        placedoes = findViewById(R.id.placedoes);
        


        // Check Box
        barang1 = findViewById(R.id.barang1);
        barang2 = findViewById(R.id.barang2);
        barang3 = findViewById(R.id.barang3);
        barangdoes = findViewById(R.id.barangdoes);

        btnSaveTask = findViewById(R.id.btnSaveTask);
        // Time Picker
        datedoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //alarm
                Intent intent = new Intent(NewTaskAct.this,AlarmReceiver.class);
                final Calendar calendar = Calendar.getInstance();
                intent.putExtra("notificationId", notificationId);
                intent.putExtra("todo",descdoes.getText().toString());

                PendingIntent alarmIntent = PendingIntent.getBroadcast(NewTaskAct.this, 0, intent
                        , PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(NewTaskAct.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        int hour = timePicker.getCurrentHour();
                        int minute = timePicker.getCurrentMinute();



                        // Create TIme
                        Calendar startTime = Calendar.getInstance();
                        startTime.set(Calendar.HOUR_OF_DAY, hour);
                        startTime.set(Calendar.MINUTE, minute);
                        startTime.set(Calendar.SECOND, 0);
                        long alarmStartTime = startTime.getTimeInMillis();

                        //Set Alarm.

                        alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);
                        Toast.makeText(NewTaskAct.this,"Alarm set",Toast.LENGTH_SHORT).show();

                        if(hourOfDay> 12){
                            datedoes.setText(hourOfDay + ":" + minutes +" PM");
                        }else {
                            datedoes.setText(hourOfDay + ":" + minutes +" AM");
                        }
                    }
                }, 0, 0, false);
                timePickerDialog.show();
                createNotificationChannel();

            }
        });
        // Calender
        final Calendar calendar = Calendar.getInstance();
        infodoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewTaskAct.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                        int tahun = calendar.get(Calendar.YEAR);
//                        int bulan = calendar.get(Calendar.MONTH);
//                        int hari = calendar.get(Calendar.DAY_OF_MONTH);
//
//                        Calendar startTime = Calendar.getInstance();
//                        startTime.set(Calendar.YEAR, tahun);
//                        startTime.set(Calendar.MONTH, bulan);
//                        startTime.set(Calendar.DAY_OF_MONTH, hari);
                        month = month + 1;
                        String date = day + "/" +month+"/"+ year;
                        infodoes.setText(date);
                    }
                },year, month, day);
                datePickerDialog.show();


            }
        });
        // Tombol Cancel

        ic_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewTaskAct.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // mengatur agar ketika mengklik tombol kembali tidak looping pada activity sebelumnya
                startActivity(intent);
            }
        });
        // Tombol Save
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        getReference = database.getReference();
        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Logic Radio button
                String m1 = place1.getText().toString();
                String m2 = place2.getText().toString();
                if(place1.isChecked()){
                    getPlacedoes = place1.getText().toString();
                }else{
                    getPlacedoes = place2.getText().toString();
                }
                getTitledoes = titledoes.getSelectedItem().toString();
                getDatedoes = datedoes.getText().toString();
                getDescdoes = descdoes.getText().toString();
                getInfodoes = infodoes.getText().toString();
                // Logic checkbox
                String m3 = barang1.getText().toString();
                String m4 = barang2.getText().toString();
                String m5 = barang3.getText().toString();
                if(barang1.isChecked() && !barang2.isChecked()){
                    getBarangdoes = barang1.getText().toString();

                }else if(barang2.isChecked()  && !barang1.isChecked()){
                    getBarangdoes = barang2.getText().toString();

                }else if(barang2.isChecked() && barang1.isChecked() && !barang3.isChecked()){
                    getBarangdoes = barang3.getText().toString();

                }



                Intent intent = new Intent(NewTaskAct.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // mengatur agar ketika mengklik tombol kembali tidak looping pada activity sebelumnya
                startActivity(intent);
                checkUser();
            }
        });

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

//    private void setTimer() {
//        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        Intent intent = new Intent(this,AlarmReceiver.class);
//
//        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY,pendingIntent);
//    }

    private void cobaAlarm(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(this.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this,null);
            CharSequence name = "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid",name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager =getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }else{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

    }




    private void checkUser() {
        if(isEmpty(getTitledoes) && isEmpty(getDatedoes) && isEmpty(getDescdoes)&& isEmpty(getInfodoes)&& isEmpty(getPlacedoes)&& isEmpty(getBarangdoes)){
            Toast.makeText(NewTaskAct.this,"Task cannot be empty",Toast.LENGTH_SHORT).show();
        } else{
            getReference.child("MyNote").child("Does").push()
                    .setValue(new data_kegiatan(getTitledoes, getDatedoes, getDescdoes, getInfodoes, getPlacedoes, getBarangdoes, UID))
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            titledoes.getSelectedItem().toString();
                            datedoes.setText("");
                            descdoes.setText("");
                            infodoes.setText("");
                            // metod radio button
                            String m1 = place1.getText().toString();
                            String m2 = place1.getText().toString();
                            if(place1.isChecked()){
                                getPlacedoes = place1.getText().toString();
                            }else{
                                getPlacedoes = place2.getText().toString();
                            }
                            // Logic checkbox
                            String m3 = barang1.getText().toString();
                            String m4 = barang2.getText().toString();
                            String m5 = barang3.getText().toString();
                            if(barang1.isChecked() && !barang2.isChecked()){
                                getBarangdoes = barang1.getText().toString();

                            }else if(barang2.isChecked() && !barang1.isChecked()){
                                getBarangdoes = barang2.getText().toString();

                            }else if(barang2.isChecked() && barang1.isChecked()  && !barang3.isChecked()){
                                getBarangdoes = barang3.getText().toString();

                            }
                            Toast.makeText(NewTaskAct.this,"Successfully Added Task", Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
}