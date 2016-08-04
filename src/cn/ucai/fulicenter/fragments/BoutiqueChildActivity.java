package cn.ucai.fulicenter.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.view.DisplyUtils;

public class BoutiqueChildActivity extends Activity {
    private static final int ACTION_DOWNLOAD = 0;
    private static final int ACTION_PULLUP = 1;
    private static final int ACTION_PULLDOWN = 2;
    private static final String TAG = BoutiqueChildActivity.class.getSimpleName();
    RecyclerView mrv;
    SwipeRefreshLayout mSRL;
    TextView mtvsrlHint;
    NewGoodsAdapter mAdapter;
    ArrayList<NewGoodBean> mList;
    GridLayoutManager mLayoutManager;
    int mPageId=0;
    BoutiqueChildActivity mContext = this;
    int mCatId=0;
    String mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boutique_child);
        initData();
        initView();
        setListener();
    }
    private void initData() {
        mCatId = getIntent().getIntExtra(D.Boutique.KEY_GOODS_ID, 0);
         mTitle = getIntent().getStringExtra(D.Boutique.KEY_TITLE);
        mPageId = 0;
        downloadNewGoodsList(ACTION_DOWNLOAD);
    }

    private void downloadNewGoodsList(final int action) {
        OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID,mCatId+"")
                .addParam(I.PAGE_ID,mPageId+"")
                .addParam(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                .targetClass(NewGoodBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
                    @Override
                    public void onSuccess(NewGoodBean[] result) {
                        mAdapter.setMore(result.length==I.PAGE_SIZE_DEFAULT);
                        if (mAdapter.isMore()){
                            mAdapter.setFooterText("上拉刷新，加载更多...");
                        }else {
                            mAdapter.setFooterText("到底啦...");
                        }
                        Log.e(TAG, "resultArr02=" + result);
                        ArrayList<NewGoodBean> newGoodBeanList = OkHttpUtils2.array2List(result);
                        switch (action){
                            case ACTION_DOWNLOAD:
                                mAdapter.initNewGoods(newGoodBeanList);
                                break;
                            case ACTION_PULLDOWN:
                                mAdapter.initNewGoods(newGoodBeanList);
                                break;
                            case ACTION_PULLUP:
                                mAdapter.addNewGoods(newGoodBeanList);
                                break;
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }

    private void initView() {
        DisplyUtils.initTitle(mContext,mTitle);
        mrv = (RecyclerView) findViewById(R.id.rvBoutique);
        mList=new ArrayList<>();
        mAdapter=new NewGoodsAdapter(mContext,mList);
        mrv.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(mContext, 2);
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mrv.setLayoutManager(mLayoutManager);

        mSRL = (SwipeRefreshLayout) findViewById(R.id.srlBoutique);
        mtvsrlHint = (TextView) findViewById(R.id.tvBoutiqueSrlHint);
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
                mAdapter.setFooterText("上拉刷新，加载更多...");
                if (newState==RecyclerView.SCROLL_STATE_IDLE&&lastPosition>=mAdapter.getItemCount()-1&&mAdapter.isMore()){
                    mPageId+=I.PAGE_SIZE_DEFAULT;
                    downloadNewGoodsList(ACTION_PULLUP);
                }
            }
        });
    }
}
