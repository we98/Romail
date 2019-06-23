package com.romail.romail;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.romail.romail.global.GlobalVariables;
import com.romail.romail.entity.Account;
import com.romail.romail.util.LoadingDialogManager;
import com.romail.romail.util.Pop3Manager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginButton;
    private Button moreInfoButton;
    private Spinner emailTypeSpinner;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText = findViewById(R.id.edittext_account_activity_login);
        passwordEditText = findViewById(R.id.edittext_password_activity_login);
        loginButton = findViewById(R.id.buttonlogin_login_activity_login);
        moreInfoButton = findViewById(R.id.buttonmore_login_activity_login);
        emailTypeSpinner = findViewById(R.id.spinner_email_activity_login);
        loginButton.setOnClickListener(this);
        moreInfoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonlogin_login_activity_login:
                login();
                break;
            case R.id.buttonmore_login_activity_login:
                forMoreInfo();
                break;
        }
    }

    private int loginResult = 0;
    private void login(){
        Account.buildAccount(emailEditText.getText().toString(),
                passwordEditText.getText().toString(), (String) (emailTypeSpinner.getSelectedItem()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    handler.sendEmptyMessage(GlobalVariables.START_TRANSACTION);
                    loginResult = Pop3Manager.verifyLogin();
                    handler.sendEmptyMessage(loginResult);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void forMoreInfo(){
        Intent intent = new Intent(this, MoreInfoActivity.class);
        startActivity(intent);
    }


    private Dialog dialog = null;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(dialog == null){
                dialog = LoadingDialogManager.createLoadingDialog(LoginActivity.this, "Loading...");
            }
            switch(msg.what) {
                case GlobalVariables.START_TRANSACTION:
                    LoadingDialogManager.showDialog(dialog);
                    break;
                case GlobalVariables.LOGIN_TRANSACTION_SUCCESS:
                    Intent intent = new Intent(LoginActivity.this, InboxActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    LoadingDialogManager.closeDialog(dialog);
                    dialog = null;
                    Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    break;
                case GlobalVariables.LOGIN_TRANSACTION_FAIL:
                    LoadingDialogManager.closeDialog(dialog);
                    dialog = null;
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    Account.clearCurrentAccount();
                    break;
                case GlobalVariables.NO_NETWORK_CONNECTION:
                    LoadingDialogManager.closeDialog(dialog);
                    dialog = null;
                    Toast.makeText(LoginActivity.this, "No network connection", Toast.LENGTH_SHORT).show();
                    Account.clearCurrentAccount();
                    break;
            }
        }
    };


}
