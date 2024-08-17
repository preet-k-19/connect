package com.example.chat_application;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_application.adapter.RecentChatRecyclerAdapter;
import com.example.chat_application.model.ChatRoomModel;
import com.example.chat_application.util.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class chat_fragment extends Fragment {
RecyclerView recyclerView;
RecentChatRecyclerAdapter adapter;
    public chat_fragment() {

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_chat_fragment, container, false);
       recyclerView=view.findViewById(R.id.recycler_view_chat);
       setupRecyclerView();
       return view;

    }





    void setupRecyclerView(){
        Query query = FireBaseUtil.allChatRoomCollection()
                //if not works error should be here 50% chances
                .whereArrayContains("userIds",FireBaseUtil.currentUserId())
                .orderBy("lastMSgTime", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query, ChatRoomModel.class).build();

        adapter=new RecentChatRecyclerAdapter(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
        {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
        {
            adapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
        {
            adapter.notifyDataSetChanged();
        }
    }


}