package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.Contact;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;

public class DownloadCartListTask {
    private static final String TAG = DownloadCartListTask.class.getSimpleName();
    Context context;
    String username;

    public DownloadCartListTask(Context context, String username) {
        this.context = context;
        this.username = username;
    }

    public void execute() {
        final OkHttpUtils2<CartBean[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_CARTS)
                .addParam(I.Cart.USER_NAME,username)
                .addParam(I.PAGE_ID,String.valueOf(I.PAGE_ID_DEFAULT))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CartBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<CartBean[]>() {
                    @Override
                    public void onSuccess(CartBean[] cartBeenArr) {
                        Log.e(TAG, "cartBeenArr=" + Arrays.toString(cartBeenArr));
                        if (cartBeenArr!=null&&cartBeenArr.length>0){
                            ArrayList<CartBean> cartBeenList0=utils.array2List(cartBeenArr);
                            ArrayList<CartBean> cartBeenList1 = FuliCenterApplication.getInstance().getCartBeanList();
                            for (final CartBean cartBean:cartBeenList0){
                                if (!cartBeenList1.contains(cartBean)){
                                    OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<>();
                                    utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                                            .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(cartBean.getGoodsId()))
                                            .targetClass(GoodDetailsBean.class)
                                            .execute(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                                                @Override
                                                public void onSuccess(GoodDetailsBean goodDetailsBean) {
                                                    cartBean.setGoods(goodDetailsBean);
                                                }

                                                @Override
                                                public void onError(String error) {
                                                    Log.e(TAG, "error=" + error);
                                                }
                                            });

                                    cartBeenList1.add(cartBean);
                                }else {
                                    cartBeenList1.get(cartBeenList1.indexOf(cartBean)).setChecked(cartBean.isChecked());
                                    cartBeenList1.get(cartBeenList1.indexOf(cartBean)).setCount(cartBean.getCount());

                                }
                            }
                            context.sendStickyBroadcast(new Intent("update_cart_list"));
                            Log.e(TAG, " context.sendStickyBroadcast(new Intent(\"update_cart_list\"));" );
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }
}
