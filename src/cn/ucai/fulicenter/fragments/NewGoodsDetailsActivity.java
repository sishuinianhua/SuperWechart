package cn.ucai.fulicenter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

public class NewGoodsDetailsActivity extends Activity {
    private static final String TAG = NewGoodsDetailsActivity.class.getSimpleName();
    ImageView mivShare,mivCollect, mivCart;
    TextView mtvCartCount,mtvGoodName,mtvEnglishName,mtvPriceShop, mtvPriceCurrent;
    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    WebView mwvGoodBrief;
    int mGoodsId;
    NewGoodsDetailsActivity mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goods_details);
        mContext = this;
        initView();
        initData();
    }

    private void initData() {
         mGoodsId=getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID, 0);
        Log.e(TAG, "mGoodsId=" + mGoodsId);
        if (mGoodsId>0){


        getGoodsDetailsByGoodsId(new OkHttpUtils2.OnCompleteListener<NewGoodBean>() {
            @Override
            public void onSuccess(NewGoodBean result) {
                Log.e(TAG, "result="+result);
                showGoodDetails(result);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "error=" + error);
                finish();
                Toast.makeText(mContext,"获取商品详情失败",Toast.LENGTH_LONG).show();
            }
        });
        }else {
            finish();
            Toast.makeText(mContext,"获取商品详情失败",Toast.LENGTH_LONG).show();
        }
    }

    private void showGoodDetails(NewGoodBean ngb) {
       mtvGoodName.setText(ngb.getGoodsName());
        mtvEnglishName.setText(ngb.getGoodsEnglishName());
        mtvPriceShop.setText(ngb.getPromotePrice());
         mtvPriceCurrent.setText(ngb.getCurrencyPrice());
    }

    private void getGoodsDetailsByGoodsId(OkHttpUtils2.OnCompleteListener<NewGoodBean> listner) {
        OkHttpUtils2<NewGoodBean> utils = new OkHttpUtils2<>();
                utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                        .addParam(D.GoodDetails.KEY_GOODS_ID,mGoodsId+"")
                        .targetClass(NewGoodBean.class)
                        .execute(listner);
    }

    private void initView() {
        mivShare = (ImageView) findViewById(R.id.ivShare);
        mivCollect = (ImageView) findViewById(R.id.ivCollet);
        mivCart = (ImageView) findViewById(R.id.ivAddCart);
        mtvCartCount = (TextView) findViewById(R.id.tvCartCount);
        mtvGoodName = (TextView) findViewById(R.id.tvGoodName);
        mtvEnglishName = (TextView) findViewById(R.id.tvGooodEnglishName);
        mtvPriceShop = (TextView) findViewById(R.id.tvShopPrice);
        mtvPriceCurrent = (TextView) findViewById(R.id.tvCurrencyPrice);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.srlNewGoods);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        mwvGoodBrief = (WebView) findViewById(R.id.wvGoodBrief);
        WebSettings settings = mwvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }
}
