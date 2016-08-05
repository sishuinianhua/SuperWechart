package cn.ucai.fulicenter.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryParentBean;
import cn.ucai.fulicenter.utils.UserUtils;

public class CategoryAdapter extends BaseExpandableListAdapter{
    Context mContext;
    ArrayList<CategoryParentBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;
    public CategoryAdapter(Context context, ArrayList<CategoryParentBean> groupList, ArrayList<ArrayList<CategoryChildBean>> childList) {
        mContext = context;
        mGroupList =new ArrayList<>();
        mChildList = new ArrayList<>();
        mGroupList.addAll(groupList);
        mChildList.addAll(childList);
    }

    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList.get(groupPosition).size();
    }

    @Override
    public CategoryParentBean getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        return mChildList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder = null;
        CategoryParentBean groupBean = mGroupList.get(groupPosition);
        if (convertView==null){
            convertView = View.inflate(mContext, R.layout.category_groupholder, null);
            holder = new GroupHolder();
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.ivGroupAvatar);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvGroupName);
            holder.ivOnOff = (ImageView) convertView.findViewById(R.id.ivGroupOnOff);
            convertView.setTag(holder);
        }else {
            holder= (GroupHolder) convertView.getTag();
        }
        UserUtils.setCategoryGroupAvatar(mContext,groupBean.getImageUrl(),holder.ivAvatar);
        holder.tvName.setText(groupBean.getName());
        if (isExpanded){
            holder.ivOnOff.setImageResource(R.drawable.expand_off);
        }else {
            holder.ivOnOff.setImageResource(R.drawable.expand_on);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder = null;
        final CategoryChildBean childBean = mChildList.get(groupPosition).get(childPosition);
        if (convertView==null){
            convertView = View.inflate(mContext, R.layout.category_childholder, null);
            holder = new ChildHolder();
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.ivChildAvatar);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvChildName);
            holder.rlLayout = (RelativeLayout) convertView.findViewById(R.id.layoytCategoryChild);
            convertView.setTag(holder);
        }else {
            holder= (ChildHolder) convertView.getTag();
        }
        UserUtils.setCategoryChildAvatar(mContext,childBean.getImageUrl(),holder.ivAvatar);
        holder.tvName.setText(childBean.getName());
        holder.rlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,CategoryActivity.class).putExtra(I.NewAndBoutiqueGood.CAT_ID,childBean.getId()));
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void initDate(ArrayList<CategoryParentBean> groupList, ArrayList<ArrayList<CategoryChildBean>> childList) {
        mGroupList = groupList;
        mChildList = childList;
        notifyDataSetChanged();
    }

    class GroupHolder{
        ImageView ivAvatar,ivOnOff;
        TextView tvName;
    }
    class ChildHolder{
        ImageView ivAvatar;
        TextView tvName;
        RelativeLayout rlLayout;
    }
}
