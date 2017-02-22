package org.alex.wirelesscontroller;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WhitelistActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteListener {

    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    private DividerItemDecoration mDividerItemDecoration;
    private LinearLayoutManager mLayoutManager;

    private static final String ZERO_MAC = "00:00:00:00:00:00";

    public static Intent newIntent(Context context) {
        return new Intent(context, WhitelistActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);

        mRecyclerView = (RecyclerView) findViewById(R.id.whitelist_recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        updateUI();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.whitelist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_wifi:
                addWifiToWhitelist();
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addWifiToWhitelist() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getBSSID() != null && wifiInfo.getNetworkId() != -1 &&
                    !wifiInfo.getBSSID().equals(ZERO_MAC)) {
                Set<String> wifiWhitelist = AppPreferences.getPrefWifiWhitelist(getApplicationContext());
                String SSID = wifiInfo.getSSID();
                String BSSID = wifiInfo.getBSSID();
                String SSID_BSSID = getResources().getString(R.string.ssid_bssid, SSID, BSSID);
                boolean isAdded = wifiWhitelist.add(SSID_BSSID);
                if (isAdded) {
                    AppPreferences.setPrefWifiWhitelist(getApplicationContext(), wifiWhitelist);
                    Toast.makeText(getApplicationContext(), R.string.added_to_whitelist, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.already_in_whitelist, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.connect_to_wifi, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.enable_wifi, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {

        Set<String> whitelistSet = AppPreferences.getPrefWifiWhitelist(getApplicationContext());

        List<String> whitelistArray = new ArrayList<>(whitelistSet);

        if (mAdapter == null) {
            mAdapter = new ListAdapter(whitelistArray);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setWhitelist(whitelistArray);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onConfirm(String value) {
        Set<String> whitelist = AppPreferences.getPrefWifiWhitelist(getApplicationContext());
        boolean isDeleted = whitelist.remove(value);
        if (isDeleted) {
            AppPreferences.setPrefWifiWhitelist(getApplicationContext(), whitelist);
            updateUI();
        }
    }

    private class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mWhitelistElement;
        private String mWifiElement;

        public ListHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mWhitelistElement = (TextView) itemView.findViewById(R.id.whitelist_element);

        }

        public void bindValue(String value) {
            mWifiElement = value;
            mWhitelistElement.setText(value);
        }

        @Override
        public void onClick(View v) {
            DeleteDialogFragment dialog = DeleteDialogFragment.newInstance(mWifiElement);
            dialog.show(getSupportFragmentManager(), "tag");
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListHolder> {

        private List<String> mWhitelist;

        public void setWhitelist(List<String> whitelist) {
            mWhitelist = whitelist;
        }

        public ListAdapter(List<String> whitelist) {
            mWhitelist = whitelist;
        }

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(WhitelistActivity.this);

            View itemView = inflater.inflate(R.layout.list_whitelist, parent, false);

            return new ListHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ListHolder holder, int position) {
            String element = mWhitelist.get(position);
            holder.bindValue(element);
        }

        @Override
        public int getItemCount() {
            return mWhitelist.size();
        }
    }

}
