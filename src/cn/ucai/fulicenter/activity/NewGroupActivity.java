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
package cn.ucai.fulicenter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.listener.OnSetAvatarListener;
import cn.ucai.fulicenter.utils.OkHttpUtils2;

import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;

import java.io.File;

public class NewGroupActivity extends BaseActivity {
	private static final int CREATE_GROUP = 100;
	private static final String TAG = NewGroupActivity.class.getSimpleName();
	private EditText groupNameEditText;
	private ProgressDialog progressDialog;
	private EditText introductionEditText;
	private CheckBox checkBox;
	private CheckBox memberCheckbox;
	private LinearLayout openInviteContainer;
	private ImageView avatar;
	OnSetAvatarListener mOnSetAvatarListener;
	private String avatarName;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_group);
		groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
		introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
		checkBox = (CheckBox) findViewById(R.id.cb_public);
		memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
		openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);
		avatar = (ImageView) findViewById(R.id.iv_group_avatar);

		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					openInviteContainer.setVisibility(View.INVISIBLE);
				}else{
					openInviteContainer.setVisibility(View.VISIBLE);
				}
			}
		});
		findViewById(R.id.ll_group_avatar).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnSetAvatarListener = new OnSetAvatarListener(NewGroupActivity.this,R.id.ll_parentLayout, getAvatarName(), I.AVATAR_TYPE_GROUP_PATH);
			}
		});
	}

	/**
	 * @param v
	 */
	public void save(View v) {
		String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
		String name = groupNameEditText.getText().toString();
		if (TextUtils.isEmpty(name)) {
			Intent intent = new Intent(this, AlertDialog.class);
			intent.putExtra("msg", str6);
			startActivity(intent);
		} else {
			// 进通讯录选人
			startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), CREATE_GROUP);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


		if (resultCode!=RESULT_OK){
			return;
		}
		mOnSetAvatarListener.setAvatar(requestCode,data,avatar);
		if (requestCode == CREATE_GROUP) {
			createEMGroup(data);
		}
	}

	private void createEMGroup(final Intent data) {
			setDialog();
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 调用sdk创建群组方法
					String groupName = groupNameEditText.getText().toString().trim();
					String desc = introductionEditText.getText().toString();
					String[] members = data.getStringArrayExtra("newmembers");
					EMGroup group;
					try {
						if(checkBox.isChecked()){
							//创建公开群，此种方式创建的群，可以自由加入
							//创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
							group =  EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true,200);
						}else{
							//创建不公开群
							group = EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(), 200);
						}
						Log.e(TAG, "group=" + group.getGroupId());
					} catch (final EaseMobException e) {
						runOnUiThread(new Runnable() {
							public void run() {
								progressDialog.dismiss();
								Toast.makeText(NewGroupActivity.this,  getResources().getString(R.string.Failed_to_create_groups) + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
							}
						});
					}

				}
			}).start();
		}




	private void setDialog() {
		String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(st1);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}


	public void back(View view) {
		finish();
	}

	public String getAvatarName() {
		avatarName = String.valueOf(System.currentTimeMillis());
		return avatarName;
	}
}
