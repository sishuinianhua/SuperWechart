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
    private static final int ACTION_DOWNLOAD = 0;
    private static final int ACTION_PULLUP = 1;
    private static final int ACTION_PULLDOWN = 2;
    private static final String TAG = CartFragment.class.getSimpleName();
    RecyclerView mrv;
    SwipeRefreshLayout mSRL;
    TextView mtvsrlHint;
    CartAdapter mAdapter;
    ArrayList<CartBean> mList;
    LinearLayoutManager mLayoutManager;
    int mPageId=0;
    int mAction;
    Context mContext;
    CartBeanListReceiver mReceiver;

    public CartFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_cart, container, false);
         mContext = getContext();
        initView(layout);
        setListener();
        return layout;
    }

    private void registerCartBeanListReceiver() {
        mReceiver=new CartBeanListReceiver();
        IntentFilter filter=new IntentFilter("update_contact_list");
        mContext.registerReceiver(mReceiver, filter);
    }

    private void initData() {
        mPageId = 0;
        mAction = ACTION_DOWNLOAD;
        downloadCartBeanList();
    }

    private void downloadCartBeanList() {

                        switch (mAction){
                            case ACTION_DOWNLOAD:
                            case ACTION_PULLDOWN:
                                mAdapter.initCartBeenList(mList);
                                break;
                            case ACTION_PULLUP:
                                mAdapter.addCartBeenList(mList);
                                break;
                        }
    }

    private void initView(View layout) {
        mrv = (RecyclerView) layout.findViewById(R.id.rvCart);
        mList=new ArrayList<>();
        mAdapter=new CartAdapter(getContext(),mList);
        mrv.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        mrv.setLayoutManager(mLayoutManager);

        mSRL = (SwipeRefreshLayout) layout.findViewById(R.id.srlCart);
        mtvsrlHint = (TextView) layout.findViewById(R.id.tvCartSrlHint);
    }

    private void setListener() {
        registerCartBeanListReceiver();
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
                mPageId = 0;
                mAction = ACTION_PULLDOWN;
                downloadCartBeanList();
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
                    mPageId+=I.PAGE_SIZE_DEFAULT;
                    mAction = ACTION_PULLUP;
                    downloadCartBeanList();
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
            initData();
            mAdapter.notifyDataSetChanged();
        }
    }
}
