package com.gypsee.sdk.Adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.WalletLayoutBinding;
import com.gypsee.sdk.models.WalletTransactionsModel;

public class WalletTransactionsAdapter extends RecyclerView.Adapter<WalletTransactionsAdapter.MyViewHolder> {

    private ArrayList<WalletTransactionsModel> walletTransactionsModelArrayList;
    private Context context;

    public WalletTransactionsAdapter(ArrayList<WalletTransactionsModel> walletTransactionsModelArrayList, Context context) {
        this.walletTransactionsModelArrayList = walletTransactionsModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        WalletLayoutBinding walletLayoutBinding = WalletLayoutBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(walletLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        WalletLayoutBinding walletLayoutBinding = holder.walletLayoutBinding;

        WalletTransactionsModel walletTransactionsList = walletTransactionsModelArrayList.get(position);

        if (walletTransactionsList.getDebitAmount().equals("true")){

            walletLayoutBinding.amount.setTextColor(context.getColor(R.color.redBack));
            walletLayoutBinding.amount.setText("₹"+walletTransactionsList.getAmount());
        } else {
            walletLayoutBinding.amount.setTextColor(context.getColor(R.color.colorPrimary));

            walletLayoutBinding.amount.setText("₹"+walletTransactionsList.getAmount());
        }

        walletLayoutBinding.name.setText(walletTransactionsList.getDescription());

        String dateString = walletTransactionsList.getCreatedOn();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(dateString);
            dateFormat.applyPattern("MMMM dd");
            String convertedDate = dateFormat.format(date);
            walletLayoutBinding.date.setText(convertedDate);
        } catch (Exception e) {
            Log.e("Error occurred: " , e.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return walletTransactionsModelArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        WalletLayoutBinding walletLayoutBinding;

        public MyViewHolder(@NonNull WalletLayoutBinding walletLayoutBinding) {
            super(walletLayoutBinding.getRoot());
            this.walletLayoutBinding = walletLayoutBinding;
        }
    }
}


