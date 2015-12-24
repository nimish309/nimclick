package com.foresight.clickonmoney.redeem;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asd on 07-08-2015.
 */

public class RedeemListAdapter extends RecyclerView.Adapter<RedeemListAdapter.RecyclerViewHolder> {

    private MainActivity mainActivity;
    private ArrayList<String> arrayList;
    private JSONObject item;

    public RedeemListAdapter(MainActivity mainActivity, ArrayList<String> arrayList) {
        this.mainActivity = mainActivity;
        this.arrayList = arrayList;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        TextView txtDate, txtAmount, txtMobile;
        LinearLayout rowRedeemHistoryList;

        public RecyclerViewHolder(View v) {
            super(v);
            rowRedeemHistoryList = (LinearLayout) v.findViewById(R.id.rowRedeemHistoryList);
            txtDate = (TextView) v.findViewById(R.id.txtDate);
            txtAmount = (TextView) v.findViewById(R.id.txtAmount);
            txtMobile = (TextView) v.findViewById(R.id.txtMobile);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.redeem_history_list_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        item = getItem(position);
        if (item != null) {
            try {
                if (position % 2 == 0) {
                    holder.rowRedeemHistoryList.setBackgroundColor(mainActivity.getResources().getColor(R.color.card_alternet_bg));
                } else {
                    holder.rowRedeemHistoryList.setBackgroundColor(Color.WHITE);
                }
                holder.txtDate.setText(AppConstant.longToDate(item.has("date") ? (item.isNull("date") ? "" : item.getString("date")) : ""));
                holder.txtAmount.setText(item.has("amount") ? (item.isNull("amount") ? "0" : item.getString("amount")) : "0");
                holder.txtMobile.setText(item.has("mobile_no") ? (item.isNull("mobile_no") ? "" : item.getString("mobile_no")) : "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public JSONObject getItem(int position) {
        try {
            return new JSONObject(arrayList.get(position));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}