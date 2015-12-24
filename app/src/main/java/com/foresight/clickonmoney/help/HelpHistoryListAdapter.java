package com.foresight.clickonmoney.help;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asd on 23-08-2015.
 */
public class HelpHistoryListAdapter extends RecyclerView.Adapter<HelpHistoryListAdapter.RecyclerViewHolder> {

    private MainActivity mainActivity;
    private ArrayList<String> arrayList;
    private JSONObject item;

    public HelpHistoryListAdapter(MainActivity mainActivity, ArrayList<String> arrayList) {
        this.mainActivity = mainActivity;
        this.arrayList = arrayList;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        LinearLayout rowWhatNewList;

        public RecyclerViewHolder(View v) {
            super(v);
            rowWhatNewList = (LinearLayout) v.findViewById(R.id.rowWhatNewList);
            txtName = (TextView) v.findViewById(R.id.txtName);
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.what_new_list_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        item = getItem(position);
        if (item != null) {
            holder.txtName.setText(JSONData.getString(item, "help"));
        } else {
            holder.txtName.setText("");
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
