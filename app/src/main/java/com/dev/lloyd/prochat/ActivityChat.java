package com.dev.lloyd.prochat;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dev.lloyd.prochat.fragments.fragInviteFriend;
import com.dev.lloyd.prochat.fragments.fragOnlineContacts;
import com.dev.lloyd.prochat.fragments.fragActiveChat;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.OpenChannel;


import java.util.ArrayList;
import java.util.List;

public class ActivityChat extends FragmentActivity
{


    public static Boolean NEW_CHAT = null ;
    public static String ACTIVE_CHAT_FRIEND_ID = null;
    public static String ACTIVE_CHAT_CHANNEL_URL = null;
    public static GroupChannel ACTIVE_GROUP_CHANNEL = null;
    public static OpenChannel ACTIVE_OPEN_CHANNEL = null;
    public static List<String> arrMsgHistory = new ArrayList<>();
    public static ListView lstMsgHistory = null;
    public static ArrayAdapter adptMsgHistory = null;
    public static boolean isGroupChat, isOpenChat;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new fragInviteFriend(), "Invite Friend");
        adapter.addFragment(new fragOnlineContacts(), "Contacts");
        adapter.addFragment(new fragActiveChat(), "Chat");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //create user object
        TheUser user = new TheUser(ActivityChat.this);
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
