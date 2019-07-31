package com.example.hmt22.pokemongointerface;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllRaidsTabFragment extends Fragment {

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

    public AllRaidsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        View v = getActivity().findViewById(R.id.current_layout);
        refresh(v);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Pull existing raids from server, assign to arrays

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_raids_tab, container, false);
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

                        writer.write("RAID_REFRESH,username\n");
                        writer.flush();

                        Log.d("RAIDSD", "Got here");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String openBool = (socket.isConnected()) ? "true" : "false";
                        Log.d("RAIDSD", openBool);
                        String message = reader.readLine();
                        Log.d("RAIDSD", message);
                        int numRaids = Integer.parseInt(message);
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
                                Log.d("RAIDSD", Integer.toString(AllRaidsTabFragment.numRaids));
                                if(AllRaidsTabFragment.numRaids > 0){
                                    raidIDs = new String[AllRaidsTabFragment.numRaids];
                                    startTimes = new String[AllRaidsTabFragment.numRaids];
                                    numRaiders = new String[AllRaidsTabFragment.numRaids];
                                    raidLevels = new String[AllRaidsTabFragment.numRaids];
                                    gyms = new String[AllRaidsTabFragment.numRaids];
                                    pokemon = new String[AllRaidsTabFragment.numRaids];
                                    meetings = new String[AllRaidsTabFragment.numRaids];

                                    for (int i = 0; i < AllRaidsTabFragment.numRaids; i++) {
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

                                    raidIDs[0] = ("No Raids Available");
                                    startTimes[0] = ("Select \"Add Raid\" to create a new raid");
                                    numRaiders[0] = "0";
                                    raidLevels[0] = "0";
                                    gyms[0] = "";
                                    pokemon[0] = "";
                                    meetings[0] = "";
                                }

                                String[] pr = raidIDs;
                                String[] pd = startTimes;
                                Integer[] pl = new Integer[AllRaidsTabFragment.numRaids];
                                Integer[] pm = new Integer[AllRaidsTabFragment.numRaids];
                                String[] pg = new String[AllRaidsTabFragment.numRaids];
                                String[] pp = pokemon;
                                for (int i = 0; i < AllRaidsTabFragment.numRaids; i++) {
                                    pl[i] = Integer.parseInt(raidLevels[i]);
                                    Log.d("RAIDSDC", "raidlevel " + Integer.toString(pl[i]));
                                    pm[i] = Integer.parseInt(numRaiders[i]);
                                    pg[i] = gyms[i];
                                }

                                listAdapter = new CustomListAdapter(getActivity(),pr, pd, pm, pg, pl, pp, meetings);
                                listView = (ListView) getView().findViewById(R.id.list);
                                listView.setAdapter(listAdapter);

                                Button btn = (Button) v.findViewById(R.id.AddRaidButton);

                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), AddRaidActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });

                        //drawButtons(raids);
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
