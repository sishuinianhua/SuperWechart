package cn.ucai.fulicenter.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Arrays;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryParentBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    private static final String TAG = CategoryFragment.class.getSimpleName();
    ExpandableListView melvCategory;
    CategoryAdapter mAdapter;
    Context mContext;
    ArrayList<CategoryParentBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_category, container, false);
        initData();
        initView(layout);
        return layout;
    }

    private void initData() {
        mContext = getContext();
        getGroupList();

    }

    private void getChildList(final int i, CategoryParentBean[] result) {
            final OkHttpUtils2<CategoryChildBean[]> utils = new OkHttpUtils2<>();
            utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN);
            utils.addParam(I.CategoryChild.PARENT_ID, result[i].getId() + "");
            utils.addParam(I.PAGE_ID, I.PAGE_ID_DEFAULT + "");
            utils.addParam(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "");
            utils.targetClass(CategoryChildBean[].class);
        utils.execute(new OkHttpUtils2.OnCompleteListener<CategoryChildBean[]>() {
            @Override
            public void onSuccess(CategoryChildBean[] result) {
                if (result != null) {
                    Log.e(TAG, "childResult=" + result.length);
                    ArrayList<CategoryChildBean> list = utils.array2List(result);
                    mChildList.set(i,list);
            }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "error=" + error);
            }
        });}

    private void getGroupList() {
        final OkHttpUtils2<CategoryParentBean[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP)
                .targetClass(CategoryParentBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<CategoryParentBean[]>() {
                    @Override
                    public void onSuccess(CategoryParentBean[] result) {
                        if (result!=null){
                            Log.e(TAG, "GroupResult=" + result.length);
                            ArrayList<CategoryParentBean> groupList = OkHttpUtils2.array2List(result);
                            mGroupList.addAll(groupList);
                            for (int i=0;i<result.length;i++){
                                mChildList.add(new ArrayList<CategoryChildBean>());
                            }
                            for (int i=0;i<result.length;i++){
                                getChildList(i,result);
                            }
                            mAdapter.initDate(mGroupList,mChildList);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }

    private void initView(View layout) {
        melvCategory = (ExpandableListView) layout.findViewById(R.id.elvCategory);
        mGroupList=new ArrayList<>();
        mChildList = new ArrayList<>();
        mAdapter=new CategoryAdapter(mContext,mGroupList,mChildList);
        melvCategory.setAdapter(mAdapter);
        melvCategory.setGroupIndicator(null);
    }

}
