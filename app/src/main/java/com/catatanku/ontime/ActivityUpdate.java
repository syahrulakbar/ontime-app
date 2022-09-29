package com.catatanku.ontime;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ActivityUpdate extends AppCompatActivity {
    private EditText  descBaru, dateBaru, infoBaru;
    private Button update;
    private LinearLayout barangBaru;
    private Spinner titleBaru;
    private CheckBox barang1, barang2, barang3;
    private RadioGroup  placeBaru;
    private RadioButton place1, place2;
    private ImageView cancel;
    private DatabaseReference database;
    private String cekTitle,cekDesc, cekDate, cekInfo, cekPlace, cekBarang;

    private int notificationId = 1;

    //UID
    private FirebaseAuth mAuth;
    public String UID = mAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        mAuth = FirebaseAuth.getInstance();


//        getSupportActionBar().setTitle("Update Data"); // jika ada no action bar maka akan eror
        titleBaru = findViewById(R.id.new_title);
        dateBaru = findViewById(R.id.new_date);
        descBaru = findViewById(R.id.new_desc);
        infoBaru = findViewById(R.id.new_info);
        placeBaru = findViewById(R.id.new_place);
        barangBaru = findViewById(R.id.new_barang);
        barang1 = findViewById(R.id.barang1);
        barang2 = findViewById(R.id.barang2);
        barang3 = findViewById(R.id.barang3);
        place1 = findViewById(R.id.place1);
        place2 = findViewById(R.id.place2);
        update = findViewById(R.id.update);

        // tombol cancel
        cancel = findViewById(R.id.ic_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityUpdate.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // mengatur agar ketika mengklik tombol kembali tidak looping pada activity sebelumnya

                startActivity(intent);

            }
        });
        // menampilkan kalender
        final Calendar calendar = Calendar.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        GetData();
        infoBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityUpdate.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" +month+"/"+ year;
                        infoBaru.setText(date);
                    }
                },year, month, day);
                datePickerDialog.show();
            }
        });
       // Time Picker
        dateBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityUpdate.this,AlarmReceiver.class);
                //alarm
                final Calendar calendar = Calendar.getInstance();
                intent.putExtra("notificationId", notificationId);
                intent.putExtra("todo",descBaru.getText().toString());

                PendingIntent alarmIntent = PendingIntent.getBroadcast(ActivityUpdate.this, 0, intent
                        , PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(ActivityUpdate.this, new TimePickerDialog.OnTimeSetListener() {
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
                        Toast.makeText(ActivityUpdate.this,"Alarm set",Toast.LENGTH_SHORT).show();

                        if(hourOfDay> 12){
                            dateBaru.setText(hourOfDay + ":" + minutes +" PM");
                        }else {
                            dateBaru.setText(hourOfDay + ":" + minutes +" AM");
                        }
                    }
                }, 0, 0, false);
                timePickerDialog.show();
            }
        });
        // tombol update
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekTitle = titleBaru.getSelectedItem().toString();
                cekDate = dateBaru.getText().toString();
                cekDesc = descBaru.getText().toString();
                cekInfo = infoBaru.getText().toString();
                // Metod Radio Button
                String m1 = place1.getText().toString();
                String m2 = place1.getText().toString();
                if(place1.isChecked()){
                    cekPlace = place1.getText().toString();
                }else{
                    cekPlace = place2.getText().toString();
                }
                // Logic checkbox
                if(barang1.isChecked() && !barang2.isChecked()){
                    cekBarang = barang1.getText().toString();

                }else if(barang2.isChecked() && !barang1.isChecked()){
                    cekBarang = barang2.getText().toString();

                }else if(barang2.isChecked() && barang1.isChecked()  && !barang3.isChecked()){
                    cekBarang = barang3.getText().toString();

                }


                if(isEmpty(cekTitle) && isEmpty(cekDate) && isEmpty(cekDesc)&& isEmpty(cekInfo)){
                    Toast.makeText(ActivityUpdate.this,"Data tidak boleh ada yang kosong",Toast.LENGTH_SHORT).show();
                }else{
                    data_kegiatan setKegiatan = new data_kegiatan();
                    setKegiatan.setTitledoes(titleBaru.getSelectedItem().toString());
                    setKegiatan.setDatedoes(dateBaru.getText().toString());
                    setKegiatan.setDescdoes(descBaru.getText().toString());
                    setKegiatan.setInfodoes(infoBaru.getText().toString());
                    setKegiatan.setUID(UID);
                    // Rb
                    if(place1.isChecked()){
                        setKegiatan.setPlacedoes(place1.getText().toString());
                    }else{
                        setKegiatan.setPlacedoes(place2.getText().toString());
                    }
                    // Logic checkbox
                    if(barang1.isChecked() && !barang2.isChecked()){
                        setKegiatan.setBarangdoes(barang1.getText().toString());

                    }else if(barang2.isChecked() && !barang1.isChecked()){
                        setKegiatan.setBarangdoes(barang2.getText().toString());

                    }else if(barang2.isChecked() && barang1.isChecked()  && !barang3.isChecked()){
                        setKegiatan.setBarangdoes(barang3.getText().toString());

                    }
                    updateKegiatan(setKegiatan);
                }
            }
        });
    }
    private boolean isEmpty(String s){
        return TextUtils.isEmpty(s);
    }
    private void GetData() {
        final String getTitledoes = getIntent().getExtras().getString("dataTitledoes");
        final String getDescdoes = getIntent().getExtras().getString("dataDescdoes");
        final String getDatedoes = getIntent().getExtras().getString("dataDatedoes");
        final String getInfodoes = getIntent().getExtras().getString("dataInfodoes");
        final String getPlacedoes = getIntent().getExtras().getString("dataPlacedoes");
        final String getBarangdoes = getIntent().getExtras().getString("dataBarangdoes");

        // Spinner
        if (getTitledoes.indexOf("tug") != -1)
        {
            titleBaru.setSelection(0);
        }else if(getTitledoes.indexOf("lib") != -1){
            titleBaru.setSelection(1);
        }else if(getTitledoes.indexOf("acar") != -1){
            titleBaru.setSelection(2);
        }
        //CB
        if (getBarangdoes.indexOf("Laptop") != -1)
        {
            barang1.setChecked(true);
            barang2.setChecked(false);
            barang3.setChecked(false);
        }else if(getBarangdoes.indexOf("Smartphone") != -1){
            barang1.setChecked(false);
            barang2.setChecked(true);
            barang3.setChecked(false);
        }else if(getBarangdoes.indexOf("lap") != -1){
            barang1.setChecked(true);
            barang2.setChecked(true);
            barang3.setChecked(false);
        }

        // RB
        if (getPlacedoes.indexOf("IND") != -1){
            place1.setChecked(true);
            place2.setChecked(false);
        }else if (getPlacedoes.indexOf("OUT") != -1){
            place2.setChecked(true);
            place1.setChecked(false);
        }
        titleBaru.getSelectedItem().toString();
        dateBaru.setText(getDatedoes);
        descBaru.setText(getDescdoes);
        infoBaru.setText(getInfodoes);

        String m1 = place1.getText().toString();
        String m2 = place1.getText().toString();
        if(place1.isChecked()){
            cekPlace = place1.getText().toString();
        }else{
            cekPlace = place2.getText().toString();
        }
        // Logic checkbox
        String m3 = barang1.getText().toString();
        String m4 = barang2.getText().toString();
        String m5 = barang3.getText().toString();
        if(barang1.isChecked() && !barang2.isChecked()){
            cekBarang = barang1.getText().toString();

        }else if(barang2.isChecked() && !barang1.isChecked()){
            cekBarang = barang2.getText().toString();

        }else if(barang2.isChecked() && barang1.isChecked()  && !barang3.isChecked()){
            cekBarang = barang3.getText().toString();

        }

    }
    private void updateKegiatan(data_kegiatan kegiatan){
        String getKey = getIntent().getExtras().getString("getPrimaryKey");
        database.child("MyNote")
                .child("Does")
                .child(getKey)
                .setValue(kegiatan)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        titleBaru.getSelectedItem().toString();
                        descBaru.setText("");
                        dateBaru.setText("");
                        infoBaru.setText("");
                        String m1 = place1.getText().toString();
                        String m2 = place1.getText().toString();
                        if(place1.isChecked()){
                            cekPlace = place1.getText().toString();
                        }else{
                            cekPlace = place2.getText().toString();
                        }
                        // Logic checkbox
                        String m3 = barang1.getText().toString();
                        String m4 = barang2.getText().toString();
                        String m5 = barang3.getText().toString();
                        if(barang1.isChecked() && !barang2.isChecked()){
                            cekBarang = barang1.getText().toString();

                        }else if(barang2.isChecked() && !barang1.isChecked()){
                            cekBarang = barang2.getText().toString();

                        }else if(barang2.isChecked() && barang1.isChecked()  && !barang3.isChecked()){
                            cekBarang = barang3.getText().toString();

                        }
                        Toast.makeText(ActivityUpdate.this,"Task edited successfully",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}