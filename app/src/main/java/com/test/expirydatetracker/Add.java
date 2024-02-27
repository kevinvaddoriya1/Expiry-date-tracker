package com.test.expirydatetracker;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;


public class Add extends Fragment {
    EditText product_name,future_Date;
    Button add;
    DBhelper dBhelper;
    final Calendar mycalendar = Calendar.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add, container, false);


        product_name = v.findViewById(R.id.product_name);
        future_Date = v.findViewById(R.id.future_date);
        add = v.findViewById(R.id.add);
        future_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        dBhelper = new DBhelper(getContext());
        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String fdate = future_Date.getText().toString();
                String name = product_name.getText().toString();
                if(fdate.isEmpty() || name.isEmpty()){
                    Toast.makeText(getContext(), "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat formatter= new SimpleDateFormat(" HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                dBhelper.addNew(name,fdate+(formatter.format(date)).toString());
                Toast.makeText(getContext(),"Product has been added..",Toast.LENGTH_SHORT).show();
                product_name.setText("");
                future_Date.setText("");
                Intent intent = new Intent(getActivity(), Home.class);
                UpdateData.isUpdate = "yes";
                startActivity(intent);


            }
        });

        ArrayList<ProductModel> arr = dBhelper.fetchdata();

        return v;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
//        calendar.set(2000, 0, 1);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(1, year);
                calendar.set(2, monthOfYear);
                calendar.set(5, dayOfMonth);
                future_Date.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
            }
        }, calendar.get(1), calendar.get(2), calendar.get(5));

        datePickerDialog.show();
    }
}