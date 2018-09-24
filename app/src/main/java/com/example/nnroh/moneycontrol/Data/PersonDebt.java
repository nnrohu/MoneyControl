package com.example.nnroh.moneycontrol.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class PersonDebt implements Parcelable {

    private Person mPerson;

    private Debt mDebt;

    public PersonDebt(Person person, Debt debt) {
        mPerson = new Person(person);
        mDebt = new Debt(debt);
    }

    public Person getPerson() {
        return mPerson;
    }

    public void setPerson(Person person) {
        mPerson = person;
    }

    public Debt getDebt() {
        return mDebt;
    }

    public void setDebt(Debt debt) {
        mDebt = debt;
    }


    public List<Payment> getPayments() {
        return mDebt.getPayments();
    }

    public void addPayment(Payment payment) {
        if (mDebt.getPayments() != null) {
            mDebt.getPayments().add(payment);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersonDebt that = (PersonDebt) o;

        if (!mPerson.equals(that.mPerson)) {
            return false;
        }
        return mDebt.equals(that.mDebt);

    }

    @Override
    public int hashCode() {
        int result = mPerson.hashCode();
        result = 31 * result + mDebt.hashCode();
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mPerson, flags);
        dest.writeParcelable(this.mDebt, flags);
    }

    protected PersonDebt(Parcel in) {
        this.mPerson = in.readParcelable(Person.class.getClassLoader());
        this.mDebt = in.readParcelable(Debt.class.getClassLoader());
    }

    public static final Creator<PersonDebt> CREATOR = new Creator<PersonDebt>() {
        @Override
        public PersonDebt createFromParcel(Parcel source) {
            return new PersonDebt(source);
        }

        @Override
        public PersonDebt[] newArray(int size) {
            return new PersonDebt[size];
        }
    };
}
