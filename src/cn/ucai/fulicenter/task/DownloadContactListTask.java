package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.OkHttpUtils2;

/**
 * Created by wdyzyr on 2016/7/20.
 */
public class DownloadContactListTask {
    private static final String TAG = DownloadContactListTask.class.getSimpleName();
    Context context;
    String username;

    public DownloadContactListTask(Context context, String username) {
        this.context = context;
        this.username = username;
    }

    public void execute() {
        final OkHttpUtils2<Result> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,username)
                .targetClass(Result.class)
                .execute(new OkHttpUtils2.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result!=null){
                        String retJson=result.getRetData().toString();
                        Gson gson = new Gson();
                        UserAvatar[] uaArr=gson.fromJson(retJson, UserAvatar[].class);
                        Log.e(TAG, "uaArr=" + Arrays.toString(uaArr));
                        ArrayList<UserAvatar> uaList=utils.array2List(uaArr);
                        if (uaList!=null&&uaList.size()>0){
                            Log.e(TAG, "uaList=" + uaList.toString());
                            FuliCenterApplication.getInstance().setUserContactList(uaList);
                            context.sendStickyBroadcast(new Intent("update_contact_list"));
                            Map<String,UserAvatar> uaMap= FuliCenterApplication.getInstance().getContactMap();
                            for (UserAvatar ua:uaList){
                                uaMap.put(ua.getMUserName(), ua);
                            }
                        }
                     }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }
}
