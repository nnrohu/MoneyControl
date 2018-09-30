package com.example.nnroh.moneycontrol.Contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nnroh.moneycontrol.R;

import java.util.ArrayList;


public class ContactsAdapter extends ArrayAdapter implements Filterable{

    ColorGenerator mGenerator = ColorGenerator.MATERIAL;

    ArrayList<Contact> mContacts, tempContacts;
    ValueFilter mValueFilter;

    public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
        this.mContacts = contacts;
        this.tempContacts = contacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item
        Contact contact = (Contact) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.contacts_list_item, parent, false);
        }
        // Populate the data into the template view using the data object
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        ImageView ivImage = (ImageView) view.findViewById(R.id.ivImage);
        TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
        tvName.setText(contact.name);

        String letter = String.valueOf(contact.name.charAt(0));

        //        Create a new TextDrawable for our image's background
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(letter.toUpperCase(), mGenerator.getRandomColor());


        String imageUri = contact.photo;
        if (imageUri != null) {
            Glide.with(getContext().getApplicationContext())
                    .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                    .load(imageUri).into(ivImage);
        }
        else {
            ivImage.setImageDrawable(drawable);
        }

        tvPhone.setText("");

        if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
            tvPhone.setText(contact.numbers.get(0).number);
        }
        return view;
    }

    @Override
    public int getCount() {
        return mContacts.size();
    }

    @Override
    public Object getItem(int i) {
        return mContacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Filter getFilter() {
        if (mValueFilter == null) {
            mValueFilter = new ValueFilter();
        }
        return mValueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Contact> filterList = new ArrayList<Contact>();
                for (int i = 0; i < tempContacts.size(); i++) {
                    if ((tempContacts.get(i).name.toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {

                        Contact contact =
                                new Contact(tempContacts.get(i).id,
                                        tempContacts.get(i).photo,
                                        tempContacts.get(i).name,
                                        tempContacts.get(i).numbers);

                        filterList.add(contact);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = tempContacts.size();
                results.values = tempContacts;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mContacts = (ArrayList<Contact>) results.values;
            notifyDataSetChanged();
        }

    }
}
