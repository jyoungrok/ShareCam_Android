package com.claude.sharecam.group;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.share.IndividualFragment;
import com.claude.sharecam.orm.IndividualItem;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;

/**
 * 그룹을 추가 하는 Activity
 * 데이터들은 Activity에서 관리하고 Fragment에서 데이터를 얻어와 setting하는 방식 (interface구현)
 */
public class AddGroupActivity extends ActionBarActivity implements IndividualFragment.AddedItems, IndividualFragment.ActionbarItems,AddGroupFragment.GroupName{


    ArrayList<IndividualItem> addedItems;
    String groupName;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getSupportActionBar().hide();
        Util.startFragment(getSupportFragmentManager(),R.id.addGroupLayout,new AddGroupFragment(),false,null);
        addedItems=new ArrayList<IndividualItem>();
        context=this;


        //back button listener 등록
        Util.setBackBtnListener(this,getSupportFragmentManager());



    }


    @Override
    public void setAddedItems(ArrayList<IndividualItem> addedItems) {

        this.addedItems=addedItems;
    }

    @Override
    public ArrayList<IndividualItem> getAddedItems() {
        return addedItems;
    }

    @Override
    public void addItem(IndividualItem individualItem) {
        addedItems.add(individualItem);
    }

    @Override
    public void removeItem(IndividualItem individualItem) {
        addedItems.remove(individualItem);
    }

    //IndividualFragment에서 확인 버튼 누른 경우
    @Override
    public void clickActionItem1() {


        ParseAPI.createGroup(context, groupName, addedItems, new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if(e==null)
                {
                    finish();
                }
                else{
                    ParseAPI.erroHandling(context,e);
                }
            }
        });
    }
    @Override
    public void setGroupName(String groupName) {
        this.groupName=groupName;
    }

}
