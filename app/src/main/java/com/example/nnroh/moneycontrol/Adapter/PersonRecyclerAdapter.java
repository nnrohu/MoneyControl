package com.example.nnroh.moneycontrol.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.nnroh.moneycontrol.BottomSheetDialog;
import com.example.nnroh.moneycontrol.R;
import com.example.nnroh.moneycontrol.Data.Person;

import java.util.List;


public class PersonRecyclerAdapter extends RecyclerView.Adapter<PersonRecyclerAdapter.ViewHolder>{

    private Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<Person> mPersonList;
    ColorGenerator mGenerator = ColorGenerator.MATERIAL;

    public PersonRecyclerAdapter(Context context, List<Person> person) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mPersonList = person;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_person, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mPersonList.get(position).getImageUri() != null){
            Glide.with(mContext).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                    .load(mPersonList.get(position).getImageUri()).into(holder.mPersonImage);
        }
        else {
            String letter = String.valueOf(mPersonList.get(position).getFullname().charAt(0));
            TextDrawable drawable = TextDrawable.builder().buildRound(letter,mGenerator.getRandomColor());
            holder.mPersonImage.setImageDrawable(drawable);
        }
        holder.mPersonName.setText(mPersonList.get(position).getFullname());
        holder.mCurrentPersonNumber = mPersonList.get(position).getPhoneNumber();


    }

    @Override
    public int getItemCount() {
        return mPersonList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final ImageView mPersonImage;
        public final TextView mPersonName;
        public String mCurrentPersonNumber;

        public ViewHolder(View itemView) {
            super(itemView);

            mPersonImage = (ImageView) itemView.findViewById(R.id.iv_person_image);
            mPersonName = (TextView) itemView.findViewById(R.id.tv_person_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialog dialog = new BottomSheetDialog();
                    dialog.show(((AppCompatActivity)mContext).getSupportFragmentManager(), dialog.getTag());
//                    Intent intent = new Intent(mContext, PersonDetailsActivity.class);
//                    intent.putExtra(MainActivity.PERSON_NUMBER, mCurrentPersonNumber);
//                    mContext.startActivity(intent);
                }
            });
        }
    }
}