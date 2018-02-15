package com.example.l.blewatch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.l.blewatch.R;
import com.example.l.blewatch.bean.BluetoothLeDeviceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/17.
 */

public class BleListAdapter extends RecyclerView.Adapter<BleListAdapter.ViewHolder> {

    private List<BluetoothLeDeviceBean> mList;
    private OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView mTvBleName, mTvBleRssi;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTvBleName = itemView.findViewById(R.id.tv_ble_name);
            mTvBleRssi = itemView.findViewById(R.id.tv_ble_rssi);
        }
    }

    public BleListAdapter(OnItemClickListener onItemClickListener) {
        mList = new ArrayList<>();
        mOnItemClickListener = onItemClickListener;
    }

    public void addDevice(BluetoothLeDeviceBean leDeviceBean) {
        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.add(leDeviceBean);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device_list, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BluetoothLeDeviceBean device = mList.get(position);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position);
            }
        });
        holder.mTvBleName.setText(device.getBluetoothName());
        holder.mTvBleRssi.setText(device.getRssi());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


}
