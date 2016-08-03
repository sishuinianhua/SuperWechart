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
import cn.ucai.fulicenter.bean.AlbumBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.view.DisplyUtils;
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
    GoodDetailsBean mGoodDetailsBean;
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


        getGoodsDetailsByGoodsId(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
            @Override
            public void onSuccess(GoodDetailsBean result) {
                Log.e(TAG, "result="+result.toString());

                mGoodDetailsBean = result;
                showGoodDetails();

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

    private void showGoodDetails() {
       mtvGoodName.setText(mGoodDetailsBean.getGoodsName());
        mtvEnglishName.setText(mGoodDetailsBean.getGoodsEnglishName());
        mtvPriceShop.setText(mGoodDetailsBean.getShopPrice());
         mtvPriceCurrent.setText(mGoodDetailsBean.getCurrencyPrice());
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator,getAlbumImageUrl(),getAlbumImageSize());
        mwvGoodBrief.loadDataWithBaseURL(null,mGoodDetailsBean.getGoodsBrief(),D.TEXT_HTML,D.UTF_8,null);
    }

    private String[] getAlbumImageUrl() {
        String[] url = new String[]{};
        if (mGoodDetailsBean.getProperties()!=null&&mGoodDetailsBean.getProperties().length>0){
            AlbumBean[] albumBean=mGoodDetailsBean.getProperties()[0].getAlbums();
            url = new String[albumBean.length];
            for (int i=0;i<url.length;i++){
                url[i] = albumBean[i].getImgUrl();
            }
        }
        return  url;
    }

    private int getAlbumImageSize() {
        if (mGoodDetailsBean.getProperties()!=null&&mGoodDetailsBean.getProperties().length>0){
            return mGoodDetailsBean.getProperties()[0].getAlbums().length;
        }
        return 0;
    }

    private void getGoodsDetailsByGoodsId(OkHttpUtils2.OnCompleteListener<GoodDetailsBean> listner) {
        OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<>();
                utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                        .addParam(D.GoodDetails.KEY_GOODS_ID,mGoodsId+"")
                        .targetClass(GoodDetailsBean.class)
                        .execute(listner);
    }

    private void initView() {
        DisplyUtils.initBack(mContext);
        mivShare = (ImageView) findViewById(R.id.ivShare);
        mivCollect = (ImageView) findViewById(R.id.ivCollet);
        mivCart = (ImageView) findViewById(R.id.ivAddCart);
        mtvCartCount = (TextView) findViewById(R.id.tvCartCount);
        mtvGoodName = (TextView) findViewById(R.id.tvGoodName);
        mtvEnglishName = (TextView) findViewById(R.id.tvGooodEnglishName);
        mtvPriceShop = (TextView) findViewById(R.id.tvShopPrice);
        mtvPriceCurrent = (TextView) findViewById(R.id.tvCurrencyPrice);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        mwvGoodBrief = (WebView) findViewById(R.id.wvGoodBrief);
        WebSettings settings = mwvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }
}
