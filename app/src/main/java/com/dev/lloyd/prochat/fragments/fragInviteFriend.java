package com.dev.lloyd.prochat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.lloyd.prochat.R;
import com.dev.lloyd.prochat.TheUser;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anu on 22/04/17.
 */



public class fragInviteFriend extends Fragment
{
    public final String CHANNEL_TYPE = "sendbird_group_channel";

    public fragInviteFriend()
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
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        //create and invite a friend to a group channel
        (view.findViewById(R.id.btnInviteFriend)).setOnClickListener((e)->
        {
            String friendId = ((EditText) view.findViewById(R.id.edtFriend_ID)).getText().toString();


            List<String> friendList = new ArrayList<>();
            friendList.add(friendId);
            GroupChannel.createChannelWithUserIds(friendList, true, new GroupChannel.GroupChannelCreateHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e)
                {
                    if((e == null))
                    {
                        //avoid duplicates
                        for(int i = 0; i < TheUser.allContacts.size() - 1; i++)
                        {
                            String[] temp = new String[1];
                            temp = TheUser.allContacts.get(i).split(" ");
                            String sID = temp[0];
                            String sChannelUrl = temp[1];

                            if((sID.equals(friendId)) && (sChannelUrl.contains("send_bird_group")))
                            {
                                Toast.makeText(fragInviteFriend.this.getContext(),"User already exists in contacts",Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        TheUser.addToFileContactList(groupChannel.getUrl(),friendId);
                        TheUser.contactListAdapter.notifyDataSetChanged();
                        Toast.makeText(fragInviteFriend.this.getActivity(),"Friend added successfully",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(fragInviteFriend.this.getActivity(),"Adding friend was unsuccessful",Toast.LENGTH_LONG).show();
                        Toast.makeText(fragInviteFriend.this.getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
            });
        });


        //create public group (button onclick)
        (view.findViewById(R.id.btnCreateGroup)).setOnClickListener((e)->
        {
            String groupName = ((EditText) view.findViewById(R.id.edtGroupName)).getText().toString();

            //avoid duplicates
            for(int i = 0; i < TheUser.allContacts.size() - 1; i++)
            {
                String[] temp = new String[1];
                temp = TheUser.allContacts.get(i).split(" ");
                String sID = temp[0];
                String sChannelUrl = temp[1];

                if((sID.equals(groupName)) && (sChannelUrl.contains("send_bird_open")))
                {
                    Toast.makeText(fragInviteFriend.this.getContext(),"Group already exists",Toast.LENGTH_LONG).show();
                    return;
                }
            }

            //create new open channel
            OpenChannel.createChannel(groupName, null, null, null, new OpenChannel.OpenChannelCreateHandler() {
                @Override
                public void onResult(OpenChannel openChannel, SendBirdException e)
                {
                    if(e == null) //no error
                    {
                        Toast.makeText(fragInviteFriend.this.getContext(),"Group: "+groupName +" created successfully",Toast.LENGTH_LONG).show();
                        TheUser.addToFileContactList(openChannel.getUrl(),groupName);
                    }
                    else
                    {
                        Toast.makeText(fragInviteFriend.this.getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        e.printStackTrace();

                    }
                }
            });



        });
        return view;
    }


}