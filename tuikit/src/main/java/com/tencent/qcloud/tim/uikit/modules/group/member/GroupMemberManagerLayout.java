package com.tencent.qcloud.tim.uikit.modules.group.member;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.tencent.qcloud.tim.uikit.modules.group.interfaces.IGroupMemberLayout;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.utils.PopWindowUtil;

import java.util.List;


public class GroupMemberManagerLayout extends LinearLayout implements IGroupMemberLayout {

    private TitleBarLayout mTitleBar;
    private AlertDialog mDialog;
    private GroupMemberManagerAdapter mAdapter;
    private IGroupMemberRouter mGroupMemberManager;
    private GroupInfo mGroupInfo;

    public GroupMemberManagerLayout(Context context) {
        super(context);
        init();
    }

    public GroupMemberManagerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupMemberManagerLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.group_member_layout, this);
        mTitleBar = findViewById(R.id.group_member_title_bar);
        mTitleBar.setTitle(getContext().getString(R.string.manager), TitleBarLayout.POSITION.RIGHT);
        mTitleBar.getRightIcon().setVisibility(GONE);
        mTitleBar.setOnRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buildPopMenu();
            }
        });
        mAdapter = new GroupMemberManagerAdapter();
        mAdapter.setMemberChangedCallback(new IGroupMemberChangedCallback() {

            @Override
            public void onMemberRemoved(GroupMemberInfo memberInfo) {
                mTitleBar.setTitle(getContext().getString(R.string.group_members) + "(" + mGroupInfo.getMemberDetails().size() + ")", TitleBarLayout.POSITION.MIDDLE);
            }
        });
        GridView gridView = findViewById(R.id.group_all_members);
        gridView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onGroupMemberClick(View view, int position, GroupInfo groupInfo) {
                String mGroupId = groupInfo.getId();
                final String groupType = groupInfo.getGroupType();

                List<GroupMemberInfo> memberDetails = groupInfo.getMemberDetails();
                final String userID = memberDetails.get(0).getAccount();
                V2TIMManager.getGroupManager().setGroupMemberRole(mGroupId, userID, V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_ADMIN, new V2TIMCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("yp---->", userID + "被设置为管理员"+ "群类型: " + groupType);
                    }

                    @Override
                    public void onError(int code, String desc) {
                        Log.d("yp---->", code + "; desc");
                    }
                });
            }
        });

    }

    public TitleBarLayout getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void setParentLayout(Object parent) {

    }

    public void setDataSource(GroupInfo groupInfo) {
        mGroupInfo = groupInfo;
        mAdapter.setDataSource(groupInfo);
        if (groupInfo != null) {
            mTitleBar.setTitle(getContext().getString(R.string.group_members) + "(" + groupInfo.getMemberDetails().size() + ")", TitleBarLayout.POSITION.MIDDLE);
        }
    }

    private void buildPopMenu() {
        if (mGroupInfo == null) {
            return;
        }
        if (mDialog == null) {
            mDialog = PopWindowUtil.buildFullScreenDialog((Activity) getContext());
            View moreActionView = inflate(getContext(), R.layout.group_member_pop_menu, null);
            moreActionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
            Button addBtn = moreActionView.findViewById(R.id.add_group_member);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mGroupMemberManager != null) {
                        mGroupMemberManager.forwardAddMember(mGroupInfo);
                    }
                    mDialog.dismiss();

                }
            });
            Button deleteBtn = moreActionView.findViewById(R.id.remove_group_member);
            if (!mGroupInfo.isOwner()) {
                deleteBtn.setVisibility(View.GONE);
            }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mGroupMemberManager != null) {
                        mGroupMemberManager.forwardDeleteMember(mGroupInfo);
                    }
                    mDialog.dismiss();
                }
            });
            Button cancelBtn = moreActionView.findViewById(R.id.cancel);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
            mDialog.setContentView(moreActionView);
        } else {
            mDialog.show();
        }

    }

    public void setRouter(IGroupMemberRouter callBack) {
        this.mGroupMemberManager = callBack;
    }

    public interface OnItemClickListener {
        void onGroupMemberClick(View view, int position, GroupInfo groupInfo);
    }

}
