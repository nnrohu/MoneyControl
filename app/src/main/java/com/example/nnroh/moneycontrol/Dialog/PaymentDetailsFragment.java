package com.example.nnroh.moneycontrol.Dialog;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nnroh.moneycontrol.Adapter.PaymentsOfDebtRecyclerAdapter;
import com.example.nnroh.moneycontrol.Data.Payment;
import com.example.nnroh.moneycontrol.Data.local.DataManager;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PaymentsEntry;
import com.example.nnroh.moneycontrol.R;

import java.util.List;


public class PaymentDetailsFragment extends BottomSheetDialogFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    PaymentsOfDebtRecyclerAdapter mPaymentsOfDebtRecyclerAdapter;
    RecyclerView mPaymentDetailsOfDebt;
    private String mDebtId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.payment_details_of_debt, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        Bundle bundle = getArguments();
        mDebtId = bundle.getString("debtId");

        mPaymentsOfDebtRecyclerAdapter = new PaymentsOfDebtRecyclerAdapter(getContext(), null);
        mPaymentDetailsOfDebt =  view.findViewById(R.id.payment_details_for_debt);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mPaymentDetailsOfDebt.setLayoutManager(layoutManager);
        mPaymentDetailsOfDebt.setAdapter(mPaymentsOfDebtRecyclerAdapter);

        getLoaderManager().initLoader(1, null, this);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri uri = PaymentsEntry.CONTENT_URI;
        String[] paymentColumns = {
                PaymentsEntry.COLUMN_AMOUNT,
                PaymentsEntry.COLUMN_NOTE,
                PaymentsEntry.COLUMN_DATE_ENTERED};
        String selection = PaymentsEntry.COLUMN_DEBT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(mDebtId)};
        return new android.support.v4.content.CursorLoader(getContext(), uri, paymentColumns, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == 1)
            mPaymentsOfDebtRecyclerAdapter.changeCursour(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == 1)
            mPaymentsOfDebtRecyclerAdapter.changeCursour(null);
    }
}
