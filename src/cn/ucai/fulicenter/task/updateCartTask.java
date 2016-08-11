package cn.ucai.fulicenter.task;

import android.content.Context;
import android.util.Log;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;

public class UpdateCartTask {
    private static final String TAG = DownloadCartListTask.class.getSimpleName();
    Context mContext;
    CartBean mCartBean;

    public UpdateCartTask(Context context, CartBean cartBean) {
        mContext = context;
        mCartBean = cartBean;
    }
    public void execute() {
        updateCartBean();
    }

    private void updateCartBean(){
        OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_UPDATE_CART)
                .addParam(I.Cart.ID,String.valueOf(mCartBean.getId()))
                .addParam(I.Cart.COUNT,String.valueOf(mCartBean.getCount()))
                .addParam(I.Cart.IS_CHECKED,String.valueOf(mCartBean.isChecked()))
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean messageBean) {
                        if (messageBean!=null&&messageBean.isSuccess()){
                            new DownloadCartListTask(mContext,mCartBean.getUserName()).execute();
                            Log.e(TAG, "updateCartBean");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });

    }

}
