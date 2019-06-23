package com.romail.romail;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.romail.romail.global.GlobalVariables;
import com.romail.romail.entity.Account;
import com.romail.romail.entity.Email;
import com.romail.romail.util.AlertDialogManager;

public class InboxActivity extends AppCompatActivity {

    private InboxListViewAdapter inboxListViewAdapter;
    private ListView inboxList;
    private TextView accountTV;
    private ImageView exitIV;

    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        inboxList = findViewById(R.id.listview_inbox_activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_actionbar_activity_inbox);

        View view = actionBar.getCustomView();
        accountTV = view.findViewById(R.id.tv_current_account_main_activity);
        accountTV.setText(Account.getCurrentAccount().getCurrentAccountEmail() + Account.getCurrentAccount().getCurrentAccountEmailType());
        exitIV = view.findViewById(R.id.imageview_exit);
        exitIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog == null){
                    AlertDialogManager.createAlertDialog(InboxActivity.this, "Sure to logout?");
                }
                AlertDialogManager.showDialog(dialog);
            }
        });

        if(inboxListViewAdapter == null){
            inboxListViewAdapter = new InboxListViewAdapter(this);
        }
        inboxList.setAdapter(inboxListViewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(inboxListViewAdapter != null){
            inboxListViewAdapter.notifyDataSetChanged();
        }
    }
}

class InboxListViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    public InboxListViewAdapter(Context context){
        this.context = context;
        layoutInflater =LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return Account.getCurrentAccount().getEmails().size();
    }
    @Override
    public Object getItem(int i) {
        return Account.getCurrentAccount().getEmails().get(i);
    }
    @Override
    public long getItemId(int i) {
        return Account.getCurrentAccount().getEmails().get(i).getIndex();
    }
    static class ViewHolder{
        int id;
        private ImageView imgSender;
        private TextView tvSender, tvSubject, tvContent, tvDate, tvImg;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Email email = Account.getCurrentAccount().getEmails().get(i);
        ViewHolder viewHolder = null;
        if(view == null){
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.listitem_activity_inbox, null);
            viewHolder.imgSender = view.findViewById(R.id.img_sender);
            viewHolder.tvSender = view.findViewById(R.id.tv_sender);
            viewHolder.tvSubject = view.findViewById(R.id.tv_subject);
            viewHolder.tvContent = view.findViewById(R.id.tv_content);
            viewHolder.tvDate = view.findViewById(R.id.tv_date);
            viewHolder.tvImg = view.findViewById(R.id.tv_img);
            viewHolder.id = i + 1;
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        final int index = i;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EmailInfoActivity.class);
                intent.putExtra("index", index);
                context.startActivity(intent);
            }
        });
        int colorIndex = email.getSender().hashCode() % 5;
        if(colorIndex < 0){
            colorIndex = -colorIndex;
        }
        String[] senderName = email.getSender();
        viewHolder.imgSender.setBackgroundColor(GlobalVariables.COLORS[colorIndex]);
        viewHolder.tvSender.setText(senderName[senderName.length - 1]);
        viewHolder.tvSubject.setText(email.getSubject());
        viewHolder.tvContent.setText(email.getContent());
        viewHolder.tvDate.setText(email.getDate());
        viewHolder.tvImg.setText(String.valueOf(senderName[0].charAt(0)));
        return view;
    }
}
