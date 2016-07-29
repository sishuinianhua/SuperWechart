package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.google.gson.Gson;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.db.UserDao;
import cn.ucai.fulicenter.task.DownloadContactListTask;
import cn.ucai.fulicenter.task.DownloadGroupListTask;
import cn.ucai.fulicenter.utils.OkHttpUtils2;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {
	private RelativeLayout rootLayout;
	private TextView versionText;
	
	private static final int sleepTime = 2000;
	public static final String TAG = SplashActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_splash);
		super.onCreate(arg0);

		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		versionText = (TextView) findViewById(R.id.tv_version);

		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					// ** 免登陆情况 加载所有本地群和会话
					//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
					//加上的话保证进了主页面会话和群组都已经load完毕
					long start = System.currentTimeMillis();
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					String userName = FuliCenterApplication.getInstance().getUserName();
					Log.e(TAG, "userName=" + userName);
					UserAvatar ua=new UserDao(SplashActivity.this).getUserAvatar(userName);
					Log.e(TAG, "ua=" + ua);
					if(ua==null){
						OkHttpUtils2<Result> utils = new OkHttpUtils2<>();
						utils.setRequestUrl(I.REQUEST_FIND_USER)
								.addParam(I.User.USER_NAME,userName)
								.targetClass(Result.class)
								.execute(new OkHttpUtils2.OnCompleteListener<Result>() {
									@Override
									public void onSuccess(Result result) {
										if (result.isRetMsg()){
											String retJson=result.getRetData().toString();
											Gson gson = new Gson();
											UserAvatar ua=gson.fromJson(retJson, UserAvatar.class);
											Log.e(TAG, "result=" + result);
											if (ua!=null){
												Log.e(TAG, "ua1=" + ua);
												FuliCenterApplication.getInstance().setUa(ua);
												FuliCenterApplication.currentUserNick = ua.getMUserNick();
											}
										}
									}
									@Override
									public void onError(String error) {
										Log.e(TAG, "error=" + error);
																			}
								});
					}else {
						FuliCenterApplication.getInstance().setUa(ua);
						FuliCenterApplication.currentUserNick = ua.getMUserNick();
					}

					new DownloadContactListTask(SplashActivity.this,userName).execute();
					new DownloadGroupListTask(SplashActivity.this,userName).execute();

					long costTime = System.currentTimeMillis() - start;
					//等待sleeptime时长
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//进入主页面
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					finish();
				}
			}
		}).start();

	}
	
	/**
	 * 获取当前应用程序的版本号
	 */
	private String getVersion() {
		String st = getResources().getString(R.string.Version_number_is_wrong);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}
}
