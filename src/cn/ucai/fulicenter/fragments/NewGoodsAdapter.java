package cn.ucai.fulicenter.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.UserUtils;

public class NewGoodsAdapter extends RecyclerView.Adapter{
    private static final int TYPE_FOOTER =0;
    private static final int TYPE_ITEM = 1;
    private static final String TAG = NewGoodsAdapter.class.getSimpleName();
    Context mContext;
    ArrayList<NewGoodBean> mList;
    private boolean more;
    private String footerText;
    ViewGroup parent;
    private int sortBy=1;

    public NewGoodsAdapter(Context context, ArrayList<NewGoodBean> list) {
        mContext = context;
        mList = list;
        sortByTimePrice();
    }

    private void sortByTimePrice() {

        Collections.sort(mList, new Comparator<NewGoodBean>() {
            @Override
            public int compare(NewGoodBean lhs, NewGoodBean rhs) {
                int result = 1;
                switch (getSortBy()){
                    case 1:
                        result = (int) (rhs.getAddTime() - lhs.getAddTime());
                        break;
                    case 2:
                        result = (int) (lhs.getAddTime() - rhs.getAddTime());
                        break;
                    case 3:
                        result = getCurrentPrice(rhs.getCurrencyPrice())-getCurrentPrice(lhs.getCurrencyPrice());
                        break;
                    case 4:
                        result = getCurrentPrice(lhs.getCurrencyPrice())-getCurrentPrice(rhs.getCurrencyPrice());
                        break;
                }
                return result;
            }
        });
        notifyDataSetChanged();
    }

    private int getCurrentPrice(String currencyPriceStr) {
        return Integer.parseInt(currencyPriceStr.substring(1));
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
                holder = new ItemNewGoodsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_newgoods, null));
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
                ItemNewGoodsViewHolder ngHolder = (ItemNewGoodsViewHolder) holder;
                final NewGoodBean ngBean = mList.get(position);
                ngHolder.tvGoodsName.setText(ngBean.getGoodsName());
                ngHolder.tvPrice.setText(ngBean.getCurrencyPrice());
                UserUtils.setAvatar(mContext,ngBean.getGoodsThumb(),ngHolder.ivAvatar);
                ngHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext,NewGoodsDetailsActivity.class)
                                .putExtra(D.NewGood.KEY_GOODS_ID,ngBean.getGoodsId())
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

    public void initNewGoods(ArrayList<NewGoodBean> newGoodBeanList) {
        mList.clear();
        mList.addAll(newGoodBeanList);
        sortByTimePrice();
    }

    public void addNewGoods(ArrayList<NewGoodBean> newGoodBeanList) {
        mList.addAll(newGoodBeanList);
        sortByTimePrice();
    }

    public int getSortBy() {
        return sortBy;
    }

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
        sortByTimePrice();
    }


    class ItemNewGoodsViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        ImageView ivAvatar;
        TextView tvGoodsName, tvPrice;
        public ItemNewGoodsViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivNewGoodsAvatar);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tvGoodsName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvNewGoodsPrice);
            layout = (LinearLayout) itemView.findViewById(R.id.llItemNewgoods);
        }
    }


}
