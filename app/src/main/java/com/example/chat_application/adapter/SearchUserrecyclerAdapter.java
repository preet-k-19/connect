package com.example.chat_application.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_application.ChatAcitivity;
import com.example.chat_application.R;
import com.example.chat_application.model.UserModel;
import com.example.chat_application.util.AndroidUtil;
import com.example.chat_application.util.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserrecyclerAdapter extends FirestoreRecyclerAdapter<UserModel,SearchUserrecyclerAdapter.userModelViewHolder> {

    Context context;


    public SearchUserrecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options,Context context) {
        super(options);
        this.context= context;
    }

    @Override
    protected void onBindViewHolder(@NonNull userModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.userNameText.setText(model.getUserName());
        holder.phoneText.setText(model.getPhone());
        if(model.getUserId().equals(FireBaseUtil.currentUserId()))
        {
            holder.userNameText.setText(model.getUserName()+" (ME)");
        }

        FireBaseUtil.getOtherProfilePic(model.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful())
                    {
                        Uri uri= t.getResult();
                        AndroidUtil.setProfilePic(context,uri,holder.profilePic);
                    }
                });

        holder.itemView.setOnClickListener(v->{
            //navigating to the chat screen
            Intent intent=new Intent(context,ChatAcitivity.class);
            AndroidUtil.passUserModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }


    @NonNull
    @Override
    public userModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);
        return new userModelViewHolder(view);
    }


    class userModelViewHolder extends RecyclerView.ViewHolder{

         TextView userNameText;
         TextView phoneText;
         ImageView profilePic;

         public userModelViewHolder(@NonNull View itemView) {
             super(itemView);
             userNameText=itemView.findViewById(R.id.user_name_text);
             phoneText=itemView.findViewById(R.id.phone_number_text);
             profilePic=itemView.findViewById(R.id.profile_pic_image_view);


         }
     }

}
