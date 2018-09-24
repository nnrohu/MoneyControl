package com.example.nnroh.moneycontrol;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.nnroh.moneycontrol.App.MainActivity;
import com.example.nnroh.moneycontrol.Data.Person;
import com.example.nnroh.moneycontrol.Data.local.DataManager;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private String mPersonName;
    ColorGenerator mGenerator = ColorGenerator.MATERIAL;
    private ImageView mPersonImage;
    private Person mPerson;
    private String mPhoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if (!bundle.isEmpty()) {
            mPhoneNumber = bundle.getString(MainActivity.PERSON_NUMBER);
        }
        DataManager dm = new DataManager(getContext());
        mPerson = dm.getPerson(mPhoneNumber);
        mPersonName = mPerson.getFullname();

    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View v = View.inflate(getActivity(), R.layout.bottomsheet, null);
        dialog.setContentView(v);
        v.setFitsSystemWindows(true);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.bottomsheet_toolbar);
        toolbar.inflateMenu(R.menu.menu_details);
        toolbar.setTitle(mPersonName);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });

        mPersonImage = (ImageView) v.findViewById(R.id.iv_person);
        setImage();

    }

    private void setImage() {
        if (mPerson.getImageUri() != null){
            mPersonImage.setImageURI(Uri.parse(mPerson.getImageUri()));
           // Glide.with(this).load(mPerson.getImageUri()).into(mPersonImage);
        }else {
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(String.valueOf(mPerson.getFullname().charAt(0)), R.color.colorAccent);
            mPersonImage.setImageDrawable(drawable);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
