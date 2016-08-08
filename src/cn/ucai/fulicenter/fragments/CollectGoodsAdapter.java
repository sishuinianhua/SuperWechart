package cn.ucai.fulicenter.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.NewGoodBean;

public class CollectGoodsAdapter extends RecyclerView.Adapter{
    CollectGoodsActivity mContext;
    ArrayList<CollectBean> mList;
    private boolean more;
    private String footerText;

    public CollectGoodsAdapter(CollectGoodsActivity context, ArrayList<CollectBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public boolean isMore() {
        return more;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
        notifyDataSetChanged();
    }

    public void initNewGoods(ArrayList<CollectBean> collectBeanList) {
        mList.clear();
        mList.addAll(collectBeanList);
           notifyDataSetChanged();
    }

    public void addNewGoods(ArrayList<CollectBean> collectBeanList) {
        mList.addAll(collectBeanList);
        notifyDataSetChanged();
    }
}
