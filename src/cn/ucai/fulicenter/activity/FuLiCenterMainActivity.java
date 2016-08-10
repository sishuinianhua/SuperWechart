package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.fragments.BoutiqueFragment;
import cn.ucai.fulicenter.fragments.CartFragment;
import cn.ucai.fulicenter.fragments.CategoryFragment;
import cn.ucai.fulicenter.fragments.PersonnelCenterFragment;
import cn.ucai.fulicenter.fragments.NewgoodsFragment;
import cn.ucai.fulicenter.utils.UserUtils;

public class FuLiCenterMainActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = FuLiCenterMainActivity.class.getSimpleName();
    TextView mtvNewGoods,mtvBoutique,mtvCategory,mtvCart, mtvContact;
    FrameLayout mflCart;
    TextView mtvCartHint;
    ViewPager mvp;
    Fragment[] mFragmentArr;
    TextView[] mtvArr;
    int[] mSelectedArr;
    int[] mNormalArr;
    CartcountReceiver mReceiver;
    int position=0;
    ArrayList<Integer> positionList;
    int lastPosition;
    boolean isLoging;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fulicenter);
        initFragment();
        initView();
        initData();
        setListener();
    }

    private void initFragment() {
        mFragmentArr = new Fragment[]{new NewgoodsFragment(),new BoutiqueFragment(),new CategoryFragment(),
                new CartFragment(),new PersonnelCenterFragment()};
    }

    private void initView() {
        mtvNewGoods = (TextView) findViewById(R.id.tvAppLableNewgoods);
        mtvBoutique = (TextView) findViewById(R.id.tvAppLableBoutique);
        mtvCategory = (TextView) findViewById(R.id.tvAppLableCategory);
        mtvCart = (TextView) findViewById(R.id.tvAppLableCart);
        mflCart = (FrameLayout) findViewById(R.id.flAppLabelCart);
        mtvContact = (TextView) findViewById(R.id.tvAppLableContact);
        mtvCartHint = (TextView) findViewById(R.id.tvAppLabelCartHint);
        mvp = (ViewPager) findViewById(R.id.vp);
        MyPGAdapter adapter=new MyPGAdapter(getSupportFragmentManager(),mFragmentArr);
        mvp.setAdapter(adapter);
    }

    private void initData() {
        mtvArr = new TextView[]{mtvNewGoods, mtvBoutique, mtvCategory, mtvCart, mtvContact};
        mSelectedArr = new int[]{R.drawable.menu_item_new_good_selected, R.drawable.boutique_selected, R.drawable.menu_item_category_selected,
                 R.drawable.menu_item_cart_selected, R.drawable.menu_item_personal_center_selected};
        mNormalArr = new int[]{R.drawable.menu_item_new_good_normal,R.drawable.boutique_normal,R.drawable.menu_item_category_normal
                ,R.drawable.menu_item_cart_normal,R.drawable.menu_item_personal_center_normal};
    }

    private void setListener() {
        registerCartcountReceiver();
        mtvNewGoods.setOnClickListener(this);
        mtvBoutique.setOnClickListener(this);
        mtvCategory.setOnClickListener(this);
        mflCart.setOnClickListener(this);
        mtvContact.setOnClickListener(this);
         positionList = new ArrayList<>();
        positionList.add(position);

        if(!DemoHXSDKHelper.getInstance().isLogined()){
            changeItemState(mtvArr[lastPosition],mSelectedArr[lastPosition],Color.rgb(0xff, 0x66, 0xff));
            mvp.setCurrentItem(lastPosition);
        }
        changeItemState(mtvArr[position],mSelectedArr[position],Color.rgb(0xff, 0x66, 0xff));
        mvp.setCurrentItem(position);
        mvp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i=0;i<mtvArr.length;i++){
                    changeItemState(mtvArr[i],mNormalArr[i],Color.BLACK);
                }
                changeItemState(mtvArr[position],mSelectedArr[position],Color.rgb(0xff, 0x66, 0xff));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


}

    private void registerCartcountReceiver() {
        mReceiver=new CartcountReceiver();
        IntentFilter filter=new IntentFilter("update_cart_list");
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tvAppLableNewgoods:
                position = 0;
                break;
            case R.id.tvAppLableBoutique:
                position = 1;
                break;
            case R.id.tvAppLableCategory:
                position = 2;
                break;
            case R.id.flAppLabelCart:
                position = 3;
                break;
            case R.id.tvAppLableContact:
                if (DemoHXSDKHelper.getInstance().isLogined()){
                    position = 4;
                }else {
                    startActivity(new Intent(this,LoginActivity.class));
                    lastPosition = positionList.get(positionList.size()-1);
                    Log.e(TAG, "lastPosition=" + lastPosition);
                    // isLoging = true;
                }
                break;
        }
       /* if (isLoging){
            return;
        }*/
        positionList.add(position);
        Log.e(TAG, "position=" + position);

        for (int i=0;i<mtvArr.length;i++){
            changeItemState(mtvArr[i],mNormalArr[i],Color.BLACK);
        }
        changeItemState(mtvArr[position],mSelectedArr[position],Color.rgb(0xff, 0x66, 0xff));

        mvp.setCurrentItem(position);
    }

    private void changeItemState(TextView tv, int id, int color) {
        tv.setTextColor(color);
        Drawable drawableTop=ContextCompat.getDrawable(this,id);
        drawableTop.setBounds(0,0,drawableTop.getMinimumWidth(),drawableTop.getMinimumHeight());
        tv.setCompoundDrawables(null,drawableTop,null,null);


    }

    private class MyPGAdapter extends FragmentPagerAdapter{
        Fragment[] fragmentArr;
        public MyPGAdapter(FragmentManager fm, Fragment[] fragmentArr) {
            super(fm);
            this.fragmentArr = fragmentArr;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArr[position];
        }

        @Override
        public int getCount() {
            return fragmentArr.length;
        }
    }


    private class CartcountReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            UserUtils.setCartCount(mtvCartHint);
        }
    }
}
