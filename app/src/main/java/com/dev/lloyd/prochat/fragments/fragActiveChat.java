package com.dev.lloyd.prochat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.dev.lloyd.prochat.R;
import com.dev.lloyd.prochat.TheUser;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;

import java.util.ArrayList;
import java.util.List;
import static com.dev.lloyd.prochat.ActivityChat.*;

/**
 * Created by Anu on 22/04/17.
 */



public class fragActiveChat extends Fragment
{
    public static Thread msgReceiveThread;

    String MAIN_MESSAGE_HANDLER = "Main message handler";


    public fragActiveChat() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_three, container, false);

        //prepare chat history list to view messages
        lstMsgHistory = (ListView) view.findViewById(R.id.lstChatHistory);
        adptMsgHistory = new ArrayAdapter(fragActiveChat.this.getContext(),android.R.layout.simple_list_item_1,arrMsgHistory);
        lstMsgHistory.setAdapter(adptMsgHistory);
        EditText edtMessage = (EditText) view.findViewById(R.id.edtMsg);

    new Thread(new Runnable() {
        @Override
        public void run()
        {
            while(ACTIVE_GROUP_CHANNEL == null && ACTIVE_OPEN_CHANNEL == null)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true)
                        {
                            loadPreviousMessages(null);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                }).start();
            }


        }
    }).start();




        //onclick to send message
        ((Button) view.findViewById(R.id.btnSndMsg)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                //get message to be sent to friend
                String msg2Snd = null;
                msg2Snd = edtMessage.getText().toString();

                if((ACTIVE_CHAT_CHANNEL_URL != null) && (ACTIVE_CHAT_FRIEND_ID != null) && ACTIVE_GROUP_CHANNEL != null)

                {
                    //send message to friend
                    String finalMsg2Snd = msg2Snd;
                    ACTIVE_GROUP_CHANNEL.sendUserMessage(msg2Snd, new BaseChannel.SendUserMessageHandler() {
                        @Override
                        public void onSent(UserMessage userMessage, SendBirdException e) {
                            if (e == null) //no error
                            {
                                arrMsgHistory.add("You: " + finalMsg2Snd);
                                adptMsgHistory.notifyDataSetChanged();
                            } else //show error
                            {
                                Toast.makeText(fragActiveChat.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    });
                }
                    else
                    {
                        if((ACTIVE_CHAT_CHANNEL_URL != null) && (ACTIVE_CHAT_FRIEND_ID != null) && ACTIVE_OPEN_CHANNEL != null)

                        {
                            //send message to to open group
                            String finalMsg2Snd = msg2Snd;
                            ACTIVE_OPEN_CHANNEL.sendUserMessage(msg2Snd, new BaseChannel.SendUserMessageHandler()
                            {
                                @Override
                                public void onSent(UserMessage userMessage, SendBirdException e)
                                {
                                    if(e == null) //no error
                                    {
                                        //arrMsgHistory.add(TheUser.username +": "+ finalMsg2Snd);
                                        //adptMsgHistory.notifyDataSetChanged();
                                    }
                                    else //show error
                                    {
                                        Toast.makeText(fragActiveChat.this.getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }

            }
        });







        return view;
    }


    public static void startMessageReceiver()
    {


                Handler handler = new Handler(Looper.getMainLooper());

                    SendBird.addChannelHandler(null, new SendBird.ChannelHandler() {
                        @Override
                        public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                                arrMsgHistory.add(((UserMessage) baseMessage).getSender().getUserId() + ":Received " + ((UserMessage) baseMessage).getMessage());

                                //refresh message history


                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adptMsgHistory.notifyDataSetChanged();
                                        lstMsgHistory.setAdapter(adptMsgHistory);
                                        lstMsgHistory.refreshDrawableState();

                                    }
                                });

                        }
                    });


    }


    //open existing channel
    public static void openChannel(Context context) {
        //if (this.isVisible()) {
        isGroupChat = false;
        isOpenChat = false;

        //if tab is visable, begin new chat session
        if ((ACTIVE_CHAT_CHANNEL_URL != null) && (ACTIVE_CHAT_FRIEND_ID != null)) {
            //open group channel
            if ((ACTIVE_CHAT_CHANNEL_URL).contains("sendbird_group_channel")) {
                GroupChannel.getChannel(ACTIVE_CHAT_CHANNEL_URL, new GroupChannel.GroupChannelGetHandler() {
                    @Override
                    public void onResult(GroupChannel groupChannel, SendBirdException e) {
                        if (e == null) {
                            isGroupChat = true;
                            ACTIVE_GROUP_CHANNEL = groupChannel;
                            Toast.makeText(context, "Loading message history...", Toast.LENGTH_LONG).show();
                            loadPreviousMessages(context);
                            startMessageReceiver();
                        } else {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });

            } else if ((ACTIVE_CHAT_CHANNEL_URL).contains("sendbird_open_channel")) {
                //open open channel
                OpenChannel.getChannel(ACTIVE_CHAT_CHANNEL_URL, new OpenChannel.OpenChannelGetHandler() {
                    @Override
                    public void onResult(OpenChannel openChannel, SendBirdException e) {
                        if (e == null) {
                            //isOpenChat = true;
                            ACTIVE_OPEN_CHANNEL = openChannel;
                            Toast.makeText(context, "Loading message history...", Toast.LENGTH_LONG).show();
                            loadPreviousMessages(context);
                            startMessageReceiver();


                            //enter open channel
                            ACTIVE_OPEN_CHANNEL.enter(new OpenChannel.OpenChannelEnterHandler() {
                                @Override
                                public void onResult(SendBirdException e) {
                                    if (e == null) {
                                        Toast.makeText(context, "Public Group entered successfully", Toast.LENGTH_LONG);
                                    } else {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
                                        e.printStackTrace();

                                    }
                                }
                            });


                        } else {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });


            } else //remove channel handler and clear channel variable instances
            {
                //SendBird.removeChannelHandler(MAIN_MESSAGE_HANDLER);
                // ACTIVE_GROUP_CHANNEL = null;
                //ACTIVE_OPEN_CHANNEL = null;
                //indicate not to start a new session if user exits tab and returns without selecting another user to chat with
            }
            //}
        }
    }

    public static void loadPreviousMessages(Context context) {
       // Toast.makeText(context, "Loading....", Toast.LENGTH_LONG).show();
        final int[] currentMsgCount = {arrMsgHistory.size() + 1};

        PreviousMessageListQuery prevMsg = null;
        if (ACTIVE_GROUP_CHANNEL != null)
        {
            prevMsg = ACTIVE_GROUP_CHANNEL.createPreviousMessageListQuery();

        }
        else if(ACTIVE_OPEN_CHANNEL != null)
        {
            prevMsg = ACTIVE_OPEN_CHANNEL.createPreviousMessageListQuery();
        }

        prevMsg.load(100, false, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e)
            {
                if (e == null && ACTIVE_GROUP_CHANNEL != null)  //no error
                {

                        while(currentMsgCount[0] <= list.size())
                        {

                            String friendId =  ((UserMessage)(list.get(currentMsgCount[0] -1))).getSender().getUserId();

                            if(friendId.equals(ACTIVE_CHAT_FRIEND_ID))
                            {
                                arrMsgHistory.add((friendId + ": " + ((UserMessage)(list.get(currentMsgCount[0]-1))).getMessage()));
                                adptMsgHistory.notifyDataSetChanged();

                            } else {
                                arrMsgHistory.add((("You: " + ": " + ((UserMessage)(list.get(currentMsgCount[0]-1))).getMessage())));
                                adptMsgHistory.notifyDataSetChanged();
                            }
                            adptMsgHistory.notifyDataSetChanged();
                            currentMsgCount[0]++;
                        }
                    }

                if (e == null && ACTIVE_OPEN_CHANNEL != null)  //no error
                {

                    while(currentMsgCount[0] <= list.size())
                    {

                        String friendId =  ((UserMessage)(list.get(currentMsgCount[0] -1))).getSender().getUserId();


                        arrMsgHistory.add((friendId + ": " + ((UserMessage)(list.get(currentMsgCount[0]-1))).getMessage()));


                        adptMsgHistory.notifyDataSetChanged();
                        currentMsgCount[0]++;
                    }
                }

                    adptMsgHistory.notifyDataSetChanged();
                }


        });

       // Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();

    }


}