package com.foresight.clickonmoney.bank_withdraw;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.JSONData;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lucky on 19/08/15.
 */
public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.RecyclerViewHolder> {

    private MainActivity mainActivity;
    private ArrayList<String> arrayList;
    private JSONObject item;

    public TransactionListAdapter(MainActivity mainActivity, ArrayList<String> arrayList) {
        this.mainActivity = mainActivity;
        this.arrayList = arrayList;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        TextView txtBankName, txtAccountNo, txtIsfcCode, txtAmount, txtTransfer, txtDate;
        LinearLayout rowTransactionList;

        public RecyclerViewHolder(View v) {
            super(v);
            rowTransactionList = (LinearLayout) v.findViewById(R.id.rowTransactionList);
            txtBankName = (TextView) v.findViewById(R.id.txtBankName);
            txtAccountNo = (TextView) v.findViewById(R.id.txtAccountNo);
            txtIsfcCode = (TextView) v.findViewById(R.id.txtIsfcCode);
            txtAmount = (TextView) v.findViewById(R.id.txtAmount);
            txtTransfer = (TextView) v.findViewById(R.id.txtTransfer);
            txtDate = (TextView) v.findViewById(R.id.txtDate);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.transaction_list_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        item = getItem(position);
        if (item != null) {
            try {

                holder.txtBankName.setText(JSONData.getString(item, "bank"));
                holder.txtAccountNo.setText(JSONData.getString(item,"account"));
                holder.txtIsfcCode.setText(JSONData.getString(item, "ifsc_code"));
                holder.txtAmount.setText(JSONData.getString(item,"amount"));
                holder.txtTransfer.setText(JSONData.getString(item,"transaction_amount"));
                holder.txtDate.setText(AppConstant.longToDate(JSONData.getString(item,"transaction_date")));

            } catch (Exception e) {
                e.printStackTrace();
                holder.txtBankName.setText("");
                holder.txtAccountNo.setText("");
                holder.txtIsfcCode.setText("");
                holder.txtAmount.setText("");
                holder.txtTransfer.setText("");
                holder.txtDate.setText("");
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