package cn.ucai.fulicenter.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.LoginActivity;
import cn.ucai.fulicenter.bean.AlbumBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.task.DownloadCollectGoodsCountTask;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.UserUtils;
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
    boolean isCollect;
    CartCountReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goods_details);
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        CollectListener listener=new CollectListener();
        mivCollect.setOnClickListener(listener);
        mivShare.setOnClickListener(listener);
        registerCartCountReceiver();
    }

    private void registerCartCountReceiver() {
         mReceiver=new CartCountReceiver();
        IntentFilter filter=new IntentFilter("update_cart_list");
        registerReceiver(mReceiver, filter);
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

    private class CollectListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ivCollet:
                    if (DemoHXSDKHelper.getInstance().isLogined()){
                        if (isCollect){
                            OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<>();
                            utils.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                                    .addParam(I.Collect.USER_NAME, FuliCenterApplication.getInstance().getUserName())
                                    .addParam(I.Collect.GOODS_ID, String.valueOf(mGoodsId))
                                    .targetClass(MessageBean.class)
                                    .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                                        @Override
                                        public void onSuccess(MessageBean messageBean) {
                                            Log.e(TAG, "messageBean=" + messageBean);
                                            if (messageBean!=null&&messageBean.isSuccess()){
                                                isCollect = false;
                                                new DownloadCollectGoodsCountTask(mContext, FuliCenterApplication.getInstance().getUserName()).execute();
                                            }else {
                                                Log.e(TAG, "取消关注失败");
                                            }
                                            updateCollectStatus();
                                            Toast.makeText(mContext,messageBean.getMsg(),Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.e(TAG, "error=" + error);
                                        }
                                    });
                        }else {
                            OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<>();
                            utils.setRequestUrl(I.REQUEST_ADD_COLLECT)
                                    .addParam(I.Collect.USER_NAME,FuliCenterApplication.getInstance().getUserName())
                                    .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodDetailsBean.getGoodsId()))
                                    .addParam(I.Collect.ADD_TIME,String.valueOf(mGoodDetailsBean.getAddTime()))
                                    .addParam(I.Collect.GOODS_ENGLISH_NAME,mGoodDetailsBean.getGoodsEnglishName())
                                    .addParam(I.Collect.GOODS_IMG,mGoodDetailsBean.getGoodsImg())
                                    .addParam(I.Collect.GOODS_THUMB,mGoodDetailsBean.getGoodsThumb())
                                    .addParam(I.Collect.GOODS_NAME,mGoodDetailsBean.getGoodsName())
                                    .targetClass(MessageBean.class)
                                    .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                                        @Override
                                        public void onSuccess(MessageBean messageBean) {
                                            Log.e(TAG,"messageBean=" + messageBean);
                                            if (messageBean!=null&&messageBean.isSuccess()){
                                                isCollect = true;
                                                new DownloadCollectGoodsCountTask(mContext,FuliCenterApplication.getInstance().getUserName()).execute();
                                            } else {
                                                Log.e(TAG,"添加关注失败");
                                            }
                                            updateCollectStatus();
                                            Toast.makeText(mContext,messageBean.getMsg(),Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.e(TAG, "error=" + error);
                                        }
                                    });
                        }
                    }else {
                        startActivity(new Intent(mContext, LoginActivity.class));
                    }
                    break;
                case R.id.ivShare:
                    showShare();
                    break;

            }
        }
    }

    private void updateCollectStatus() {
        if (isCollect){
            mivCollect.setImageResource(R.drawable.bg_collect_out);
        }else {
            mivCollect.setImageResource(R.drawable.bg_collect_in);
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(mGoodDetailsBean.getShareUrl());
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mGoodDetailsBean.getGoodsName());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(mGoodDetailsBean.getShareUrl());
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(mGoodDetailsBean.getGoodsName());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(mGoodDetailsBean.getShareUrl());

// 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private class CartCountReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, " UserUtils.setCartCount(mtvCartCount);" );
            UserUtils.setCartCount(mtvCartCount);
        }
    }
}
