package cn.ucai.fulicenter.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.UserUtils;

public class CartAdapter extends RecyclerView.Adapter{
    Context mContext;
    ArrayList<CartBean> mList=new ArrayList<>();
    private boolean more;

    public CartAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_cart_holder, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                final CartViewHolder holder1 = (CartViewHolder) holder;
                final CartBean cartBean = mList.get(position);
                holder1.tvGoodName.setText(cartBean.getGoods().getGoodsName());
                holder1.tvCount.setText("("+cartBean.getCount()+")");
                holder1.tvPrice.setText(cartBean.getGoods().getCurrencyPrice());
                UserUtils.setAvatar(mContext,cartBean.getGoods().getGoodsThumb(),holder1.ivThumb);
                holder1.cbSelected.setChecked(cartBean.isChecked());
        holder1.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cartBean.setChecked(isChecked);
                new UpdateCartTask(mContext,cartBean).execute();
            }
        });

        holder1.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartBean.setCount( cartBean.getCount()+1);
                new UpdateCartTask(mContext,cartBean).execute();
            }
        });
        holder1.ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( cartBean.getCount()==1){

                }
                cartBean.setCount( cartBean.getCount()-1);
                new UpdateCartTask(mContext,cartBean).execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public boolean isMore() {
        return more;
    }

    public void initCartBeenList(ArrayList<CartBean> list) {
        if (mList!=null){
            mList.clear();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addCartBeenList(ArrayList<CartBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class CartViewHolder extends RecyclerView.ViewHolder{
        CheckBox cbSelected;
        ImageView ivThumb,ivAdd,ivDel;
        TextView tvGoodName,tvCount,tvPrice;
        public CartViewHolder(View itemView) {
            super(itemView);
            cbSelected = (CheckBox) itemView.findViewById(R.id.cb_cart_selected);
            ivThumb = (ImageView) itemView.findViewById(R.id.iv_cart_thumb);
            ivAdd = (ImageView) itemView.findViewById(R.id.iv_cart_add);
            ivDel = (ImageView) itemView.findViewById(R.id.iv_cart_del);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_cart_good_name);
            tvCount = (TextView) itemView.findViewById(R.id.tv_cart_count);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_cart_price);
        }
    }
}

