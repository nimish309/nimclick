package com.foresight.clickonmoney.earned_history;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.network.AdvanceNetworkFragment;
import com.foresight.clickonmoney.network.NetworkFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asd on 07-08-2015.
 */

public class EarnedListAdapter extends RecyclerView.Adapter<EarnedListAdapter.RecyclerViewHolder> {

    private MainActivity mainActivity;
    private ArrayList<String> arrayList;
    private JSONObject item;

    public EarnedListAdapter(MainActivity mainActivity, ArrayList<String> arrayList) {
        this.mainActivity = mainActivity;
        this.arrayList = arrayList;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        TextView txtDate, txtAmount, txtRemarks;
        LinearLayout rowEarnedList;

        public RecyclerViewHolder(View v) {
            super(v);
            rowEarnedList = (LinearLayout) v.findViewById(R.id.rowEarnedList);
            txtDate = (TextView) v.findViewById(R.id.txtDate);
            txtAmount = (TextView) v.findViewById(R.id.txtAmount);
            txtRemarks = (TextView) v.findViewById(R.id.txtRemarks);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.earned_list_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        item = getItem(position);
        if (item != null) {
            try {
                if(position%2==0){
                    holder.rowEarnedList.setBackgroundColor(mainActivity.getResources().getColor(R.color.card_alternet_bg));
                }else{
                    holder.rowEarnedList.setBackgroundColor(Color.WHITE);
                }
                holder.txtDate.setText(AppConstant.longToDate(item.has("date")?(item.isNull("date")?"":item.getString("date")):""));
                holder.txtAmount.setText(item.has("amount") ? (item.isNull("amount") ? "0" : item.getString("amount")) : "0");
                holder.txtRemarks.setText(item.has("remarks")?(item.isNull("remarks")?"":item.getString("remarks")):"");
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