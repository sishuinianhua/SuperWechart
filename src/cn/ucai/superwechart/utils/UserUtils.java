package cn.ucai.superwechart.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.superwechart.I;
import cn.ucai.superwechart.SuperWeChatApplication;
import cn.ucai.superwechart.applib.controller.HXSDKHelper;
import cn.ucai.superwechart.DemoHXSDKHelper;

import cn.ucai.superwechart.R;
import cn.ucai.superwechart.bean.UserAvatar;
import cn.ucai.superwechart.domain.User;
import com.squareup.picasso.Picasso;

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
        if (path!=null&&username!=null){
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
        .append(I.NAME_OR_HXID)
        .append(I.EQU)
        .append(username)
        .append(I.AND)
        .append(I.AVATAR_TYPE)
        .append(I.EQU)
        .append(I.AVATAR_TYPE_USER_PATH);
        return path.toString();
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
        UserAvatar ua = getContactInfo(username);
        if (ua!=null){
            if (ua.getMUserNick()!=null){
                nameTextview.setText(ua.getMUserNick());
            }else {
                nameTextview.setText(username);
            }
        }else {
            nameTextview.setText(username);
    }
    }

    private static UserAvatar getContactInfo(String username) {
        UserAvatar ua = SuperWeChatApplication.getInstance().getContactMap().get(username);
        if (ua==null){
            ua = new UserAvatar(username);
        }
        return ua;
    }

    /**
     * 设置当前用户昵称
     */
    public static void setAppCurrentUserNick(TextView textView) {
        UserAvatar ua = SuperWeChatApplication.getInstance().getUa();
        if (ua!=null&&textView!=null){
            if (ua.getMUserNick()!=null){
                textView.setText(ua.getMUserNick());
            }else {
                textView.setText(ua.getMUserName());
            }
        }
    }


    public static void setAppCurrentAvatar(Context context, ImageView imageView) {
        String username=SuperWeChatApplication.getInstance().getUserName();
        setContactAvatar(context,username,imageView);
    }
}
