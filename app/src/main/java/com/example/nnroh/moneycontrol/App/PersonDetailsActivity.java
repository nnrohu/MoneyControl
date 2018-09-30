package com.example.nnroh.moneycontrol.App;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.nnroh.moneycontrol.Data.Person;
import com.example.nnroh.moneycontrol.Data.local.DataManager;
import com.example.nnroh.moneycontrol.R;

public class PersonDetailsActivity extends AppCompatActivity {

    private ImageView mPersonImage;
    private DataManager mDataManager;
    private Person mPerson;
    ColorGenerator mGenerator = ColorGenerator.MATERIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra(MainActivity.PERSON_NUMBER);

        mPersonImage = (ImageView) findViewById(R.id.iv_person);

        mDataManager = new DataManager(this);

        mPerson = mDataManager.getPerson(phoneNumber);

        if (mPerson.getImageUri() != null) {
            Glide.with(this).load(mPerson.getImageUri()).into(mPersonImage);
        }else {
            String letter = String.valueOf(mPerson.getFullname().charAt(0));
            TextDrawable drawable = TextDrawable.builder().buildRound(letter.toUpperCase(), mGenerator.getRandomColor());
            mPersonImage.setImageDrawable(drawable);
        }
        CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        layout.setTitle(mPerson.getFullname());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
