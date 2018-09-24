package com.example.nnroh.moneycontrol.Data;

//import com.google.common.base.Strings;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Person implements Parcelable{

    private String mFullname;

    private String mPhoneNumber;

    private String mImageUri;

    private List<Debt> mDebts;


    public Person() {
    }

    public Person(String fullname, String phoneNumber, String imageUri) {
        mFullname = fullname;
        mPhoneNumber = phoneNumber;
        mImageUri = imageUri;

        mDebts = new ArrayList<>();
    }

    public String getFullname() {
        return mFullname;
    }

    public void setFullname(String fullname) {
        mFullname = fullname;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getImageUri() {
        return mImageUri;
    }

    public void setImageUri(String imageUri) {
        mImageUri = imageUri;
    }

    public List<Debt> getDebts() {
        return mDebts;
    }

    public void setDebts(List<Debt> debts) {
        mDebts = debts;
    }

    public void addDebt(Debt debt){
        mDebts.add(debt);
    }

    public Person(Person person) {
        mDebts = person.getDebts();
        mFullname = person.getFullname();
        mPhoneNumber = person.getPhoneNumber();
        mImageUri = person.getImageUri();
    }

//    public boolean isEmpty() {
//        return Strings.isNullOrEmpty(mPhoneNumber)
//                && Strings.isNullOrEmpty(String.valueOf(mFullname));
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mFullname);
        dest.writeString(this.mPhoneNumber);
        dest.writeString(this.mImageUri);
        dest.writeTypedList(this.mDebts);
    }

    protected Person(Parcel in) {
        this.mFullname = in.readString();
        this.mPhoneNumber = in.readString();
        this.mImageUri = in.readString();
        this.mDebts = in.createTypedArrayList(Debt.CREATOR);
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Person person = (Person) o;

        if (!mFullname.equals(person.mFullname)) {
            return false;
        }
        if (!mPhoneNumber.equals(person.mPhoneNumber)) {
            return false;
        }
        if (mImageUri != null ? !mImageUri.equals(person.mImageUri) : person.mImageUri != null) {
            return false;
        }
        return mDebts != null ? mDebts.equals(person.mDebts) : person.mDebts == null;

    }

    @Override
    public int hashCode() {
        int result = mFullname.hashCode();
        result = 31 * result + mPhoneNumber.hashCode();
        result = 31 * result + (mImageUri != null ? mImageUri.hashCode() : 0);
        result = 31 * result + (mDebts != null ? mDebts.hashCode() : 0);
        return result;
    }
}
