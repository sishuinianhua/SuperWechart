package cn.ucai.fulicenter.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;

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

    ExpandableListView melvCategory;
    CategoryAdapter mAdapter;
    Context mContext=getContext();
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
        final OkHttpUtils2<CategoryParentBean[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP)
                .targetClass(CategoryParentBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<CategoryParentBean[]>() {
                    @Override
                    public void onSuccess(CategoryParentBean[] result) {
                        if (result!=null){
                            utils.array2List(result);
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    private void initView(View layout) {
        melvCategory = (ExpandableListView) layout.findViewById(R.id.elvCategory);
        mGroupList=new ArrayList<>();
        mChildList=new ArrayList<>();
        mAdapter=new CategoryAdapter(mContext,mGroupList,mChildList);
        melvCategory.setAdapter(mAdapter);
    }

}
