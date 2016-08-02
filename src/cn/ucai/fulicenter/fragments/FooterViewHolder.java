package cn.ucai.fulicenter.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by wdyzyr on 2016/8/1.
 */
public class FooterViewHolder extends RecyclerView.ViewHolder{
    TextView tvFooter;
    public FooterViewHolder(View itemView) {
        super(itemView);
        tvFooter = (TextView) itemView.findViewById(R.id.tvFooter);
    }
}
