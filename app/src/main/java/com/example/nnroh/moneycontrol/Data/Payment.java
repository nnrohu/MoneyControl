package com.example.nnroh.moneycontrol.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Payment implements Parcelable {

    public static final int PAYMENT_ACTION_DEBT_INCREASE = 100;
    public static final int PAYMENT_ACTION_DEBT_DECREASE = 200;
    public static final int PAYMENT_ACTION_DEBT_DONT_CHANGE = 300;

    private String mId;

    private double mAmount;

    private String mDebtId;

    private long mDateEntered;

    private String mNote;

    private int mAction;

    private String mPersonPhoneNo;

    private Debt mDebt;

    public Payment(String id, double amount, String debtId, long dateEntered, String note, int action, String personPhoneNo) {
        mId = id;
        mAmount = amount;
        mDebtId = debtId;
        mDateEntered = dateEntered;
        mNote = note;
        mAction = action;
        mPersonPhoneNo = personPhoneNo;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }

    public String getDebtId() {
        return mDebtId;
    }

    public void setDebtId(String debtId) {
        mDebtId = debtId;
    }

    public long getDateEntered() {
        return mDateEntered;
    }

    public void setDateEntered(long dateEntered) {
        mDateEntered = dateEntered;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public int getAction() {
        return mAction;
    }

    public void setAction(int action) {
        mAction = action;
    }

    public String getPersonPhoneNo() {
        return mPersonPhoneNo;
    }

    public void setPersonPhoneNo(String personPhoneNo) {
        mPersonPhoneNo = personPhoneNo;
    }


    public static class Builder {

        // required parameters
        private String mId;
        private double mAmount;
        private String mDebtId;
        private long mDateEntered;
        private String mNote;
        private int mAction;
        private String mPersonPhoneNumber;

        public Builder id(String id) {
            mId = id;
            return this;
        }

        public Builder amount(double amount) {
            mAmount = amount;
            return this;
        }

        public Builder debtId(String debtId) {
            mDebtId = debtId;
            return this;
        }

        public Builder dateEntered(long dateEntered) {
            mDateEntered = dateEntered;
            return this;
        }

        public Builder note(String note) {
            mNote = note;
            return this;
        }

        public Builder action(int action) {
            mAction = action;
            return this;
        }

        public Builder personPhoneNumber(String personPhoneNumber) {
            mPersonPhoneNumber = personPhoneNumber;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }

    public Payment(Builder builder) {

        mId = builder.mId;
        mAmount = builder.mAmount;
        mAction = builder.mAction;
        mNote = builder.mNote;
        mDateEntered = builder.mDateEntered;
        mDebtId = builder.mDebtId;
        mPersonPhoneNo = builder.mPersonPhoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mDebtId);
        dest.writeLong(this.mDateEntered);
        dest.writeString(this.mNote);
        dest.writeInt(this.mAction);
        dest.writeDouble(this.mAmount);
        dest.writeString(this.mPersonPhoneNo);
    }

    private Payment(Parcel in) {
        this.mId = in.readString();
        this.mDebtId = in.readString();
        this.mDateEntered = in.readLong();
        this.mNote = in.readString();
        this.mAction = in.readInt();
        this.mAmount = in.readDouble();
        this.mPersonPhoneNo = in.readString();
    }

    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel source) {
            return new Payment(source);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
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

        Payment payment = (Payment) o;

        if (Double.compare(payment.mAmount, mAmount) != 0) {
            return false;
        }
        if (mDateEntered != payment.mDateEntered) {
            return false;
        }
        if (mAction != payment.mAction) {
            return false;
        }
        if (!mId.equals(payment.mId)) {
            return false;
        }
        if (!mDebtId.equals(payment.mDebtId)) {
            return false;
        }
        if (!mNote.equals(payment.mNote)) {
            return false;
        }
        return mPersonPhoneNo.equals(payment.mPersonPhoneNo);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mId.hashCode();
        temp = Double.doubleToLongBits(mAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + mDebtId.hashCode();
        result = 31 * result + (int) (mDateEntered ^ (mDateEntered >>> 32));
        result = 31 * result + mNote.hashCode();
        result = 31 * result + mAction;
        result = 31 * result + mPersonPhoneNo.hashCode();
        return result;
    }

}
