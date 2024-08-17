package com.example.chat_application;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_application.adapter.SearchUserrecyclerAdapter;
import com.example.chat_application.model.UserModel;
import com.example.chat_application.util.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class Search_user_activity extends AppCompatActivity {
EditText searchInput;
ImageButton searchUserBtn,backBtn;
RecyclerView recyclerView;
SearchUserrecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput=findViewById(R.id.search_user_edt);
        searchUserBtn=findViewById(R.id.search_user_btn);
        recyclerView=findViewById(R.id.search_user_recycler_view);
        backBtn=findViewById(R.id.back_btn);

        searchInput.requestFocus();

        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });

        searchUserBtn.setOnClickListener(v->{
            String search=searchInput.getText().toString();
            if(search.isEmpty() || search.length()<3 || search.equals(""))
            {
                searchInput.setError("Invalid UserName");
                return;
            }
            setUpSearchRecycleView(search);
        });
    }


    void setUpSearchRecycleView(String search){
        Query query = FireBaseUtil.allUserCollectionReference()
                //if not works error should be here 50% chances
                .whereGreaterThanOrEqualTo("userName",search)
                .whereLessThanOrEqualTo("username",search+'\uf8ff');


        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter=new SearchUserrecyclerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
        {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
        {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
        {
            adapter.startListening();
        }
    }
}