package com.example.nnroh.moneycontrol.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Debt implements Parcelable{

    public static final int DEBT_TYPE_IOWE = 100;
    public static final int DEBT_TYPE_OWED = 200;
    public static final int DEBT_STATUS_PARTIAL = 101;
    public static final int DEBT_STATUS_ACTIVE = 102;

    private final String mId;

    private double mAmount;

    private String mNote;

    private final String mPersonPhoneNumber;

    private long mCreatedDate;

    private long mDueDate;

    private int mDebtType;

    private final int mStatus;

    private List<Payment> mPayments;

    private Person mPerson;

    public Debt(String id, double amount, String note, String personPhoneNumber, long createdDate, long dueDate, int debtType, int status, Person person) {
        mId = id;
        mAmount = amount;
        mNote = note;
        mPersonPhoneNumber = personPhoneNumber;
        mCreatedDate = createdDate;
        mDueDate = dueDate;
        mDebtType = debtType;
        mStatus = status;
        mPerson = person;
    }

    public Person getPerson() {
        return mPerson;
    }

    public void setPerson(Person person) {
        mPerson = person;
    }

    public String getId() {
        return mId;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public String getPersonPhoneNumber() {
        return mPersonPhoneNumber;
    }

    public long getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(long createdDate) {
        mCreatedDate = createdDate;
    }

    public long getDueDate() {
        return mDueDate;
    }

    public void setDueDate(long dueDate) {
        mDueDate = dueDate;
    }

    public int getDebtType() {
        return mDebtType;
    }

    public void setDebtType(int debtType) {
        mDebtType = debtType;
    }

    public int getStatus() {
        return mStatus;
    }

    public List<Payment> getPayments() {
        return mPayments;
    }

    public void setPayments(List<Payment> payments) {
        mPayments = payments;
    }


    public void addPayment(Payment payment) {
        if (mPayments != null) {
            mPayments.add(payment);
        }
    }

    public static class Builder {

        // Required parameters
        private final String mId;
        private final String mPersonPhoneNumber;
        private final long mCreatedDate;
        private final int mDebtType;
        private final int mStatus;
        private final double mAmount;

        // Optional parameters
        private String mNote = "";
        private long mDueDate = 0;
        private List<Payment> mPayments = new ArrayList<>();

        public Builder(String id, String personPhoneNumber, double amount, long createdDate, int debtType, int status) {
            mId = id;
            mAmount = amount;
            mCreatedDate = createdDate;
            mPersonPhoneNumber = personPhoneNumber;
            mDebtType = debtType;
            mStatus = status;
        }

        public Builder note(String note) {
            mNote = note;
            return this;
        }

        public Builder dueDate(long dueDate) {
            mDueDate = dueDate;
            return this;
        }

        public Builder addPayment(Payment payment) {
            mPayments.add(payment);
            return this;
        }

        public Builder payments(List<Payment> payments) {
            mPayments = payments;
            return this;
        }

        public Debt build() {
            return new Debt(this);
        }

    }

    public Debt(Builder builder) {

        mId = builder.mId;
        mPersonPhoneNumber = builder.mPersonPhoneNumber;
        mCreatedDate = builder.mCreatedDate;
        mDebtType = builder.mDebtType;
        mStatus = builder.mStatus;
        mAmount = builder.mAmount;
        mNote = builder.mNote;
        mDueDate = builder.mDueDate;
        mPayments = builder.mPayments;
    }

    public Debt(Debt debt) {

        mId = debt.getId();
        mPersonPhoneNumber = debt.getPersonPhoneNumber();
        mCreatedDate = debt.getCreatedDate();
        mDebtType = debt.getDebtType();
        mStatus = debt.getStatus();
        mAmount = debt.getAmount();
        mNote = debt.getNote();
        mDueDate = debt.getDueDate();
        mPayments = debt.getPayments();
    }

    public boolean isEmpty() {
        return mAmount == 0;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeDouble(this.mAmount);
        dest.writeString(this.mNote);
        dest.writeString(this.mPersonPhoneNumber);
        dest.writeLong(this.mCreatedDate);
        dest.writeLong(this.mDueDate);
        dest.writeInt(this.mDebtType);
        dest.writeInt(this.mStatus);
        dest.writeTypedList(this.mPayments);
    }

    private Debt(Parcel in) {
        this.mId = in.readString();
        this.mAmount = in.readDouble();
        this.mNote = in.readString();
        this.mPersonPhoneNumber = in.readString();
        this.mCreatedDate = in.readLong();
        this.mDueDate = in.readLong();
        this.mDebtType = in.readInt();
        this.mStatus = in.readInt();
        this.mPayments = in.createTypedArrayList(Payment.CREATOR);
    }

    public static final Creator<Debt> CREATOR = new Creator<Debt>() {
        @Override
        public Debt createFromParcel(Parcel source) {
            return new Debt(source);
        }

        @Override
        public Debt[] newArray(int size) {
            return new Debt[size];
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

        Debt debt = (Debt) o;

        if (Double.compare(debt.mAmount, mAmount) != 0) {
            return false;
        }
        if (mCreatedDate != debt.mCreatedDate) {
            return false;
        }
        if (mDueDate != debt.mDueDate) {
            return false;
        }
        if (mDebtType != debt.mDebtType) {
            return false;
        }
        if (mStatus != debt.mStatus) {
            return false;
        }
        if (!mId.equals(debt.mId)) {
            return false;
        }
        if (mNote != null ? !mNote.equals(debt.mNote) : debt.mNote != null) {
            return false;
        }
        if (!mPersonPhoneNumber.equals(debt.mPersonPhoneNumber)) {
            return false;
        }
        return mPayments != null ? mPayments.equals(debt.mPayments) : debt.mPayments == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mId.hashCode();
        temp = Double.doubleToLongBits(mAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (mNote != null ? mNote.hashCode() : 0);
        result = 31 * result + mPersonPhoneNumber.hashCode();
        result = 31 * result + (int) (mCreatedDate ^ (mCreatedDate >>> 32));
        result = 31 * result + (int) (mDueDate ^ (mDueDate >>> 32));
        result = 31 * result + mDebtType;
        result = 31 * result + mStatus;
        result = 31 * result + (mPayments != null ? mPayments.hashCode() : 0);
        return result;
    }
}
