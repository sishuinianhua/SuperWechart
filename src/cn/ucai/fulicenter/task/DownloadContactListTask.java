package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.bean.Contact;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.OkHttpUtils2;

public class DownloadContactListTask {
    private static final String TAG = DownloadContactListTask.class.getSimpleName();
    Context context;
    String username;

    public DownloadContactListTask(Context context, String username) {
        this.context = context;
        this.username = username;
    }

    public void execute() {
        final OkHttpUtils2<Contact[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,username)
                .targetClass(Contact[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<Contact[]>() {
                    @Override
                    public void onSuccess(Contact[] contactArr) {
                        Log.e(TAG, "contactArr=" + Arrays.toString(contactArr));
                        if (contactArr!=null&&contactArr.length>0){
                        ArrayList<Contact> contactList=utils.array2List(contactArr);
                            FuliCenterApplication.getInstance().setUserContactList( contactList);
                            context.sendStickyBroadcast(new Intent("update_contact_list"));
                            Map<String,Contact> uaMap= FuliCenterApplication.getInstance().getContactMap();
                            for (Contact contact:contactList){
                                uaMap.put(contact.getMUserName(), contact);
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
