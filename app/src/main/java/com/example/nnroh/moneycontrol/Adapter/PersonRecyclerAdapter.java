package com.example.nnroh.moneycontrol.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nnroh.moneycontrol.App.MainActivity;
import com.example.nnroh.moneycontrol.App.PersonDetailsActivity;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PersonsEntry;
import com.example.nnroh.moneycontrol.R;

import static com.example.nnroh.moneycontrol.App.AddPersonActivity.PERSON_PHOTO;


public class PersonRecyclerAdapter extends RecyclerView.Adapter<PersonRecyclerAdapter.ViewHolder>{

    private Context mContext;
    private final LayoutInflater mLayoutInflater;
    private Cursor mCursor;
    private ColorGenerator mGenerator = ColorGenerator.MATERIAL;
    private int mPersonIdPos;
    private int mPersonImagePos;
    private int mPersonNamePos;
    private int mPersonPhonePos;

    public PersonRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCursor = cursor;
        populateColumnPosition();
    }

    private void populateColumnPosition() {
        if (mCursor == null)
            return;
        //get column position from cursor
        mPersonImagePos = mCursor.getColumnIndex(PersonsEntry.COLUMN_IMAGE_URI);
        mPersonNamePos = mCursor.getColumnIndex(PersonsEntry.COLUMN_NAME);
        mPersonPhonePos = mCursor.getColumnIndex(PersonsEntry.COLUMN_PHONE_NO);
    }

    public void changeCursour(Cursor cursor){
        if (mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPosition();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_person, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String personName = mCursor.getString(mPersonNamePos);
        String personNumber = mCursor.getString(mPersonPhonePos);
        String personImage = mCursor.getString(mPersonImagePos);

        if (personImage != null){
            Glide.with(mContext).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                    .load(personImage).into(holder.mPersonImage);
            holder.mCurrentPersonImage = personImage;
        }
        else {
            String letter = String.valueOf(personName.charAt(0));
            TextDrawable drawable = TextDrawable.builder().buildRound(letter.toUpperCase(),mGenerator.getRandomColor());
            holder.mPersonImage.setImageDrawable(drawable);
        }
        holder.mPersonName.setText(personName);
        holder.mCurrentPersonNumber = personNumber;
        holder.mCurrentPersonName = personName;


    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

     public class ViewHolder extends RecyclerView.ViewHolder{

        public final ImageView mPersonImage;
        public final TextView mPersonName;
        public String mCurrentPersonNumber;
        public String mCurrentPersonName;
        public String mCurrentPersonImage;

        public ViewHolder(View itemView) {
            super(itemView);

            mPersonImage = (ImageView) itemView.findViewById(R.id.iv_person_image);
            mPersonName = (TextView) itemView.findViewById(R.id.tv_person_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PersonDetailsActivity.class);
                    intent.putExtra(MainActivity.PERSON_NUMBER, mCurrentPersonNumber);
                    intent.putExtra(MainActivity.PERSON_NAME, mCurrentPersonName);
                    intent.putExtra(PERSON_PHOTO, mCurrentPersonImage);
                    mContext.startActivity(intent);

//                    Bundle bundle = new Bundle();
//                    bundle.putString(MainActivity.PERSON_NUMBER, mCurrentPersonNumber);
//                    bundle.putString(MainActivity.PERSON_NAME, mCurrentPersonName);
//                    bundle.putString(MainActivity.PERSON_IMAGE, mCurrentPersonImage);
//                    PersonDetailsDialogFragment dialogFragment = new PersonDetailsDialogFragment();
//                    dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(), dialogFragment.getTag());
//                    dialogFragment.setArguments(bundle);
                }
            });
        }
    }
}