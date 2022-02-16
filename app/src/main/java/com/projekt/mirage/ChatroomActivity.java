package com.projekt.mirage;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projekt.mirage.adapters.ChatRecyclerViewAdapter;
import com.projekt.mirage.models.ChatsModel;
import com.projekt.mirage.models.MessageModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatroomActivity extends AppCompatActivity {

    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    ChatsDatabaseHelper chatsDatabase;
    private RecyclerView chatsRecyclerView;
    private EditText userMessageEdit;
    private ArrayList<ChatsModel> chatsModelArrayList;
    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        chatsRecyclerView = findViewById(R.id.RecyclerViewChats);
        userMessageEdit = findViewById(R.id.EditMessage);

        chatsDatabase = new ChatsDatabaseHelper(getApplicationContext());

        //retrieve chat records from database
        chatsModelArrayList = chatsDatabase.getAllRecords();
        chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(chatsModelArrayList, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        chatsRecyclerView.setLayoutManager(manager);
        chatsRecyclerView.setAdapter(chatRecyclerViewAdapter);

        if (chatsModelArrayList.size() > 0)
            chatsRecyclerView.scrollToPosition(chatsModelArrayList.size() - 1);
        else {
            ChatsModel ChatEntry = new ChatsModel("Hi!", BOT_KEY);
            chatsModelArrayList.add(ChatEntry);
            chatsDatabase.addChatRecord(ChatEntry);
        }

        //override enter key to send text when pressed
        userMessageEdit.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                sendText();
                return true;
            }
            return false;
        });

    }

    private void getResponse(String message) {
        chatsModelArrayList.add(new ChatsModel(message, USER_KEY));
        chatRecyclerViewAdapter.notifyDataSetChanged();

        String url = "http://api.brainshop.ai/get?bid=162142&key=VvB0XRhGNve3rmIq&uid=" +
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) +
                "&msg=" + message;

        String BASE_URL = "http://api.brainshop.ai/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MessageModel> call = retrofitAPI.getMessage(url);

        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    MessageModel messageModel = response.body();
                    ChatsModel ChatEntry = new ChatsModel(messageModel.getCnt(), BOT_KEY);
                    chatsModelArrayList.add(ChatEntry);
                    chatsRecyclerView.scrollToPosition(chatsModelArrayList.size() - 1);
                    chatRecyclerViewAdapter.notifyDataSetChanged();

                    chatsDatabase.addChatRecord(ChatEntry);
                }
            }

            @Override
            public void onFailure(Call<MessageModel> call, Throwable throwable) {
                ChatsModel ChatEntry = new ChatsModel("Please revert your question and check your internet connection.", BOT_KEY);
                chatsModelArrayList.add(ChatEntry);
                chatsDatabase.addChatRecord(ChatEntry);
                chatRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    public void sendText() {
        if (userMessageEdit.getText().toString().isEmpty()) {
            Toast.makeText(ChatroomActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();
            return;
        }

        getResponse(userMessageEdit.getText().toString());
        chatsDatabase.addChatRecord(new ChatsModel(userMessageEdit.getText().toString(), USER_KEY));

        userMessageEdit.setText("");
        chatsRecyclerView.scrollToPosition(chatsModelArrayList.size() - 1);
    }

    public void onClickSend(View view) {
        sendText();
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openInfo(View view) {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}