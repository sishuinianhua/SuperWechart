package cn.ucai.fulicenter.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.Contact;
import cn.ucai.fulicenter.utils.OkHttpUtils2;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    private static final String TAG = CartFragment.class.getSimpleName();
    RecyclerView mrv;
    SwipeRefreshLayout mSRL;
    TextView mtvsrlHint,tvSumPrice,tvSavePrice,tvBuy;
    CartAdapter mAdapter;
    ArrayList<CartBean> mList;
    LinearLayoutManager mLayoutManager;
    Context mContext;
    CartBeanListReceiver mReceiver;

    public CartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_cart, container, false);
         mContext = getContext();
        registerCartBeanListReceiver();
        initView(layout);
        setListener();
        return layout;
    }

    private void registerCartBeanListReceiver() {
        mReceiver=new CartBeanListReceiver();
        IntentFilter filter=new IntentFilter("update_contact_list");
        mContext.registerReceiver(mReceiver, filter);
    }

    private void initView(View layout) {
        mrv = (RecyclerView) layout.findViewById(R.id.rvCart);
        mAdapter=new CartAdapter(getContext());
        mrv.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        mrv.setLayoutManager(mLayoutManager);

        mSRL = (SwipeRefreshLayout) layout.findViewById(R.id.srlCart);
        mtvsrlHint = (TextView) layout.findViewById(R.id.tvCartSrlHint);
        tvSumPrice = (TextView) layout.findViewById(R.id.tv_cart_sum_price);
        tvSavePrice = (TextView) layout.findViewById(R.id.tv_cart_save_price);
        tvBuy = (TextView) layout.findViewById(R.id.tv_cart_buy);

    }

    private void setPrice() {
        if (mList!=null){
        int currencyPrice=0, rankPrice=0;
        for (CartBean cartBean:mList){
            Log.e(TAG, "getCurrencyPrice" + cartBean.getGoods().getCurrencyPrice());
            Log.e(TAG, "getCount" + cartBean.getCount());
             currencyPrice+=Integer.parseInt( cartBean.getGoods().getCurrencyPrice().substring(1))*cartBean.getCount();
             rankPrice+=Integer.parseInt(cartBean.getGoods().getRankPrice().substring(1))*cartBean.getCount();
        }
        tvSumPrice.setText("合计:￥"+String.valueOf(currencyPrice));
        tvSavePrice.setText("节省:"+String.valueOf(currencyPrice-rankPrice));
            Log.e(TAG, "(\"合计:￥\"+String.valueOf(currencyPrice)=" + ("合计:￥" + String.valueOf(currencyPrice)));
        }else {
            tvSumPrice.setText(String.valueOf("合计:￥00.00"));
            tvSavePrice.setText(String.valueOf("节省:￥00.00"));
            Log.e(TAG, "(\"合计:￥\"+String.valueOf(currencyPrice)=" + ("合计:￥00.00"));
        }
    }

    private void setListener() {
        setPullUpListener();
        setPullDownListener();
    }

    private void setPullDownListener() {
        mSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSRL.setEnabled(true);
                mSRL.setRefreshing(true);
                mtvsrlHint.setVisibility(View.VISIBLE);
                mAdapter.initCartBeenList(mList);
                mSRL.setRefreshing(false);
                mtvsrlHint.setVisibility(View.GONE);

            }
        });
    }

    private void setPullUpListener() {
        mrv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastPosition;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastPosition = mLayoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState==RecyclerView.SCROLL_STATE_IDLE&&lastPosition>=mAdapter.getItemCount()-1&&mAdapter.isMore()){
                    mAdapter.addCartBeenList(mList);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }

    private class CartBeanListReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            mList = FuliCenterApplication.getInstance().getCartBeanList();
            mAdapter.initCartBeenList(mList);
            mAdapter.notifyDataSetChanged();
            setPrice();
            Log.e(TAG, "CartBeanListReceiver"+mList.toString());
        }
    }
}
