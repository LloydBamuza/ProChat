package com.dev.lloyd.prochat.fragments;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import static com.dev.lloyd.prochat.ActivityChat.*;

import com.dev.lloyd.prochat.ActivityChat;
import com.dev.lloyd.prochat.R;
import com.dev.lloyd.prochat.TheUser;

import java.util.ArrayList;

import static com.dev.lloyd.prochat.TheUser.allContacts;
import static com.dev.lloyd.prochat.TheUser.contactListAdapter;
import static com.dev.lloyd.prochat.TheUser.contactNamesOnly;
import static com.dev.lloyd.prochat.fragments.fragActiveChat.loadPreviousMessages;

/**
 * Created by Anu on 22/04/17.
 */



public class fragOnlineContacts extends Fragment
{

    public static ListView contactList = null;
    public fragOnlineContacts()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_two, container, false);

        contactList = view.findViewById(R.id.lstContacts);

        return view;
    }


    @Override
    public void onStart()
    {
        super.onStart();
        displayContacts();
    }

    public void displayContacts()
    {
        contactListAdapter = new ArrayAdapter(fragOnlineContacts.this.getContext(), android.R.layout.simple_list_item_1,  contactNamesOnly);

        contactList.setAdapter(contactListAdapter);

        //load all open chanels
        TheUser.loadOpenChannels(fragOnlineContacts.this.getContext());

        //define contacts listview onclick for opening new channel/chat

        contactList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String item = TheUser.allContacts.get(i);
                String[] newChatParams = item.split(" ");

                //update active chat params to start new chat
                ActivityChat.ACTIVE_CHAT_FRIEND_ID = newChatParams[0];
                ActivityChat.ACTIVE_CHAT_CHANNEL_URL = newChatParams[1];



                //open chat fragmant
               /* FragmentTransaction ft = getFragmentManager().beginTransaction();
                fragActiveChat f = new fragActiveChat();
                f.setRetainInstance(true);
                ft.add(R.id.activity_chat,f).commit();*/

                Toast.makeText(fragOnlineContacts.this.getContext(),newChatParams[0] + " selected.\n Go to Chat tab",Toast.LENGTH_SHORT).show();
                fragActiveChat.openChannel(fragOnlineContacts.this.getContext());

                //tidy up to prepare for new chat session
                //fragActiveChat.msgReceiveThread.stop();
                ActivityChat.arrMsgHistory.clear();
                ActivityChat.adptMsgHistory.notifyDataSetChanged();

                return false;
            }
        });






    }


}

