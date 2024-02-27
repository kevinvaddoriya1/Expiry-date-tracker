package com.test.expirydatetracker;

import static android.content.Context.NOTIFICATION_SERVICE;



import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.CountDownTimer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class RecycleProductAdapter extends RecyclerView.Adapter<RecycleProductAdapter.ViewHolder> {
    Context context;
    EditText edit_name,edit_date;
    Button update;
    DBhelper db;

    ArrayList<ProductModel> arrayList;
    private static final String CHANNEL_ID = "Message";
    private static final int NOTIFICATION_ID = 100;
    private DBhelper dBhelper;

    RecycleProductAdapter(Context context, ArrayList<ProductModel> arrayList,DBhelper dBhelper) {
        this.context = context;
        this.arrayList = arrayList;
        this.dBhelper = dBhelper;

    }

    private AdapterView.OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.p_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ProductModel item = arrayList.get(position);
        holder.txtname.setText(item.getName());

        String dateString = item.date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date(); // Set a default date if parsing fails
        }
        long currentTimeMillis = System.currentTimeMillis();
        long targetTimeMillis = date.getTime();
        long timeRemainingMillis = targetTimeMillis - currentTimeMillis;
        new CountDownTimer(timeRemainingMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update the TextView with the remaining time
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                String countdownText = String.format(Locale.getDefault(),
                        "%02dd %02dh %02dm %02ds", days, hours % 24, minutes % 60, seconds % 60);

                holder.txttimer.setText(countdownText);
            }

            public void onFinish() {
                // Countdown finished
                holder.txttimer.setText("Expire this Product");

            }
        }.start();
        holder.llrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,UpdateData.class);
                intent.putExtra("name",item.getName());
                intent.putExtra("date",item.getDate());
                String id = String.valueOf(item.id);
                intent.putExtra("idup",id);
                Log.d("upop", "onClick:"+item.id);
                context.startActivity(intent);
            }
        });
        holder.llrow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDelete(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtname, txttimer;
        LinearLayout llrow;

        public ViewHolder(View iteam) {
            super(iteam);
            txtname = iteam.findViewById(R.id.p_name);
            txttimer = iteam.findViewById(R.id.timer);
            llrow = iteam.findViewById(R.id.llrow);

        }
    }
    private void deleteIteam(int posiotion){
        SQLiteDatabase db = dBhelper.getReadableDatabase();
        long idToDelete = arrayList.get(posiotion).getId();
        String tablename = "product";
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(idToDelete)};

        db.delete(tablename,whereClause,whereArgs);
        db.close();
        arrayList.remove(posiotion);
        notifyItemRemoved(posiotion);
    }
    private void showDelete(int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete item");
        builder.setMessage("Are you delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                deleteIteam(adapterPosition);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

  }
