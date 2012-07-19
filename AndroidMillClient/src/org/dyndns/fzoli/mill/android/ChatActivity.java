package org.dyndns.fzoli.mill.android;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineActivity;
import org.dyndns.fzoli.mill.android.activity.IntegerMillModelActivityAdapter;
import org.dyndns.fzoli.mill.android.activity.MillModelActivityAdapter;
import org.dyndns.fzoli.mill.client.model.ChatModel;
import org.dyndns.fzoli.mill.common.model.entity.Message;
import org.dyndns.fzoli.mill.common.model.pojo.ChatData;
import org.dyndns.fzoli.mill.common.model.pojo.ChatEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChatActivity extends AbstractMillOnlineActivity<ChatEvent, ChatData> {

	public static final String KEY_PLAYER = "player";
	
	private ViewGroup lMessages;
	private ProgressBar pbChat;
	private EditText etChat;
	
	public String getPlayerName() {
		return getIntent().getStringExtra(KEY_PLAYER);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		setTitle(getString(R.string.chat) + " - " + getPlayerName());
		lMessages = (ViewGroup) findViewById(R.id.lMessages);
		pbChat = (ProgressBar) findViewById(R.id.pbChat);
		etChat = (EditText) findViewById(R.id.etChat);
	}
	
	@Override
	public ChatModel createModel(Connection<Object, Object> connection) {
		return new ChatModel(connection);
	}
	
	@Override
	public ChatModel getModel() {
		return (ChatModel) super.getModel();
	}
	
	private void setAction(boolean on) {
		pbChat.setVisibility(on ? View.VISIBLE : View.GONE);
		etChat.setEnabled(!on);
	}
	
	private void initMessages(List<Message> l) {
		lMessages.removeAllViews();
		for (Message msg : l) {
			LayoutInflater infalInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View msgView = infalInflater.inflate(R.layout.chat_msg, null);
	        TextView tvUser = (TextView) msgView.findViewById(R.id.tvUser);
	        TextView tvDate = (TextView) msgView.findViewById(R.id.tvDate);
	        TextView tvMessage = (TextView) msgView.findViewById(R.id.tvMessage);
	        Date now = new Date();
	        Date date = msg.getSendDate();
	        DateFormat dateFormat;
	        if (now.getDate() == date.getDate() && now.getMonth() == date.getMonth() && now.getYear() == date.getYear()) {
	        	dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault());
	        }
	        else {
	        	dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault());
	        }
	        tvUser.setText(msg.getSender());
	        tvDate.setText(dateFormat.format(date));
	        tvMessage.setText(msg.getText());
	        lMessages.addView(msgView);
		}
	}
	
	@Override
	public boolean processModelData(ChatData e) {
		boolean b = super.processModelData(e);
		if (b) {
			setAction(true);
			getModel().loadUnreadedMessages(getPlayerName(), new ModelActionListener<ChatData>() {
				
				@Override
				public void modelActionPerformed(ModelActionEvent<ChatData> e) {
					
					new MillModelActivityAdapter<ChatData>(ChatActivity.this, e) {
						
						@Override
						public void onEvent(ChatData e) {
							initMessages(e.getMessages());
							getModel().updateReadDate(getPlayerName(), new ModelActionListener<Integer>() {
								
								@Override
								public void modelActionPerformed(ModelActionEvent<Integer> e) {
									new IntegerMillModelActivityAdapter(ChatActivity.this, e) {
										
										@Override
										public void onEvent(int e) {
											//TODO
										}
										
									};
								}
								
							});
							setAction(false);
						}
						
					};
				}
				
			});
		}
		return b;
	}
	
}