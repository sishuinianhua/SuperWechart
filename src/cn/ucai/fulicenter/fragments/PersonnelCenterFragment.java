package cn.ucai.fulicenter.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.activity.SettingsActivity;
import cn.ucai.fulicenter.bean.Message;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.UserUtils;
import cn.ucai.fulicenter.view.DisplyUtils;


public class PersonnelCenterFragment extends Fragment {

    private static final String TAG = PersonnelCenterFragment.class.getSimpleName();
    ImageView ivmUserAvatar,ivmPersonalCenterMsg;
    RelativeLayout mrlCenterUseIinfo;
    TextView  mtvUseName,mtvCollecCount,mtvCenterSettings;
    LinearLayout mllLayoytCenterCollect;
    GridView mgvCenterUserOrderLlist;
    Context mContext;
    CollectCountReceiver mReceiver;

    public PersonnelCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout= inflater.inflate(R.layout.fragment_personnel_center, container, false);
        initData();
        initView(layout);
        initListener();
        return layout;
    }

    private void initListener() {
        MyClickListener listener=new MyClickListener();
        mtvCenterSettings.setOnClickListener(listener);
        mrlCenterUseIinfo.setOnClickListener(listener);
        mllLayoytCenterCollect.setOnClickListener(listener);
        registerBroadCaster();
    }

    private void registerBroadCaster() {
        mReceiver=new CollectCountReceiver();
        IntentFilter filter=new IntentFilter("update_collect_count");
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }

    private void initData() {
        mContext = getContext();
    }

    private void initView(View layout) {
        ivmUserAvatar = (ImageView) layout.findViewById(R.id.iv_user_avatar);
        UserUtils.setAppCurrentAvatar(mContext,ivmUserAvatar);
        ivmPersonalCenterMsg = (ImageView) layout.findViewById(R.id.iv_personal_center_msg);
        mtvUseName = (TextView) layout.findViewById(R.id.tv_user_name);
        mtvUseName.setText(FuliCenterApplication.getInstance().getUa().getMuserNick());
        mrlCenterUseIinfo = (RelativeLayout) layout.findViewById(R.id.center_user_info);
        mtvCollecCount = (TextView) layout.findViewById(R.id.tv_collect_count);
        mllLayoytCenterCollect = (LinearLayout) layout.findViewById(R.id.layoyt_center_collect);
        mtvCenterSettings = (TextView) layout.findViewById(R.id.tv_center_settings);
        initOrderList(layout);

    }



    private void initOrderList(View layout) {
        mgvCenterUserOrderLlist = (GridView) layout.findViewById(R.id.center_user_order_list);
        ArrayList<HashMap<String, Object>> date=new ArrayList<>();
        HashMap<String, Object> order1 = new HashMap<>();
        order1.put("order", R.drawable.order_list1);
        date.add(order1);
        HashMap<String, Object> order2 = new HashMap<>();
        order2.put("order", R.drawable.order_list2);
        date.add(order2);
        HashMap<String, Object> order3 = new HashMap<>();
        order3.put("order", R.drawable.order_list3);
        date.add(order3);
        HashMap<String, Object> order4 = new HashMap<>();
        order4.put("order", R.drawable.order_list4);
        date.add(order4);
        HashMap<String, Object> order5 = new HashMap<>();
        order5.put("order", R.drawable.order_list5);
        date.add(order5);

        SimpleAdapter adpter=new SimpleAdapter(mContext,date,R.layout.order_list_holder,
                new String[]{"order"},new int[]{R.id.ivOrderList});
        mgvCenterUserOrderLlist.setAdapter(adpter);
    }


    private class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (DemoHXSDKHelper.getInstance().isLogined()) {
                switch (v.getId()) {
                    case R.id.tv_center_settings:
                    case R.id.center_user_info:
                        startActivity(new Intent(mContext, SettingsActivity.class));
                        break;
                    case R.id.layoyt_center_collect:
                        startActivity(new Intent(mContext,CollectGoodsActivity.class));
                        break;
                }
            }
        }
    }

    private class CollectCountReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int collectGoodsCount=FuliCenterApplication.getInstance().getCollectGoodsCount();
            Log.e(TAG, "collectGoodsCount=" + collectGoodsCount);
            mtvCollecCount.setText(String.valueOf(collectGoodsCount));
        }
    }
}
