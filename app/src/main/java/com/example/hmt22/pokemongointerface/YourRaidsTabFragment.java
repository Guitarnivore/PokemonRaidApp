package com.example.hmt22.pokemongointerface;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;


/**
 * A simple {@link Fragment} subclass.
 */
public class YourRaidsTabFragment extends Fragment {

    String[] raidInfo;
    public static String[] raidIDs;
    public static String[] startTimes;
    public static String[] raidLevels;
    public static String[] numRaiders;
    public static String[] gyms;
    public static String[] pokemon;
    public static String[] meetings;
    public static ListView listView;
    public static CustomListAdapter listAdapter;

    public YourRaidsTabFragment() {
        // Required empty public constructor
    }

    /*
    public void addToYourRaids(String name, String des, Integer raiders, Integer img){
        raids.add(name);
        raidDescriptions.add(des);
        numRaiders.add(raiders);
        images.add(img);

        return;
    }
    */

    @Override
    public void onResume() {
        super.onResume();
        View v = getActivity().findViewById(R.id.current_layout);
        refresh(v);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        String[] pr = raids.toArray(new String[raids.size()]);
        String[] pd = raidDescriptions.toArray(new String[raidDescriptions.size()]);
        Integer[] pm = numRaiders.toArray(new Integer[numRaiders.size()]);
        Integer[] pi = images.toArray(new Integer[images.size()]);


        CustomListAdapter listAdapter = new CustomListAdapter(this.getActivity(),pr, pd, pm, pi);
        listView = (ListView) getView().findViewById(R.id.list);
        listView.setAdapter(listAdapter);
        */

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your_raids_tab, container, false);


    }

    static int numRaids;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        refresh(view);

    }

    private void refresh(View view) {
        final View v = view;
        //Ask for raids
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("RAIDSDD", "Creating Socket");
                        Socket socket = new Socket(MainActivity.host, MainActivity.port);
                        Boolean b = socket.isConnected();

                        //if statement for connection

                        Log.d("RAIDSDD", b.toString());
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                        writer.write("MY_RAIDS,username\n");
                        writer.flush();

                        Log.d("RAIDSD", "Got here");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String openBool = (socket.isConnected()) ? "true" : "false";
                        Log.d("RAIDSD", openBool);
                        String message = reader.readLine();
                        Log.d("RAIDSD", message);
                        final int numRaids = Integer.parseInt(message);
                        setNumRaids(numRaids);
                        raidInfo = new String[numRaids];

                        int count = 0;
                        while (!(message = reader.readLine()).equals("END")) {
                            raidInfo[count] = message;
                            count++;
                        }
                        Log.d("RAIDSD", "DONE");
                        writer.close();
                        reader.close();
                        socket.close();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("RAIDSD", Integer.toString(YourRaidsTabFragment.numRaids));
                                if(AllRaidsTabFragment.numRaids > 0){
                                    raidIDs = new String[YourRaidsTabFragment.numRaids];
                                    startTimes = new String[YourRaidsTabFragment.numRaids];
                                    numRaiders = new String[YourRaidsTabFragment.numRaids];
                                    raidLevels = new String[YourRaidsTabFragment.numRaids];
                                    gyms = new String[YourRaidsTabFragment.numRaids];
                                    pokemon = new String[YourRaidsTabFragment.numRaids];
                                    meetings = new String[YourRaidsTabFragment.numRaids];

                                    for (int i = 0; i < YourRaidsTabFragment.numRaids; i++) {
                                        String[] r = raidInfo[i].split(",");
                                        raidIDs[i] = r[0];
                                        startTimes[i] = r[1];
                                        numRaiders[i] = r[6];
                                        raidLevels[i] = r[3];
                                        gyms[i] = r[5];
                                        pokemon[i] = r[4];
                                        meetings[i] = r[7];
                                    }
                                } else {
                                    raidIDs = new String[1];
                                    startTimes = new String[1];
                                    numRaiders = new String[1];
                                    raidLevels = new String[1];
                                    gyms = new String[1];
                                    pokemon = new String[1];
                                    meetings = new String[1];


                                    raidIDs[0] = ("No Raids Available");
                                    startTimes[0] = ("Select \"Add Raid\" to create a new raid");
                                    numRaiders[0] = "0";
                                    raidLevels[0] = "0";
                                    gyms[0] = "No Raids Available";
                                    pokemon[0] = "";
                                    meetings[0] = "";

                                    YourRaidsTabFragment.numRaids = 1;
                                }

                                String[] pr = raidIDs;
                                String[] pd = startTimes;
                                Integer[] pl = new Integer[YourRaidsTabFragment.numRaids];
                                Integer[] pm = new Integer[YourRaidsTabFragment.numRaids];
                                String[] pg = new String[YourRaidsTabFragment.numRaids];
                                String[] pp = pokemon;
                                for (int i = 0; i < YourRaidsTabFragment.numRaids; i++) {
                                    pl[i] = Integer.parseInt(raidLevels[i]);
                                    Log.d("RAIDSDC", "raidlevel " + Integer.toString(pl[i]));
                                    pm[i] = Integer.parseInt(numRaiders[i]);
                                    pg[i] = gyms[i];
                                }

                                listAdapter = new CustomListAdapter(getActivity(),pr, pd, pm, pg, pl, pp, meetings);
                                listView = (ListView) getView().findViewById(R.id.list);
                                listView.setAdapter(listAdapter);

                            }
                        });
                    }
                    catch (Exception ex) {
                        Log.d("RAIDSD", ex.getMessage());
                    }
                }
            });
            t.start();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private void setNumRaids(int i) {
        numRaids = i;
    }

}
