package com.example.chat_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chat_application.model.UserModel;
import com.example.chat_application.util.AndroidUtil;
import com.example.chat_application.util.FireBaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.messaging.FirebaseMessaging;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class profile_Fragment extends Fragment {
ImageView profileImage;
EditText userName;
EditText PhoneNumber;
ProgressBar progressBar;
TextView logOutBtn;
Button update;
UserModel currentUserModel;
ActivityResultLauncher<Intent> imagePic;
Uri selectedImageUri;
    public profile_Fragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePic=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                  if(result.getResultCode()== Activity.RESULT_OK){
                      Intent data=result.getData();
                      if(data!=null && data.getData()!=null)
                      {
                          selectedImageUri = data.getData();
                          AndroidUtil.setProfilePic(getContext(),selectedImageUri,profileImage);
                      }
                  }
                }
                );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view= inflater.inflate(R.layout.fragment_profile_, container, false);
       profileImage=view.findViewById(R.id.profile_image_view);
       userName=view.findViewById(R.id.profile_user_name);
       PhoneNumber=view.findViewById(R.id.profile_phone_number);
       update=view.findViewById(R.id.profile_update_btn);
       progressBar=view.findViewById(R.id.profile_progressbar);
       logOutBtn=view.findViewById(R.id.logout_btn);
       
       getUserData();
       
       update.setOnClickListener(v-> {
            updateClick();   
       });

       logOutBtn.setOnClickListener((v)->{
           AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
           builder.setMessage("Are you Sure Want TO Logout From Application?");
           builder.setTitle("Alert!");
           builder.setCancelable(false);
           builder.setPositiveButton("Yess",(DialogInterface.OnClickListener)(dialog, which)->{
               FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                  if(task.isSuccessful()){
                      FireBaseUtil.logout();
                      Intent intent=new Intent(getContext(), SplashScreen.class);
                      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                      startActivity(intent);
                  }
               });
           });
           builder.setNegativeButton("No",(DialogInterface.OnClickListener)(dialog, which)->{
               dialog.cancel();
           });
           AlertDialog alertDialog= builder.create();
           alertDialog.show();
       });

       profileImage.setOnClickListener((v)-> {
           ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512).createIntent(
                   new Function1<Intent, Unit>() {
                       @Override
                       public Unit invoke(Intent intent) {
                           imagePic.launch(intent);

                           return null;
                       }
                   }
           );
       });

       return view;
    }
    
    
    void updateClick(){
        String NewUserName=userName.getText().toString();
        if(NewUserName.isEmpty() || NewUserName.length()<3){
            userName.setError("UserName Should be at least 3 character ");
            return;
        }
        currentUserModel.setUserName(NewUserName);
        inProgress(true);

        if(selectedImageUri!=null)
        {
            FireBaseUtil.getCurrentProfilePic().putFile(selectedImageUri).addOnCompleteListener(task -> {
                updateToFireStore();
            });

        }
        else
        {
            updateToFireStore();
        }
    }
    
    void updateToFireStore(){
       FireBaseUtil.currentUserDetails().set(currentUserModel)
               .addOnCompleteListener(task -> {
                  inProgress(false);
                   if(task.isSuccessful()){
                      AndroidUtil.showToast(getContext(),"Updated Successfully ");
                  }
                  else
                  {
                      AndroidUtil.showToast(getContext(),"Updated Failed");
                  }
               });
    }
    void getUserData(){
        inProgress(true);
        FireBaseUtil.getCurrentProfilePic().getDownloadUrl()
                        .addOnCompleteListener(task -> {
                           if(task.isSuccessful())
                           {
                               Uri uri= task.getResult();
                               AndroidUtil.setProfilePic(getContext(),uri,profileImage);
                           }
                        });
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
          inProgress(false);
            currentUserModel=task.getResult().toObject(UserModel.class);
            if (currentUserModel != null) {
                userName.setText(currentUserModel.getUserName());
            }
            if (currentUserModel != null) {
                PhoneNumber.setText(currentUserModel.getPhone());
            }

        });
    }

    void inProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            update.setVisibility(View.GONE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
        }

    }
}