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

import cn.ucai.superwechart.I;
import cn.ucai.superwechart.R;
import cn.ucai.superwechart.SuperWeChatApplication;
import cn.ucai.superwechart.bean.GroupAvatar;
import cn.ucai.superwechart.bean.Result;
import cn.ucai.superwechart.listener.OnSetAvatarListener;
import cn.ucai.superwechart.utils.OkHttpUtils2;

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
						createAppGroup(group.getGroupId(),groupName,desc,members);
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

	private void createAppGroup(final String hxId, String groupName, String desc, final String[] members) {
		boolean isPublic = checkBox.isChecked();
		boolean invites = !isPublic;
		String owner = SuperWeChatApplication.getInstance().getUserName();
		OkHttpUtils2<Result> utils = new OkHttpUtils2<>();
		String dir=OnSetAvatarListener.getAvatarPath(NewGroupActivity.this,I.AVATAR_TYPE_GROUP_PATH);
		String name=avatarName+I.AVATAR_SUFFIX_JPG;
		File file=new File(dir,name);
		utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
				.addParam(I.Group.HX_ID,hxId)
				.addParam(I.Group.NAME,groupName)
				.addParam(I.Group.OWNER,owner)
				.addParam(I.Group.DESCRIPTION,desc)
				.addParam(I.Group.IS_PUBLIC,String.valueOf(isPublic))
				.addParam(I.Group.ALLOW_INVITES,String.valueOf(invites))
				.addFile(file)
				.targetClass(Result.class)
				.execute(new OkHttpUtils2.OnCompleteListener<Result>() {
					@Override
					public void onSuccess(Result result) {
						if (result.isRetMsg()){
							String retData=result.getRetData().toString();
							Gson gson = new Gson();
							GroupAvatar ga=gson.fromJson(retData, GroupAvatar.class);
							addGroupMembers(hxId,members,ga);
							createGroupSuccess(ga);
						}else {
							progressDialog.dismiss();
							Toast.makeText(NewGroupActivity.this, getResources().getString(R.string.Failed_to_create_groups) , Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onError(String error) {
						progressDialog.dismiss();
						Toast.makeText(NewGroupActivity.this,  getResources().getString(R.string.Failed_to_create_groups) + error, Toast.LENGTH_LONG).show();
					}
				});
	}

	private void createGroupSuccess(GroupAvatar ga) {
		SuperWeChatApplication.getInstance().getGaMap().put(ga.getMGroupHxid(), ga);
		SuperWeChatApplication.getInstance().getGaList().add(ga);
		progressDialog.dismiss();
		setResult(RESULT_OK);
		finish();
	}

	private void addGroupMembers(String hxId, String[] members, final GroupAvatar ga) {
		String membersArr1 = "";
		for (String m:members){
			membersArr1 += m+",";
		}
		String membersArr=membersArr1.substring(0, membersArr1.length() - 1);
		OkHttpUtils2<Result> utils = new OkHttpUtils2<Result>();
		utils.setRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS)
				.addParam(I.Member.GROUP_HX_ID,hxId)
				.addParam(I.Member.USER_NAME,membersArr)
				.targetClass(Result.class)
				.execute(new OkHttpUtils2.OnCompleteListener<Result>() {
					@Override
					public void onSuccess(Result result) {
						if (result.isRetMsg()){
							createGroupSuccess(ga);
						}else {
							progressDialog.dismiss();
							Toast.makeText(NewGroupActivity.this,  getResources().getString(R.string.Failed_to_create_groups) , Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onError(String error) {
						progressDialog.dismiss();
						Toast.makeText(NewGroupActivity.this,  getResources().getString(R.string.Failed_to_create_groups) + error, Toast.LENGTH_LONG).show();
					}
				});
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
