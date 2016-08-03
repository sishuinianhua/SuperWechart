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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.OkHttpUtils2;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewgoodsFragment extends Fragment {

    private static final int ACTION_DOWNLOAD = 0;
    private static final int ACTION_PULLUP = 1;
    private static final int ACTION_PULLDOWN = 2;
    private static final int PAGE_SIZE = 6;
    private static final String TAG = NewgoodsFragment.class.getSimpleName();
    RecyclerView mrv;
    SwipeRefreshLayout mSRL;
    TextView mtvsrlHint;
    NewGoodsAdapter mAdapter;
    ArrayList<NewGoodBean> mList;
    GridLayoutManager mLayoutManager;
    int mPageId=0;

    public NewgoodsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout= inflater.inflate(R.layout.fragment_newgoods, container, false);
        initData();
        initView(layout);
        setListener();
        return layout;
    }

    private void initData() {
        mPageId = 0;
        downloadNewGoodsList(ACTION_DOWNLOAD,mPageId);
    }

    private void downloadNewGoodsList(final int action, int pageId) {
        //http://localhost:8080/FuLiCenterServer/Server?request=find_new_boutique_goods&cat_id=0&page_id=1&page_size=6
        OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID,I.CAT_ID+"")
                .addParam(I.PAGE_ID,mPageId+"")
                .addParam(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                .targetClass(NewGoodBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
                    @Override
                    public void onSuccess(NewGoodBean[] result) {
                        mAdapter.setMore(result!=null&&result.length>0);
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

    private void initView(View layout) {
        mrv = (RecyclerView) layout.findViewById(R.id.rvNewGoods);
         mList=new ArrayList<>();
        mAdapter=new NewGoodsAdapter(getContext(),mList);
        mrv.setAdapter(mAdapter);
         mLayoutManager = new GridLayoutManager(getContext(), 2);
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mrv.setLayoutManager(mLayoutManager);

        mSRL = (SwipeRefreshLayout) layout.findViewById(R.id.srlNewGoods);
        mtvsrlHint = (TextView) layout.findViewById(R.id.tvNewGoodsSrlHint);
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
                downloadNewGoodsList(ACTION_PULLDOWN,mPageId);
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
                    downloadNewGoodsList(ACTION_PULLUP,mPageId);
                    if (!mAdapter.isMore()){
                        mAdapter.setFooterText("到底啦...");
                    }
                }
            }
        });
    }

}
