package com.projekt.mirage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    ViewGroup userViewGroup;
    ViewGroup botViewGroup;

    Button userImageSelect_btn;
    Button botImageSelect_btn;

    SettingsDatabaseHelper settingsdb;

    SwitchCompat user_switch;
    SwitchCompat bot_switch;

    CircleImageView user_image;
    CircleImageView bot_image;

    boolean updateUser;

    List<Uri> uriConfig;

    ChatsDatabaseHelper chatsDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsdb = new SettingsDatabaseHelper(getApplicationContext());
        uriConfig = settingsdb.getImgSrc();

        userViewGroup = findViewById(R.id.userSetting);
        botViewGroup = findViewById(R.id.botSetting);

        userImageSelect_btn = findViewById(R.id.user_select_image_button);
        botImageSelect_btn = findViewById(R.id.bot_select_image_button);

        user_switch = findViewById(R.id.user_option_switch);
        bot_switch = findViewById(R.id.bot_option_switch);

        user_image = findViewById(R.id.user_imageview);
        bot_image = findViewById(R.id.bot_imageview);

        if (uriConfig.size() > 1) {
            if (!Uri.EMPTY.equals(uriConfig.get(0))) {
                user_image.setImageURI(uriConfig.get(0));
                user_switch.setChecked(false);
                switchOnOff(user_switch);
            }
            if (!Uri.EMPTY.equals(uriConfig.get(1))) {
                bot_image.setImageURI(uriConfig.get(1));
                bot_switch.setChecked(false);
                switchOnOff(bot_switch);
            }
        }

        chatsDatabaseHelper = new ChatsDatabaseHelper(getApplicationContext());
    }

    public void switchOnOff(View view) {
        if (((SwitchCompat) view).isChecked()) {
            ((ViewGroup) view.getParent()).setAlpha(0.3f);
            if (view == user_switch)
                user_image.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.user_default));
            if (view == bot_switch)
                bot_image.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bot_default));
        } else {
            ((ViewGroup) view.getParent()).setAlpha(1);
            if (!uriConfig.isEmpty()) {
                if (view == user_switch) {
                    if (!Uri.EMPTY.equals(uriConfig.get(0)))
                        user_image.setImageURI(uriConfig.get(0));
                }
                else {
                    if (!Uri.EMPTY.equals(uriConfig.get(1)))
                        bot_image.setImageURI(uriConfig.get(1));
                }
            }
        }
    }

    public void selectUserImage(View view) {
        File file = new File(getFilesDir(), "user_image");
        file.delete();

        if (!user_switch.isChecked() && view == userImageSelect_btn) {
            updateUser = true;
            ImagePicker.with(this)
                    .cropSquare()
                    .saveDir(new File(getFilesDir(), "user_image"))
                    .maxResultSize(200, 200)
                    .start();
        }
    }

    public void selectBotImage(View view) {
        File file = new File(getFilesDir(), "bot_image");
        file.delete();

        if (!bot_switch.isChecked() && view == botImageSelect_btn) {
            updateUser = false;
            ImagePicker.with(this)
                    .cropSquare()
                    .saveDir(new File(getFilesDir(), "bot_image"))
                    .maxResultSize(200, 200)
                    .start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imgURI = data.getData();

        if (resultCode == Activity.RESULT_OK) {
            if (updateUser) {
                user_image.setImageURI(imgURI);
                if (uriConfig.size() > 0)
                    uriConfig.set(0, imgURI);
                else uriConfig.add(imgURI);
            } else {
                bot_image.setImageURI(imgURI);
                if (uriConfig.size() > 1)
                    uriConfig.set(1, imgURI);
                else {
                    if (uriConfig.size() == 0)
                        uriConfig.add(null);
                    uriConfig.add(imgURI);
                }
            }
        }
    }

    public void saveConfig(View view) {
        if (user_switch.isChecked())
            settingsdb.updateSettings("user", Uri.EMPTY);
        else
            settingsdb.updateSettings("user", uriConfig.get(0));

        if (bot_switch.isChecked())
            settingsdb.updateSettings("bot", Uri.EMPTY);
        else
            settingsdb.updateSettings("bot", uriConfig.get(1));

        Toast.makeText(getApplicationContext(), "Configuration saved!", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Exit the app to see the change", Toast.LENGTH_LONG).show();
    }

    public void clearRecords(View view) {
        chatsDatabaseHelper.clearChatRecords();
        Toast.makeText(getApplicationContext(), "Logs cleared!", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Exit the app to see the change", Toast.LENGTH_LONG).show();
    }
}