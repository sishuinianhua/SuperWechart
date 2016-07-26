package cn.ucai.superwechart.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import cn.ucai.superwechart.I;
import cn.ucai.superwechart.SuperWeChatApplication;
import cn.ucai.superwechart.activity.LoginActivity;
import cn.ucai.superwechart.bean.GroupAvatar;
import cn.ucai.superwechart.bean.Result;
import cn.ucai.superwechart.bean.UserAvatar;
import cn.ucai.superwechart.utils.OkHttpUtils2;

public class DownloadGroupListTask {
    private static final String TAG = DownloadGroupListTask.class.getSimpleName();
    Context context;
    String username;

    public DownloadGroupListTask(Context context, String username) {
        this.context = context;
        this.username = username;
    }

    public void execute() {
        final OkHttpUtils2<Result> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_GROUP_BY_USER_NAME)
                .addParam(I.User.USER_NAME,username)
                .targetClass(Result.class)
                .execute(new OkHttpUtils2.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        String retJson=result.getRetData().toString();
                        Gson gson = new Gson();
                        GroupAvatar[] gaArr=gson.fromJson(retJson, GroupAvatar[].class);
                        Log.e(TAG, "gaArr=" + Arrays.toString(gaArr));
                        ArrayList<GroupAvatar> gaList=utils.array2List(gaArr);
                        if (gaList!=null&&gaList.size()>0){
                            Log.e(TAG, "gaList=" + gaList.toString());
                            SuperWeChatApplication.getInstance().setGaList(gaList);
                            context.sendStickyBroadcast(new Intent("update_group_list"));
                           /* Map<String,UserAvatar> uaMap=SuperWeChatApplication.getInstance().getContactMap();
                            for (GroupAvatar ga:gaList){
                                uaMap.put(ga.getMUserName(), ga);
                            }*/
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }
}
