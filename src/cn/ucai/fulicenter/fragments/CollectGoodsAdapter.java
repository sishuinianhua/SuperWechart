package cn.ucai.fulicenter.fragments;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.utils.UserUtils;

public class CollectGoodsAdapter extends RecyclerView.Adapter{
    CollectGoodsActivity mContext;
    ArrayList<CollectBean> mList;
    private boolean more;
    private String footerText;
    private static final int TYPE_FOOTER =0;
    private static final int TYPE_ITEM = 1;
    ViewGroup parent;

    public CollectGoodsAdapter(CollectGoodsActivity context, ArrayList<CollectBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        RecyclerView.ViewHolder holder = null;
        switch (viewType){
            case TYPE_FOOTER:
                holder = new FooterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_footer, null));
                break;
            case TYPE_ITEM:
                holder = new itemCollectHolder(LayoutInflater.from(mContext).inflate(R.layout.item_collect_holder, null));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TYPE_FOOTER:
                (( FooterViewHolder)holder).tvFooter.setText(getFooterText());
                break;
            case TYPE_ITEM:
                itemCollectHolder collectHolder = (itemCollectHolder) holder;
                final CollectBean collectBean = mList.get(position);
                collectHolder.tvName.setText(collectBean.getGoodsName());
                UserUtils.setAvatar(mContext,collectBean.getGoodsThumb(),collectHolder.ivAvatar);
                collectHolder.rlLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext,NewGoodsDetailsActivity.class)
                                .putExtra(D.NewGood.KEY_GOODS_ID,collectBean.getGoodsId())
                        );


                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==getItemCount()-1){
            return TYPE_FOOTER;
        }else {
            return TYPE_ITEM;
        }
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

    public String getFooterText() {
        return footerText;
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
    class itemCollectHolder extends RecyclerView.ViewHolder{
        FrameLayout rlLayout;
        ImageView ivAvatar,ivDel;
        TextView tvName;
        public itemCollectHolder(View itemView) {
            super(itemView);
            rlLayout = (FrameLayout) itemView.findViewById(R.id.rlItemCollect);
            ivDel = (ImageView) itemView.findViewById(R.id.ivCollectDel);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivCollectAvatar);
            tvName = (TextView) itemView.findViewById(R.id.tvCollectName);
        }

    }
}
