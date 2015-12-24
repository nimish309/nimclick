package com.foresight.clickonmoney.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foresight.clickonmoney.MainActivity;
import com.foresight.clickonmoney.R;
import com.foresight.clickonmoney.Util.AppConstant;
import com.foresight.clickonmoney.Util.Constants;
import com.foresight.clickonmoney.Util.JSONData;
import com.foresight.clickonmoney.Util.JSONParser;
import com.foresight.clickonmoney.recyclerviewflexibledivider.AlphaInAnimationAdapter;
import com.foresight.clickonmoney.recyclerviewflexibledivider.SlideInRightAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

/**
 * Created by asd on 31-07-2015.
 */
public class NetworkListAdapter extends RecyclerView.Adapter<NetworkListAdapter.RecyclerViewHolder> {

    private MainActivity mainActivity;
    private ArrayList<String> arrayList;
    private JSONObject item;
    private boolean isAdvanceNetwrok;
    private AdvanceNetworkFragment advanceNetworkFragment;
    private NetworkFragment networkFragment;

    public NetworkListAdapter(MainActivity mainActivity, ArrayList<String> arrayList, NetworkFragment networkFragment) {
        this.mainActivity = mainActivity;
        this.arrayList = arrayList;
        this.networkFragment = networkFragment;
    }

    public NetworkListAdapter(MainActivity mainActivity, ArrayList<String> arrayList, boolean isAdvanceNetwrok, AdvanceNetworkFragment advanceNetworkFragment) {
        this.mainActivity = mainActivity;
        this.arrayList = arrayList;
        this.isAdvanceNetwrok = isAdvanceNetwrok;
        this.advanceNetworkFragment = advanceNetworkFragment;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        TextView txtName, txtTotalUser, txtAdview,txtUserAdView, txtEmail, txtCity, txtMobile;
        LinearLayout rowNetworkList;

        public RecyclerViewHolder(View v) {
            super(v);
            rowNetworkList = (LinearLayout) v.findViewById(R.id.rowNetworkList);
            txtName = (TextView) v.findViewById(R.id.txtName);
            txtTotalUser = (TextView) v.findViewById(R.id.txtTotalUser);
            txtAdview = (TextView) v.findViewById(R.id.txtAdview);
            txtUserAdView = (TextView) v.findViewById(R.id.txtUserAdView);
            txtEmail = (TextView) v.findViewById(R.id.txtEmail);
            txtCity = (TextView) v.findViewById(R.id.txtCity);
            txtMobile = (TextView) v.findViewById(R.id.txtMobile);
            rowNetworkList.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.rowNetworkList) {
                Log.d("==>", "" + getLayoutPosition());
                item = getItem(getLayoutPosition());
                if (item != null) {
                    try {
                        if ((item.has("user_count") ? (item.isNull("user_count") ? 0 : item.getInt("user_count")) : 0) > 0) {
                            String unique_id = item.has("unique_id") ? (item.isNull("unique_id") ? "" : item.getString("unique_id")) : "";
                            if (AppConstant.isNetworkAvailable(mainActivity)) {
                                new NetworkListTask(unique_id).execute();
                            } else {
                                AppConstant.showNetworkError(mainActivity);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.network_list_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        item = getItem(position);
        if (item != null) {
            int count = 0, adCount = 0, userAdCount = 0;
            try {
                count = item.has("user_count") ? (item.isNull("user_count") ? 0 : item.getInt("user_count")) : 0;
                adCount = item.has("todays_click") ? (item.isNull("todays_click") ? 0 : item.getInt("todays_click")) : 0;
                userAdCount = item.has("down_todays_click") ? (item.isNull("down_todays_click") ? 0 : item.getInt("down_todays_click")) : 0;
//                if (count > 0) {
//                    holder.txtCount.setVisibility(View.VISIBLE);
                holder.txtTotalUser.setText("" + count);
                holder.txtAdview.setText("" + adCount);
                holder.txtUserAdView.setText("" + userAdCount);
//                } else {
//                    holder.txtCount.setVisibility(View.GONE);
//                }
            } catch (JSONException e) {
                e.printStackTrace();
                holder.txtTotalUser.setVisibility(View.GONE);
            }
            if (isAdvanceNetwrok) {
                try {
                    holder.txtName.setText(item.isNull("user_name") ? "" : item.getString("user_name"));
                    holder.txtEmail.setVisibility(View.GONE);
                    holder.txtCity.setVisibility(View.GONE);
                    holder.txtMobile.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.txtName.setText("");
                    holder.txtEmail.setVisibility(View.GONE);
                    holder.txtCity.setVisibility(View.GONE);
                    holder.txtMobile.setVisibility(View.GONE);
                }
            } else {
                try {
                    holder.txtName.setText(item.isNull("user_name") ? "" : item.getString("user_name"));
                    holder.txtEmail.setText(item.isNull("email_id") ? "" : item.getString("email_id"));
                    holder.txtCity.setText(item.isNull("city") ? "" : item.getString("city"));
                    holder.txtMobile.setText(item.isNull("mobile_no") ? "" : item.getString("mobile_no"));
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.txtName.setText("");
                    holder.txtEmail.setText("");
                    holder.txtCity.setText("");
                    holder.txtMobile.setText("");
                }
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

    private class NetworkListTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        private int responseCode;
        private boolean flag;
        private String message, unique_id;
        private JSONArray data;
        private ArrayList<String> networkList = new ArrayList<>();

        NetworkListTask(String unique_id) {
            this.unique_id = unique_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mainActivity);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            JSONStringer jsonStringer;
            try {
                jsonStringer = new JSONStringer().object().key("unique_id").value(unique_id).endObject();
                String[] jsonData = jsonParser.sendPostReq(Constants.api + Constants.api_advance_network_list, jsonStringer.toString());
                responseCode = Integer.parseInt(jsonData[0]);
                if (responseCode == 200) {
                    JSONObject item = new JSONObject(jsonData[1]);
                    flag = item.getBoolean("flag");
                    message = JSONData.getString(item, "message");
                    if (flag) {
                        data = item.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            networkList.add(data.getJSONObject(i).toString());
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                if (responseCode == 200) {
                    if (flag) {
                        AdvanceNetworkFragment fragment = new AdvanceNetworkFragment();
                        fragment.setNetworkList(networkList);
                        mainActivity.getSupportFragmentManager().beginTransaction()
                                .add(R.id.nav_contentframe, fragment).addToBackStack(unique_id).hide(networkFragment == null ? advanceNetworkFragment : networkFragment).commit();
                    } else {
                        if (!message.trim().equals("")) {
                            AppConstant.showToastShort(mainActivity, message);
                        }
                    }
                } else {
                    AppConstant.unableConnectServer(mainActivity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
