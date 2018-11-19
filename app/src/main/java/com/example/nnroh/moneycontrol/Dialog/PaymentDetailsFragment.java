package com.example.nnroh.moneycontrol.Dialog;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nnroh.moneycontrol.Adapter.PaymentsOfDebtRecyclerAdapter;
import com.example.nnroh.moneycontrol.Data.local.DebtsContract.PaymentsEntry;
import com.example.nnroh.moneycontrol.R;


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
        return new CursorLoader(getContext(), uri, paymentColumns, selection, selectionArgs, null);
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
