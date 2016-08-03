package cn.ucai.fulicenter.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.UserUtils;

public class BoutiqueAdapter extends RecyclerView.Adapter{
    private static final int TYPE_FOOTER =0;
    private static final int TYPE_ITEM = 1;
    Context mContext;
    ArrayList<BoutiqueBean> mList;
    private boolean more;
    private String footerText;
    ViewGroup parent;

    public BoutiqueAdapter(Context context, ArrayList<BoutiqueBean> list) {
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
                holder = new ItemBoutiqueViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_boutique_adapter, null));
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
                ItemBoutiqueViewHolder holder1 = (ItemBoutiqueViewHolder) holder;
                final BoutiqueBean bean = mList.get(position);
                holder1.tvTitle.setText(bean.getTitle());
                holder1.tvName.setText(bean.getName());
                holder1.tvDescripion.setText(bean.getDescription());
                UserUtils.setAvatar(mContext,bean.getImageurl(),holder1.ivAvatar);
             /*   holder1.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, BoutiqueDetailsActivity.class)
                                .putExtra(D.GoodDetails.KEY_GOODS_ID, bean.getId()));

                    }
                });*/
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

    public void initNewGoods(ArrayList<BoutiqueBean> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addNewGoods(ArrayList<BoutiqueBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class ItemBoutiqueViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout layout;
        ImageView ivAvatar;
        TextView tvTitle, tvName,tvDescripion;
        public ItemBoutiqueViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivBoutiqueImg);
            tvTitle = (TextView) itemView.findViewById(R.id.tvBoutiqueTitle);
            tvName = (TextView) itemView.findViewById(R.id.tvBoutiqueName);
            tvDescripion = (TextView) itemView.findViewById(R.id.tvBoutiqueDescripion);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout_boutique_item);
        }
    }


}
