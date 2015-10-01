//package com.claude.sharecam.group;
//
//import android.content.Context;
//import android.support.v7.app.ActionBarActivity;
//import android.os.Bundle;
//
//import com.claude.sharecam.R;
//import com.claude.sharecam.Util;
//import com.claude.sharecam.parse.Contact;
//import com.claude.sharecam.parse.ParseAPI;
//import com.claude.sharecam.share.IndividualFragment;
//import com.claude.sharecam.share.IndividualItem;
//import com.parse.ParseException;
//import com.parse.SaveCallback;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 그룹을 추가 하는 Activity
// * 데이터들은 Activity에서 관리하고 Fragment에서 데이터를 얻어와 setting하는 방식 (interface구현)
// */
//public class AddGroupActivity extends ActionBarActivity implements IndividualFragment.ActionbarItems,AddGroupFragment.GroupName {
//
//
//    //    List<Contact> addedItems;
//    String groupName;
//    Context context;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_group);
//        getSupportActionBar().hide();
//        Util.startFragment(getSupportFragmentManager(), R.id.addGroupLayout, new AddGroupFragment(), false, null);
////        addedItems=new ArrayList<Contact>();
//        context = this;
//
//
//        //back button listener 등록
//        Util.setBackBtnListener(this, getSupportFragmentManager());
//
//
//    }
//
//
////
////    @Override
////    public boolean isAdded(Contact individualItem) {
////        for(int i=0; i<addedItems.size(); i++)
////        {
////            if(addedItems.get(i).getRecordId()==individualItem.getRecordId())
////            {
////                return true;
////            }
////        }
////        return false;
//////    }
//////    @Override
//////    public void addItem(Contact individualItem) {
//////        addedItems.add(individualItem);
//////    }
//////
//////    @Override
//////    public void removeItem(Contact individualItem) {
//////        addedItems.remove(individualItem);
//////    }
//////
//////    //IndividualFragment에서 확인 버튼 누른 경우
//////    @Override
//////    public void clickActionItem1() {
//////
//////
//////        ParseAPI.createGroup(context, groupName, addedItems, new SaveCallback() {
//////            @Override
//////            public void done(ParseException e) {
//////
//////                if(e==null)
//////                {
//////                    finish();
//////                }
//////                else{
//////                    ParseAPI.erroHandling(context,e);
//////                }
//////            }
//////        });
//////    }
//////    @Override
//////    public void setGroupName(String groupName) {
//////        this.groupName=groupName;
//////    }
////
////}
//}