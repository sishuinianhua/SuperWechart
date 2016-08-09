package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.Message;
import cn.ucai.fulicenter.utils.OkHttpUtils2;

public class DownloadCollectGoodsCountTask {
    private static final String TAG = DownloadCollectGoodsCountTask.class.getSimpleName();
    Context context;
    String username;

    public DownloadCollectGoodsCountTask(Context context, String username) {
        this.context = context;
        this.username = username;
    }
    public void execute() {
        OkHttpUtils2<Message> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_COLLECT_COUNT)
                .addParam(I.Collect.USER_NAME,username)
                .targetClass(cn.ucai.fulicenter.bean.Message.class)
                .execute(new OkHttpUtils2.OnCompleteListener<cn.ucai.fulicenter.bean.Message>() {
                    @Override
                    public void onSuccess(cn.ucai.fulicenter.bean.Message msg) {
                        if (msg.getSuccess()){
                            Log.e(TAG, "msg.getMsg()=" + msg.getMsg());
                            FuliCenterApplication.getInstance().setCollectGoodsCount(Integer.parseInt( msg.getMsg())) ;
                        }else {
                            FuliCenterApplication.getInstance().setCollectGoodsCount(0) ;
                        }
                        context.sendStickyBroadcast(new Intent("update_collect_count"));
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });

    }
}
