package com.romail.romail;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.romail.romail.global.GlobalVariables;
import com.romail.romail.entity.Account;
import com.romail.romail.entity.Email;
import com.romail.romail.util.Pop3Manager;

public class EmailInfoActivity extends AppCompatActivity {

    private TextView sender;
    private TextView subject;
    private TextView date;
    private TextView content;

    private int currentEmailIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_info);
        Intent intent = getIntent();
        currentEmailIndex = intent.getIntExtra("index", 0);
        Email email = Account.getCurrentAccount().getEmails().get(currentEmailIndex);
        String[] senderName = email.getSender();
        setTitle(senderName[senderName.length - 1]);
        subject = findViewById(R.id.tv_subject_emailinfo);
        subject.setText(email.getSubject());
        date = findViewById(R.id.tv_date_emailinfo);
        date.setText(email.getDetailDate());
        content = findViewById(R.id.tv_content_emailinfo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            content.setText(Html.fromHtml(email.getContent(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            content.setText(Html.fromHtml(email.getContent()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_emailinfo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_emailinfo:
                Account.getCurrentAccount().deleteOneEmail(currentEmailIndex);
                delete();
                finish();
                break;
            case R.id.menu_reply_emailinfo:
                Toast.makeText(this, "Menu Item reply selected",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_forward_emailinfo:
                Toast.makeText(this, "Menu Item forward selected",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.home:
                Toast.makeText(this, "Menu Item home selected",
                        Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int loginResult = 0;
    private void delete(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    loginResult = Pop3Manager.delete(currentEmailIndex + 1);
                    handler.sendEmptyMessage(loginResult);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case GlobalVariables.DELETE_TRANSACTION_SUCCESS:
                    Toast.makeText(EmailInfoActivity.this, "Delete successfully", Toast.LENGTH_SHORT).show();
                    break;
                case GlobalVariables.DELETE_TRANSACTION_FAIL:
                    Toast.makeText(EmailInfoActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    };



}
