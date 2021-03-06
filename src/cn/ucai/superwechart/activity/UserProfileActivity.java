package cn.ucai.superwechart.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMValueCallBack;

import cn.ucai.superwechart.I;
import cn.ucai.superwechart.SuperWeChatApplication;
import cn.ucai.superwechart.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import cn.ucai.superwechart.DemoHXSDKHelper;
import cn.ucai.superwechart.R;
import cn.ucai.superwechart.bean.Result;
import cn.ucai.superwechart.bean.UserAvatar;
import cn.ucai.superwechart.db.UserDao;
import cn.ucai.superwechart.domain.User;
import cn.ucai.superwechart.listener.OnSetAvatarListener;
import cn.ucai.superwechart.utils.OkHttpUtils2;
import cn.ucai.superwechart.utils.UserUtils;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends BaseActivity implements OnClickListener {

		private static final int REQUESTCODE_PICK = 1;
		private static final int REQUESTCODE_CUTTING = 2;
		private static final String TAG = UserProfileActivity.class.getSimpleName();
		private ImageView headAvatar;
		private ImageView headPhotoUpdate;
		private ImageView iconRightArrow;
		private TextView tvNickName;
		private TextView tvUsername;
		private ProgressDialog dialog;
		private RelativeLayout rlNickName;
		private OnSetAvatarListener mOnSetAvatarListener;
		private String avatarName;


		@Override

	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_user_profile);
		initView();
		initListener();
	}

	private void initView() {
		headAvatar = (ImageView) findViewById(R.id.user_head_avatar);
		headPhotoUpdate = (ImageView) findViewById(R.id.user_head_headphoto_update);
		tvUsername = (TextView) findViewById(R.id.user_username);
		tvNickName = (TextView) findViewById(R.id.user_nickname);
		rlNickName = (RelativeLayout) findViewById(R.id.rl_nickname);
		iconRightArrow = (ImageView) findViewById(R.id.ic_right_arrow);

	}

	private void initListener() {
		Intent intent = getIntent();
		String username = intent.getStringExtra("username");
		boolean enableUpdate = intent.getBooleanExtra("setting", false);
		String hxid = intent.getStringExtra("groupId");
		if (enableUpdate) {
			headPhotoUpdate.setVisibility(View.VISIBLE);
			iconRightArrow.setVisibility(View.VISIBLE);
			rlNickName.setOnClickListener(this);
			headAvatar.setOnClickListener(this);
		} else {
			headPhotoUpdate.setVisibility(View.GONE);
			iconRightArrow.setVisibility(View.INVISIBLE);
		}
		if (username == null || username.equals(EMChatManager.getInstance().getCurrentUser())) {
			tvUsername.setText(EMChatManager.getInstance().getCurrentUser());
			UserUtils.setAppCurrentUserNick(tvNickName);
			UserUtils.setCurrentUserAvatar(this, headAvatar);
		}else if (hxid!=null){
			tvUsername.setText(username);
			UserUtils.setAppMemberNick(hxid,username, tvNickName);
			UserUtils.setContactAvatar(this, username, headAvatar);
		}else {
			tvUsername.setText(username);
			UserUtils.setContactNick(username, tvNickName);
			UserUtils.setContactAvatar(this, username, headAvatar);
			//asyncFetchUserInfo(username);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.user_head_avatar:
				//uploadHeadPhoto();
				mOnSetAvatarListener = new OnSetAvatarListener(UserProfileActivity.this, R.id.ll_mine_pubpob, setAvatarName(),I.AVATAR_TYPE_USER_PATH );

				break;
			case R.id.rl_nickname:
				final EditText editText = new EditText(this);
				if (SuperWeChatApplication.getInstance().getUa().getMUserNick()!=null){
					editText.setText(SuperWeChatApplication.getInstance().getUa().getMUserNick());
				}
				new AlertDialog.Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
						.setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(final DialogInterface dialog, int which) {
								final String nickString = editText.getText().toString();
								if (TextUtils.isEmpty(nickString)) {
									Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
									return;
								}

								final OkHttpUtils2<Result> utils = new OkHttpUtils2();
								utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
										.addParam(I.User.USER_NAME, SuperWeChatApplication.getInstance().getUserName())
										.addParam(I.User.NICK, nickString)
										.targetClass(Result.class)
										.execute(new OkHttpUtils2.OnCompleteListener<Result>() {
											@Override
											public void onSuccess(Result result) {
												if (result.isRetMsg()) {
													String retData = result.getRetData().toString();
													Gson gson = new Gson();
													UserAvatar ua = gson.fromJson(retData, UserAvatar.class);
													if (ua != null) {
														SuperWeChatApplication.getInstance().setUa(ua);
														SuperWeChatApplication.currentUserNick = ua.getMUserNick();
														UserDao userDao = new UserDao(UserProfileActivity.this);
														userDao.updateUserNick(ua);
														updateRemoteNick(nickString);
													}
												} else {
													Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT).show();
													dialog.dismiss();
												}
											}

											@Override
											public void onError(String error) {
												Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT).show();
												dialog.dismiss();
											}
										});
							}
						}).setNegativeButton(R.string.dl_cancel, null).show();
				break;
			default:
				break;
		}

	}

	private String setAvatarName() {
		 avatarName = String.valueOf(System.currentTimeMillis());
		return avatarName;
	}

	public void asyncFetchUserInfo(String username) {
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<User>() {

			@Override
			public void onSuccess(User user) {
				if (user != null) {
					tvNickName.setText(user.getNick());
					if (!TextUtils.isEmpty(user.getAvatar())) {
						Picasso.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(headAvatar);
					} else {
						Picasso.with(UserProfileActivity.this).load(R.drawable.default_avatar).into(headAvatar);
					}
					UserUtils.saveUserInfo(user);
				}
			}

			@Override
			public void onError(int error, String errorMsg) {
			}
		});
	}


	private void uploadHeadPhoto() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.dl_title_upload_photo);
		builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
							case 0:
								Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
										Toast.LENGTH_SHORT).show();
								break;
							case 1:
								Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
								pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
								startActivityForResult(pickIntent, REQUESTCODE_PICK);
								break;
							default:
								break;
						}
					}
				});
		builder.create().show();
	}


	private void updateRemoteNick(final String nickName) {
		dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean updatenick = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().updateParseNickName(nickName);
				if (UserProfileActivity.this.isFinishing()) {
					return;
				}
				if (!updatenick) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
									.show();
							dialog.dismiss();
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
									.show();
							tvNickName.setText(nickName);
						}
					});
				}
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*switch (requestCode) {
			case REQUESTCODE_PICK:
				if (data == null || data.getData() == null) {
					return;
				}
				startPhotoZoom(data.getData());
				break;
			case REQUESTCODE_CUTTING:
				if (data != null) {
					setPicToView(data);
				}
				break;
			default:
				break;
		}*/
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode!=RESULT_OK){
			return;
		}

			mOnSetAvatarListener.setAvatar(requestCode,data,headAvatar);
		 if (requestCode==OnSetAvatarListener.REQUEST_CROP_PHOTO){
			uploadUserAvatar(data);
		}

		}

	private void uploadUserAvatar(final Intent picdata) {
		File file = new File(OnSetAvatarListener.getAvatarPath(UserProfileActivity.this,I.AVATAR_TYPE_USER_PATH), avatarName + I.AVATAR_SUFFIX_JPG);
		OkHttpUtils2<Result> utils = new OkHttpUtils2<>();
		utils.setRequestUrl(I.REQUEST_UPLOAD_AVATAR)
				.addParam(I.NAME_OR_HXID,SuperWeChatApplication.getInstance().getUa().getMUserName())
				.addParam(I.AVATAR_TYPE,I.AVATAR_TYPE_USER_PATH)
				.addFile(file)
				.targetClass(Result.class)
				.execute(new OkHttpUtils2.OnCompleteListener<Result>() {
					@Override
					public void onSuccess(Result result) {
						if (result.isRetMsg()){
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
									Toast.LENGTH_SHORT).show();
							setPicToView(picdata);
						}
					}

					@Override
					public void onError(String error) {
						Log.e(TAG, "error=" + error);

					}
				});
	}


	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}

	/**
	 * save the picture data
	 *
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(getResources(), photo);
			headAvatar.setImageDrawable(drawable);
			uploadUserAvatar(Bitmap2Bytes(photo));
		}

	}

	private void uploadUserAvatar(final byte[] data) {
		dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				final String avatarUrl = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().uploadUserAvatar(data);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
						if (avatarUrl != null) {
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
									Toast.LENGTH_SHORT).show();
						}

					}
				});

			}
		}).start();

		dialog.show();
	}


	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
}
