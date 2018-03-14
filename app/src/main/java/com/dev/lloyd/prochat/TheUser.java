package com.dev.lloyd.prochat;


import android.content.Context;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.SendBirdException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

public class TheUser
{
    static File contactsFile;
    public static ArrayAdapter contactListAdapter;

    public static ArrayList<String> allContacts = null;
    public static ArrayList<OpenChannel> allOpenChannels = null;
    public static ArrayList<String> contactNamesOnly = null;

    public static Context context;
    public static String username = "";

    public TheUser(Context c)
    {
        context = c;
        allContacts = new ArrayList<>();
        contactNamesOnly = new ArrayList<>();

        try
        {
            //open contacts file
            contactsFile = new File(context.getFilesDir().getPath().toString() + "AllContacts.txt");

            //create new contacts file if absent
            if(!contactsFile.exists())
            {
                contactsFile.createNewFile();
                Toast.makeText(context,"Contacts file not found. \n Creating new file...",Toast.LENGTH_LONG).show();

            }
            else
            {
                Toast.makeText(context,"Finalizing...",Toast.LENGTH_LONG).show();

            }

            //make file writable
            contactsFile.setWritable(true,true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(c,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        getContactsFromFile();

    }


    public static void getContactsFromFile()
    {
        BufferedReader br = null;
        try
        {
            String buffer = null;
            br = new BufferedReader(new FileReader(contactsFile));

            while((buffer = br.readLine()) != null)
            {
                allContacts.add(buffer);
                String[] s = buffer.split(" ");
                contactNamesOnly.add(s[0]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    public static void addToFileContactList(String channelUrl, String userId )
    {
        BufferedWriter out = null;
        StringBuilder s = new StringBuilder();

        //check if contact already exists
        String sID,sChannelUrl;
        String[] temp = new String[2];




        if(allContacts != null)

        s.append(userId + " "+channelUrl);

        //add to contacts list
        allContacts.add(s.toString());
        contactNamesOnly.add(userId);




        //update contacts file
        try
        {


            out = new BufferedWriter(new FileWriter(contactsFile,true));
            out.write(s.toString());
            out.newLine();
            out.close();
        }
        catch (Exception e)
        {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    //get list of open channels, add nonexisting groups to user contacts arr
    public static void loadOpenChannels(Context context)
    {
        OpenChannelListQuery channelListQuery = OpenChannel.createOpenChannelListQuery();
        channelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler()
        {
            @Override
            public void onResult(List<OpenChannel> list, SendBirdException e)
            {
                if(e == null) //no error
                {
                    for(OpenChannel o : list)
                    {

                        String name = o.getName();
                        String url = o.getUrl();

                        StringBuilder stringBuilder = new StringBuilder(name + " "+url);

                        allContacts.add(stringBuilder.toString());
                        contactNamesOnly.add(name);
                    }
                    contactListAdapter.notifyDataSetChanged();

                }
                else
                {
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
