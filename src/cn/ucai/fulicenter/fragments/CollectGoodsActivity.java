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
import java.util.Arrays;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.view.DisplyUtils;

public class CollectGoodsActivity extends Activity {
    private static final int ACTION_DOWNLOAD = 0;
    private static final int ACTION_PULLUP = 1;
    private static final int ACTION_PULLDOWN = 2;
    private static final String TAG = BoutiqueChildActivity.class.getSimpleName();
    RecyclerView mrv;
    SwipeRefreshLayout mSRL;
    TextView mtvsrlHint;
    CollectGoodsAdapter mAdapter;
    ArrayList<CollectBean> mList;
    GridLayoutManager mLayoutManager;
    int mPageId=0;
    CollectGoodsActivity mContext = this;
    int mCatId=0;
    String mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_goods);
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
//find_collects&userName=aa&page_id=0&page_size=5
    private void downloadNewGoodsList(final int action) {
        OkHttpUtils2<CollectBean[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_COLLECTS)
                .addParam(I.Collect.USER_NAME, FuliCenterApplication.getInstance().getUserName())
                .addParam(I.PAGE_ID,mPageId+"")
                .addParam(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                .targetClass(CollectBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<CollectBean[]>() {
                    @Override
                    public void onSuccess(CollectBean[] collectBeanArr) {
                        Log.e(TAG, "collectBean=" + Arrays.toString(collectBeanArr));
                        mAdapter.setMore(collectBeanArr.length==I.PAGE_SIZE_DEFAULT);
                        if (mAdapter.isMore()){
                            mAdapter.setFooterText("上拉刷新，加载更多...");
                        }else {
                            mAdapter.setFooterText("到底啦...");
                        }
                        ArrayList<CollectBean> collectBeanList = OkHttpUtils2.array2List(collectBeanArr);
                        switch (action){
                            case ACTION_DOWNLOAD:
                                mAdapter.initNewGoods(collectBeanList);
                                break;
                            case ACTION_PULLDOWN:
                                mAdapter.initNewGoods(collectBeanList);
                                break;
                            case ACTION_PULLUP:
                                mAdapter.addNewGoods(collectBeanList);
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
        DisplyUtils.initTitle(mContext,"收藏商品列表");
        mrv = (RecyclerView) findViewById(R.id.rvCollect);
        mList=new ArrayList<>();
        mAdapter=new CollectGoodsAdapter(mContext,mList);
        mrv.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(mContext, 2);
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mrv.setLayoutManager(mLayoutManager);

        mSRL = (SwipeRefreshLayout) findViewById(R.id.srlCollect);
        mtvsrlHint = (TextView) findViewById(R.id.tvCollectSrlHint);
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
