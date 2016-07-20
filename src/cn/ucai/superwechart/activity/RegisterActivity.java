/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechart.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import java.io.File;

import cn.ucai.superwechart.I;
import cn.ucai.superwechart.R;
import cn.ucai.superwechart.SuperWeChatApplication;
import cn.ucai.superwechart.bean.Result;
import cn.ucai.superwechart.listener.OnSetAvatarListener;
import cn.ucai.superwechart.utils.OkHttpUtils2;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends BaseActivity {

	private static  final String TAG=RegisterActivity.class.getSimpleName();
	private EditText userNameEditText;
	private EditText userNickEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	private Button mButton;
	private ImageView mAvatar;
	private EditText mEtAvataeName;
	private PopupWindow mPopupWindow;
	 ProgressDialog pd;
	private String avatarName;
	private OnSetAvatarListener mOnSetAvatarListener;
	private RelativeLayout mReUpAvatar;

	 String userNick;
	 String username;
	 String pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();

		setListener();
	}

	private void initView() {
		userNickEditText= (EditText) findViewById(R.id.etNick);
		userNameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
		mButton = (Button) findViewById(R.id.btn_login);
		mReUpAvatar= (RelativeLayout) findViewById(R.id.rlRegisterAvatar);

		mAvatar= (ImageView) findViewById(R.id.ivAvatar);

	}

	private void setListener() {
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mReUpAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnSetAvatarListener=new OnSetAvatarListener(RegisterActivity.this,R.id.llRegisterPupob,getAvatarName(), I.AVATAR_TYPE_USER_PATH);
			}

		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode!=RESULT_OK){
			return;
		}
		mOnSetAvatarListener.setAvatar(requestCode,data,mAvatar);
	}

	/**
	 * 头像上传PopupWindow
	 */


	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		userNick=userNickEditText.getText().toString().trim();
		username = userNameEditText.getText().toString().trim();
		pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();
		if (!username.matches("[\\w][\\w\\d_]+")) {
			Toast.makeText(this, getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		}else if(TextUtils.isEmpty(userNick)){
			Toast.makeText(this, getResources().getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
			userNickEditText.requestFocus();
			return;
		}
		else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		}
		else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			 pd = new ProgressDialog(this);
			pd.setMessage(getResources().getString(R.string.Is_the_registered));
			pd.show();
			registupAvatar();

		}
	}

	private void registupAvatar() {
		File file = new File(OnSetAvatarListener.getAvatarPath(RegisterActivity.this, I.AVATAR_TYPE_USER_PATH), avatarName + I.AVATAR_SUFFIX_JPG);
		OkHttpUtils2<Result>utils=new OkHttpUtils2<>();
		utils.setRequestUrl(I.REQUEST_REGISTER)
				.addParam(I.User.USER_NAME,username)
				.addParam(I.User.NICK,userNick)
				.addParam(I.User.PASSWORD,pwd)
				.targetClass(Result.class)
				.addFile(file)
				.execute(new OkHttpUtils2.OnCompleteListener<Result>() {
					@Override
					public void onSuccess(Result result) {
						Log.e(TAG,"Result"+result);
						if(result.isRetMsg()){
							regsuperwechat();
						}else {
							Log.e(TAG,"register ..."+result.getRetCode());
							pd.dismiss();
						}
					}

					@Override
					public void onError(String error) {

					}
				});
	}

	private void regsuperwechat(){

			new Thread(new Runnable() {
				public void run() {
					try {
						// 调用sdk注册方法
						EMChatManager.getInstance().createAccountOnServer(username, pwd);
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								// 保存用户名
								SuperWeChatApplication.getInstance().setUserName(username);
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
								finish();
							}
						});
					} catch (final EaseMobException e) {
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								int errorCode=e.getErrorCode();
								if(errorCode==EMError.NONETWORK_ERROR){
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
								}else if(errorCode == EMError.USER_ALREADY_EXISTS){
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
								}else if(errorCode == EMError.UNAUTHORIZED){
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
								}else if(errorCode == EMError.ILLEGAL_USER_NAME){
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			}).start();

		}


	/**
	 * 返回
	 * @param view
     */
	/*public void onAvatar(View view){
		View view1 = View.inflate(this, R.layout.activity_main, null);
		mPopupWindow.showAtLocation(view1, Gravity.BOTTOM,0,0);
	}*/
	public void back(View view) {
		finish();
	}


	private String getAvatarName(){
		 avatarName = String.valueOf(System.currentTimeMillis());
		return  avatarName;
	}
}
