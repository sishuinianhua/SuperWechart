package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.bean.MemberUserAvatar;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.utils.OkHttpUtils2;

/**
 * Created by wdyzyr on 2016/7/20.
 */
public class DownloadMemberMapTask {
    private static final String TAG = DownloadContactListTask.class.getSimpleName();
    Context context;
    String hxId;

    public DownloadMemberMapTask(Context context, String hxId) {
        this.context = context;
        this.hxId = hxId;
    }

    public void execute() {
        final OkHttpUtils2<Result> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID)
                .addParam(I.Member.GROUP_HX_ID,hxId)
                .targetClass(Result.class)
                .execute(new OkHttpUtils2.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        String retJson=result.getRetData().toString();
                        Gson gson = new Gson();
                        MemberUserAvatar[] muaArr=gson.fromJson(retJson, MemberUserAvatar[].class);
                        Log.e(TAG, "muaArr=" + Arrays.toString(muaArr));
                        ArrayList<MemberUserAvatar> muaList=utils.array2List(muaArr);
                        if (muaList!=null&&muaList.size()>0){
                            Log.e(TAG, "muaList=" + muaList.toString());
                           Map<String,HashMap<String,MemberUserAvatar>>hxmuaMap= SuperWeChatApplication.getInstance().getMuaMap();
                            if (!hxmuaMap.containsKey(hxId)){
                                hxmuaMap.put(hxId, new HashMap<String, MemberUserAvatar>());
                            }
                            HashMap<String,MemberUserAvatar>muaMap=hxmuaMap.get(hxId);

                            for (MemberUserAvatar mua:muaList){
                                muaMap.put(mua.getMUserName(), mua);
                            }
                            context.sendStickyBroadcast(new Intent("update_member_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }
}

