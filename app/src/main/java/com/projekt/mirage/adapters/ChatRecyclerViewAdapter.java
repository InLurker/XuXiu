package com.projekt.mirage.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projekt.mirage.R;
import com.projekt.mirage.SettingsDatabaseHelper;
import com.projekt.mirage.models.ChatsModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter {

    SettingsDatabaseHelper settingsDatabaseHelper;
    private ArrayList<ChatsModel> ChatsModelArrayList;
    private Context context;

    public ChatRecyclerViewAdapter(ArrayList<ChatsModel> chatsModelArrayList, Context context) {
        ChatsModelArrayList = chatsModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        settingsDatabaseHelper = new SettingsDatabaseHelper(context);
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_item, parent, false);

                CircleImageView userIcon = view.findViewById(R.id.UserIcon);
                Uri user_uri = settingsDatabaseHelper.getUri("user");
                if(!Uri.EMPTY.equals(user_uri))
                    userIcon.setImageURI(user_uri);

                return new UserViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_message_item, parent, false);

                CircleImageView botIcon = view.findViewById(R.id.BotIcon);
                Uri bot_uri = settingsDatabaseHelper.getUri("bot");
                if(!Uri.EMPTY.equals(bot_uri))
                    botIcon.setImageURI(bot_uri);

                return new BotViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatsModel chatsModel = ChatsModelArrayList.get(position);

        switch (chatsModel.getSender()) {
            case "user":
                ((UserViewHolder) holder).userTextView.setText(Html.fromHtml(chatsModel.getMessage(), Html.FROM_HTML_MODE_LEGACY));
                break;
            case "bot":
                ((BotViewHolder) holder).botTextView.setText(Html.fromHtml(chatsModel.getMessage(), Html.FROM_HTML_MODE_LEGACY));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return ChatsModelArrayList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userTextView;

        public UserViewHolder(@NonNull View view) {
            super(view);
            userTextView = view.findViewById(R.id.UserTextView);
        }
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView botTextView;
        public BotViewHolder(@NonNull View view) {
            super(view);
            botTextView = view.findViewById(R.id.BotTextView);

        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (ChatsModelArrayList.get(position).getSender()) {
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }


}
