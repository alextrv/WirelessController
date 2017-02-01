package org.travinskiy.alex.universalcontroller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WhitelistActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteListener {

    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    private DividerItemDecoration mDividerItemDecoration;
    private LinearLayoutManager mLayoutManager;

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
