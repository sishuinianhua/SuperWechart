package cn.ucai.fulicenter.utils;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import cn.ucai.fulicenter.DemoHXSDKHelper;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.Contact;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.domain.User;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class UserUtils {
    private static final String TAG = UserUtils.class.getSimpleName();

    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
        User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(username);
        if(user == null){
            user = new User(username);
        }


        if(user != null){
            //demo没有这些数据，临时填充
        	if(TextUtils.isEmpty(user.getNick()))
        		user.setNick(username);
        }
        return user;
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	User user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }
    
    /**
     * 设置当前用户头像
     */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
		User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if (user != null && user.getAvatar() != null) {
			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	User user = getUserInfo(username);
    	if(user != null){
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(username);
    	}
    }
    
    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView){
    	User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
    	if(textView != null){
    		textView.setText(user.getNick());
    	}
    }
    
    /**
     * 保存或更新某个用户
     * @param
     */
	public static void saveUserInfo(User newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
	}

    public static void setContactAvatar(Context context, String username, ImageView avatar) {
        String path = "";
        if (username!=null){
            path = getContactAvatarPath(username);
            Log.e(TAG, "path=" + path);
            Picasso.with(context).load(path).placeholder(R.drawable.default_avatar).into(avatar);
        }else {
            Picasso.with(context).load(R.drawable.default_avatar).into(avatar);
        }
    }

    public static String getContactAvatarPath(String username) {
        StringBuilder path=new StringBuilder(I.SERVER_ROOT);
        path.append(I.QUESTION)
        .append(I.KEY_REQUEST)
        .append(I.EQU)
        .append(I.REQUEST_DOWNLOAD_AVATAR)
        .append(I.AND)
        /*.append(I.NAME_OR_HXID)
        .append(I.EQU)
        .append(username)
        .append(I.AND)*/
        .append(I.AVATAR_TYPE)
        .append(I.EQU)
        .append(username);
        return path.toString();
      //  http://192.168.7.7:8080/FuLiCenterServer/Server?request=download_avatar&avatarType=aa
    }
    /*设置群组头像*/
    public static void setGroupAvatar(Context context, String hxId, ImageView avatar) {
        String path = "";
        if (hxId!=null){
            path = getGroupAvatarPath(hxId);
            Log.e(TAG, "path=" + path);
            Picasso.with(context).load(path).placeholder(R.drawable.group_icon).into(avatar);
        }else {
            Picasso.with(context).load(R.drawable.group_icon).into(avatar);
        }
    }

    public static String getGroupAvatarPath(String hxId) {
        StringBuilder path=new StringBuilder(I.SERVER_ROOT);
        path.append(I.QUESTION)
        .append(I.KEY_REQUEST)
        .append(I.EQU)
        .append(I.REQUEST_DOWNLOAD_AVATAR)
        .append(I.AND)
        .append(I.NAME_OR_HXID)
        .append(I.EQU)
        .append(hxId)
        .append(I.AND)
        .append(I.AVATAR_TYPE)
        .append(I.EQU)
        .append(I.AVATAR_TYPE_GROUP_PATH);
        return path.toString();
    }

    public static void setContactNick(String username, TextView nameTextview) {
        Contact contact = getContactInfo(username);
        if (contact!=null){
            if (contact.getMuserNick()!=null){
                nameTextview.setText(contact.getMuserNick());
            }else {
                nameTextview.setText(username);
            }
        }else {
            nameTextview.setText(username);
    }
    }

    private static Contact getContactInfo(String username) {
      Contact contact = FuliCenterApplication.getInstance().getContactMap().get(username);
        if (contact==null){
            contact = new Contact(username);
        }return contact;
    }

    /**
     * 设置当前用户昵称
     */
    public static void setAppCurrentUserNick(TextView textView) {
        UserAvatar ua = FuliCenterApplication.getInstance().getUa();
        if (ua!=null&&textView!=null){
            if (ua.getMuserNick()!=null){
                textView.setText(ua.getMuserNick());
            }else {
                textView.setText(ua.getMuserName());
            }
        }
    }


    public static void setAppCurrentAvatar(Context context, ImageView imageView) {
        String username= FuliCenterApplication.getInstance().getUserName();
        setContactAvatar(context,username,imageView);
    }

    public static void setAvatar(Context context, String avatarPath, ImageView avatar) {
        String url = "http://192.168.7.7:8080/FuLiCenterServer/Server?request=download_new_good&file_name="+avatarPath;
        Picasso.with(context).load(url).placeholder(R.drawable.nopic).into(avatar);

    }
    public static void setCategoryGroupAvatar(Context context, String avatarPath, ImageView avatar) {
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_GROUP_IMAGE_URL+avatarPath;
        Picasso.with(context).load(url).placeholder(R.drawable.nopic).into(avatar);

    }
    public static void setCategoryChildAvatar(Context context, String avatarPath, ImageView avatar) {
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_CHILD_IMAGE_URL+avatarPath;
        Log.e(TAG, "url=" + url + ",avatar=" + avatar);
        Picasso.with(context).load(url).placeholder(R.drawable.nopic).into(avatar);
    }


}
