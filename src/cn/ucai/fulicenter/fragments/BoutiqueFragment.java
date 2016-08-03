package cn.ucai.fulicenter.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;


/**
 * A simple {@link Fragment} subclass.
 */
public class BoutiqueFragment extends Fragment {
    private static final int ACTION_DOWNLOAD = 0;
    private static final int ACTION_PULLUP = 1;
    private static final int ACTION_PULLDOWN = 2;
    private static final String TAG = BoutiqueFragment.class.getSimpleName();
    RecyclerView mrv;
    SwipeRefreshLayout mSRL;
    TextView mtvsrlHint;
    BoutiqueAdapter mAdapter;
    ArrayList<BoutiqueBean> mList;
    LinearLayoutManager mLayoutManager;
    int mPageId=0;

    public BoutiqueFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_boutique, container, false);
        initData();
        initView(layout);
        setListener();
        return layout;
    }

    private void initData() {
        mPageId = 0;
        downloadNewGoodsList(ACTION_DOWNLOAD);
    }

    private void downloadNewGoodsList(final int action) {
        OkHttpUtils2<BoutiqueBean[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_BOUTIQUES)
                /*.addParam(I.NewAndBoutiqueGood.CAT_ID,I.CAT_ID+"")
                .addParam(I.PAGE_ID,mPageId+"")
                .addParam(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")*/
                .targetClass(BoutiqueBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<BoutiqueBean[]>() {
                    @Override
                    public void onSuccess(BoutiqueBean[] result) {
                        mAdapter.setMore(result!=null&&result.length>I.PAGE_SIZE_DEFAULT);
                        Log.e(TAG, "resultArr02=" + result);
                        ArrayList<BoutiqueBean> list = OkHttpUtils2.array2List(result);
                        switch (action){
                            case ACTION_DOWNLOAD:
                                mAdapter.initNewGoods(list);
                                break;
                            case ACTION_PULLDOWN:
                                mAdapter.initNewGoods(list);

                                break;
                            case ACTION_PULLUP:
                                mAdapter.addNewGoods(list);
                                break;
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }

    private void initView(View layout) {
        mrv = (RecyclerView) layout.findViewById(R.id.rvBoutique);
        mList=new ArrayList<>();
        mAdapter=new BoutiqueAdapter(getContext(),mList);
        mrv.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        mrv.setLayoutManager(mLayoutManager);

        mSRL = (SwipeRefreshLayout) layout.findViewById(R.id.srlBoutique);
        mtvsrlHint = (TextView) layout.findViewById(R.id.tvBoutiqueSrlHint);
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
                mPageId = 0;
                downloadNewGoodsList(ACTION_PULLDOWN);
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
                    downloadNewGoodsList(ACTION_PULLUP);
                    if (mAdapter.isMore()){
                         mAdapter.setFooterText("上拉刷新，加载更多...");
                    }else {
                        mAdapter.setFooterText("到底啦...");
                    }
                }
            }
        });
    }

}
