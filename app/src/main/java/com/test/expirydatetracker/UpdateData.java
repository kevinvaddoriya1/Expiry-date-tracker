package com.test.expirydatetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.ListFragment;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateData extends AppCompatActivity {
public static String isUpdate = "no";
    EditText ename,edate;
    Button update;
    final Calendar mycalendar = Calendar.getInstance();
    String day,month,year;
    DBhelper dBhelper = new DBhelper(UpdateData.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ddata);
        String name = getIntent().getStringExtra("name");
       String date =  getIntent().getStringExtra("date");
       String id = getIntent().getStringExtra("idup");

       date = date.substring(0,10);
       day = date.substring(0,2);
       month = date.substring(3,5);
       year = date.substring(6,10);
       ename = findViewById(R.id.edit_name);
       edate = findViewById(R.id.edit_date);
       update = findViewById(R.id.update);
       ename.setText(name);
       edate.setText(date);
        edate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fdate = edate.getText().toString();

                String name = ename.getText().toString();
                SimpleDateFormat formatter= new SimpleDateFormat(" HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());

                String newValue1 = ename.getText().toString();

                Log.d("datecheck", "onClick: "+fdate+(formatter.format(date)).toString());

                if(fdate.isEmpty() || name.isEmpty()){
                    Toast.makeText(UpdateData.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }else {



                    dBhelper.updateNew(UpdateData.this,newValue1,fdate+(formatter.format(date)).toString(),id);
                    ename.setText("");
                    edate.setText("");
                    Intent intent = new Intent(UpdateData.this, Home.class);
                    isUpdate = "yes";
                    startActivity(intent);
                }

            }
        });
    }
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day));

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(1, year);
                calendar.set(2, monthOfYear);
                calendar.set(5, dayOfMonth);
                edate.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
            }
        }, calendar.get(1), calendar.get(2), calendar.get(5));

        datePickerDialog.show();
    }
}