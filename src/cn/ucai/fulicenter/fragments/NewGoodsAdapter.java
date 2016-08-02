package cn.ucai.fulicenter.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageLoader;

public class NewGoodsAdapter extends RecyclerView.Adapter{
    private static final int TYPE_FOOTER =0;
    private static final int TYPE_ITEM = 1;
    Context mContext;
    ArrayList<NewGoodBean> mList;
    private boolean more;
    private String footerText;
    ViewGroup parent;

    public NewGoodsAdapter(Context context, ArrayList<NewGoodBean> list) {
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
                NewGoodBean ngBean = mList.get(position);
                ngHolder.tvGoodsName.setText(ngBean.getGoodsName());
                ngHolder.tvPrice.setText(ngBean.getCurrencyPrice());

                ImageLoader.build()
                        .url("http://192.168.7.7:8080/FuLiCenterServer/Server?request=download_new_good&file_name="+ngBean.getGoodsThumb())
                        .width(350)
                        .height(350)
                        .defaultPicture(R.drawable.default_image)
                        .imageView(ngHolder.ivAvatar)
                        .listener(parent)
                        .showImage(mContext);
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
        notifyDataSetChanged();
    }

    public void addNewGoods(ArrayList<NewGoodBean> newGoodBeanList) {
        mList.addAll(newGoodBeanList);
        notifyDataSetChanged();
    }

    class ItemNewGoodsViewHolder extends RecyclerView.ViewHolder{
        ImageView ivAvatar;
        TextView tvGoodsName, tvPrice;
        public ItemNewGoodsViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivNewGoodsAvatar);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tvGoodsName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvNewGoodsPrice);
        }
    }
}
