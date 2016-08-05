package cn.ucai.fulicenter.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

public class CategoryActivity extends Activity {
    private static final int ACTION_DOWNLOAD = 0;
    private static final int ACTION_PULLUP = 1;
    private static final int ACTION_PULLDOWN = 2;
    private static final String TAG = CategoryActivity.class.getSimpleName();
    RecyclerView mrv;
    SwipeRefreshLayout mSRL;
    TextView mtvsrlHint;
    NewGoodsAdapter mAdapter;
    ArrayList<NewGoodBean> mList;
    GridLayoutManager mLayoutManager;
    int mPageId=0;
    CategoryActivity mContext = this;
    int mCatId=0;
    String mTitle;
    TextView mbtTimeSort, mbtPriceSort;
    boolean timeSortFlag=true;
    boolean priceSortFlag=true;
    int sortBy = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initData();
        initView();
        setListener();
    }
    private void initData() {
        mCatId = getIntent().getIntExtra(I.NewAndBoutiqueGood.CAT_ID, 0);
       // mTitle = getIntent().getStringExtra(D.Boutique.KEY_TITLE);
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
                        if (result!=null){
                            if (result.length==I.PAGE_SIZE_DEFAULT){
                                mAdapter.setFooterText("上拉刷新，加载更多...");
                                mAdapter.setMore(true);
                            }else {
                                mAdapter.setFooterText("到底啦...");
                                mAdapter.setMore(false);
                            }


                      /*  if (mAdapter.isMore()){
                            mAdapter.setFooterText("上拉刷新，加载更多...");
                        }else {
                            mAdapter.setFooterText("到底啦...");
                        }*/
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
                    }
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }

    private void initView() {
        DisplyUtils.initTitle(mContext,"");
        mrv = (RecyclerView) findViewById(R.id.rvCategory);
        mList=new ArrayList<>();
        mAdapter=new NewGoodsAdapter(mContext,mList);
        mrv.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(mContext, 2);
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mrv.setLayoutManager(mLayoutManager);

        mSRL = (SwipeRefreshLayout) findViewById(R.id.srlCategory);
        mtvsrlHint = (TextView) findViewById(R.id.tvCategorySrlHint);

        mbtTimeSort = (TextView) findViewById(R.id.tvCategoryTimeSort);
        mbtPriceSort= (TextView) findViewById(R.id.tvCategoryPriceSort);
    }

    private void setListener() {
        setPullUpListener();
        setPullDownListener();
        SortListener sortListener = new SortListener();
        mbtTimeSort.setOnClickListener(sortListener);
        mbtPriceSort.setOnClickListener(sortListener);
       /* Drawable right = null;
        switch (sortBy){
            case 1:
            case 3:
                right = ContextCompat.getDrawable(mContext, R.drawable.arrow_order_down);
                right.setBounds(0,0,right.getMinimumWidth(),right.getMinimumHeight());
                mbtTimeSort.setCompoundDrawables(null,null,right,null);
                break;
            case 2:
            case 4:
                right = ContextCompat.getDrawable(mContext, R.drawable.arrow_order_up);
                right.setBounds(0,0,right.getMinimumWidth(),right.getMinimumHeight());
                mbtTimeSort.setCompoundDrawables(null,null,right,null);
                break;
        }*/

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
                }
            }
        });
    }

    private class SortListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.tvCategoryTimeSort:
                    Log.e(TAG, "tvCategoryTimeSort");
                    if (timeSortFlag){
                        sortBy = 2;
                        setArrowUp(mbtTimeSort);
                    }else {
                        sortBy = 1;
                        setArrowDown(mbtTimeSort);
                    }
                    timeSortFlag = !timeSortFlag;
                    break;
                case R.id.tvCategoryPriceSort:
                    Log.e(TAG, "tvCategoryPriceSort");
                    if (priceSortFlag){
                        sortBy = 4;
                         setArrowUp(mbtPriceSort);
                    }else {
                        sortBy = 3;
                        setArrowDown(mbtPriceSort);
                    }
                    priceSortFlag = !priceSortFlag;
                    break;
            }
            Log.e(TAG, "sortBy="+sortBy);
            mAdapter.setSortBy(sortBy);
        }
    }

    private void setArrowUp(TextView btn) {
        Drawable right = ContextCompat.getDrawable(mContext, R.drawable.arrow_order_up);
        right.setBounds(0,0,right.getMinimumWidth(),right.getMinimumHeight());
        btn.setCompoundDrawables(null,null,right,null);
    }
    private void setArrowDown(TextView btn) {
        Drawable right = ContextCompat.getDrawable(mContext, R.drawable.arrow_order_down);
        right.setBounds(0,0,right.getMinimumWidth(),right.getMinimumHeight());
        btn.setCompoundDrawables(null,null,right,null);
    }
}
