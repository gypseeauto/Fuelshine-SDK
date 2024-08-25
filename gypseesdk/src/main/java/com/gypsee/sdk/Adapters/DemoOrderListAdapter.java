package com.gypsee.sdk.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.fragments.DemoProductPageFragment;
import com.gypsee.sdk.models.MyOrderModel;


public class DemoOrderListAdapter extends RecyclerView.Adapter<DemoOrderListAdapter.ViewHolder>{


    private ArrayList<MyOrderModel> orderList;
    private Context context;
    private FragmentActivity activity;

    public DemoOrderListAdapter(ArrayList<MyOrderModel> orderList, Context context, FragmentActivity activity) {
        this.orderList = orderList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_order_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.activationCode.setText(orderList.get(position).getCardNumber());
        holder.copyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copied to clipboard", "DEMO CODE");
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        holder.expiry.setText("Expiry: " + orderList.get(position).getExpiry());
        holder.errorInfo.setText("Info: " + orderList.get(position).getErrorInfo());
        holder.cardPin.setText(Html.fromHtml("Card Pin: <b>" + orderList.get(position).getCardPin() + "</b>"));
        holder.name.setText(orderList.get(position).getName());
        holder.status.setText(Html.fromHtml("Status: <b>" + orderList.get(position).getStatus() + "</b>"));

        Glide.with(context)
                .load(context.getDrawable(R.drawable.ic_subscribe_now_icon))
                .placeholder(R.drawable.ic_image_placeholder_new)
                .error(R.drawable.ic_broken_image_placeholder_new)
                .into(holder.image);
        holder.tapToActivate.setVisibility(View.GONE);

        holder.amount.setText(Html.fromHtml("Amount: <b>" + orderList.get(position).getAmount() + "</b>"));
        holder.refNo.setText(Html.fromHtml("Ref No: <b>" + orderList.get(position).getRefNo() + "</b>"));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoProductPageFragment fragment = new DemoProductPageFragment(true);
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.mainFrameLayout, fragment, DemoProductPageFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }



    private String TAG = OrderListAdapter.class.getSimpleName();











    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image, copyImage;
        TextView name, status, activationCode, expiry, cardPin, errorInfo, cardPinInfo, tapToActivate, amount, refNo;
        LinearLayout codeLayout;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.card_image);
            name = itemView.findViewById(R.id.product_name);
            status = itemView.findViewById(R.id.product_status);
            activationCode = itemView.findViewById(R.id.coupon_code);
            expiry = itemView.findViewById(R.id.product_expiry);
            codeLayout = itemView.findViewById(R.id.code_layout);
            //cardNumber = itemView.findViewById(R.id.card_number);
            cardPin = itemView.findViewById(R.id.card_pin);
            cardPinInfo = itemView.findViewById(R.id.card_pin_info);
            copyImage = itemView.findViewById(R.id.copy_image);
            errorInfo = itemView.findViewById(R.id.error_info);
            cardView = itemView.findViewById(R.id.order_item_card);
            tapToActivate = itemView.findViewById(R.id.tap_to_activate);
            amount = itemView.findViewById(R.id.order_amount);
            refNo = itemView.findViewById(R.id.ref_no);
        }
    }

}
