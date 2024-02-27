package com.test.expirydatetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;


public class Account extends Fragment {

    Button logout;
    LinearLayout profile, About, plog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        About = v.findViewById(R.id.ll_about_us);
        profile = v.findViewById(R.id.ll_profile);
        plog = v.findViewById(R.id.ll_logout);
        plog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent goback = new Intent(getActivity(), Login.class);
                startActivity(goback);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intet = new Intent(getActivity(), Profile.class);
                startActivity(intet);
            }
        });
        About.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intet = new Intent(getActivity(), AboutUs.class);
                startActivity(intet);
            }
        });

        return v;
    }
}