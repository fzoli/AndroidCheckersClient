package org.dyndns.fzoli.mill.android;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineActivity;
import org.dyndns.fzoli.mill.android.activity.IntegerMillModelActivityAdapter;
import org.dyndns.fzoli.mill.android.activity.MillModelActivityAdapter;
import org.dyndns.fzoli.mill.client.model.ChatModel;
import org.dyndns.fzoli.mill.client.model.PlayerModel;
import org.dyndns.fzoli.mill.common.model.entity.BasePlayer;
import org.dyndns.fzoli.mill.common.model.entity.Message;
import org.dyndns.fzoli.mill.common.model.entity.Player;
import org.dyndns.fzoli.mill.common.model.pojo.ChatData;
import org.dyndns.fzoli.mill.common.model.pojo.ChatEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class ChatActivity extends AbstractMillOnlineActivity<ChatEvent, ChatData> {

	public static final String KEY_PLAYER = "player";
	
	private static final HashMap<String, Integer> emoticons = new HashMap<String, Integer>() {
		
		private static final long serialVersionUID = 1L;

		{
			put(":)", R.drawable.emo_im_happy);
			put(":-)", R.drawable.emo_im_happy);
			put(":(", R.drawable.emo_im_sad);
			put(":-(", R.drawable.emo_im_sad);
			put(";)", R.drawable.emo_im_winking);
			put(";-)", R.drawable.emo_im_winking);
			put(":P", R.drawable.emo_im_tongue_sticking_out);
			put(":-P", R.drawable.emo_im_tongue_sticking_out);
			put("=-O", R.drawable.emo_im_surprised);
			put(":-*", R.drawable.emo_im_kissing);
			put(":O", R.drawable.emo_im_yelling);
			put("B-)", R.drawable.emo_im_cool);
			put(":-$", R.drawable.emo_im_money_mouth);
			put(":-!", R.drawable.emo_im_foot_in_mouth);
			put(":-[", R.drawable.emo_im_embarrassed);
			put("O:-)", R.drawable.emo_im_angel);
			put(":-\\", R.drawable.emo_im_undecided);
			put(":'(", R.drawable.emo_im_crying);
			put(":X", R.drawable.emo_im_lips_are_sealed);
			put(":-X", R.drawable.emo_im_lips_are_sealed);
			put(":D", R.drawable.emo_im_laughing);
			put(":-D", R.drawable.emo_im_laughing);
			put("o_O", R.drawable.emo_im_wtf);
		}
		
	};
	
	private String sender, address;
	private List<Message> messages;
	
	private ViewGroup lMessages;
	private ScrollView svChat;
	private ProgressBar pbChat;
	private EditText etChat;
	
	public String getPlayerName() {
		return getIntent().getStringExtra(KEY_PLAYER);
	}
	
	@Override
	public boolean onConnectionBinded() {
		PlayerModel playerModel = (PlayerModel) getConnectionBinder().getModelMap().get(HomeActivity.class);
		Player p = playerModel.getCache().getPlayer();
		sender = p.getName();
		address = getPlayerName();
		for (BasePlayer bp : p.getFriendList()) {
			if (bp.getPlayerName().equals(getPlayerName())) {
				address = bp.getName();
				setTitle(getString(R.string.chat) + " - " + address);
				break;
			}
		}
		return super.onConnectionBinded();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		lMessages = (ViewGroup) findViewById(R.id.lMessages);
		svChat = (ScrollView) findViewById(R.id.svChat);
		pbChat = (ProgressBar) findViewById(R.id.pbChat);
		etChat = (EditText) findViewById(R.id.etChat);
		
		etChat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent) {
				String message = etChat.getText().toString().trim();
				if (!message.isEmpty()) {
					sendMessage(message);
					return true;
				}
				return false;
			}
			
		});
		
	}
	
	@Override
	public ChatModel createModel(Connection<Object, Object> connection) {
		return new ChatModel(connection);
	}
	
	@Override
	public ChatModel getModel() {
		return (ChatModel) super.getModel();
	}
	
	private Spannable getSmiledText(String text) {
		return getSmiledText(this, text);
	}
	
	private void setAction(boolean on) {
		pbChat.setVisibility(on ? View.VISIBLE : View.GONE);
		etChat.setEnabled(!on);
	}
	
	private void sendMessage(final String text) {
		setAction(true);
		getModel().sendMessage(getPlayerName(), text, new ModelActionListener<Integer>() {
			
			@Override
			public void modelActionPerformed(ModelActionEvent<Integer> e) {
				new IntegerMillModelActivityAdapter(ChatActivity.this, e) {
					
					@Override
					public void onEvent(int e) {
						setAction(false);
						etChat.setText("");
						addMessage(new Message(address, sender, text, new Date()));
					}
					
				};
			}
			
		});
	}
	
	private void addMessage(final Message m) {
		initMessages(new ArrayList<Message>() {
			
			private static final long serialVersionUID = 1L;
			
			{
				add(m);
			}
			
		}, false);
		messages.add(m);
	}
	
	private void initMessages(List<Message> l, boolean reset) {
		if (reset) lMessages.removeAllViews();
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
	        tvMessage.setText(getSmiledText(msg.getText()));
	        lMessages.addView(msgView);
		}
		if (!reset) {
			svChat.fullScroll(ScrollView.FOCUS_DOWN);
			etChat.requestFocus();
		}
	}
	
	@Override
	public boolean processModelChange(final ChatEvent e) {
		if (super.processModelChange(e)) {
			if (messages != null) {
				addMessage(e.getMessage());
				getModel().updateReadDate(getPlayerName(), new ModelActionListener<Integer>() {
					
					@Override
					public void modelActionPerformed(ModelActionEvent<Integer> d) {
						new IntegerMillModelActivityAdapter(ChatActivity.this, d) {
							
							@Override
							public void onEvent(int i) {
								messages.add(e.getMessage());
							}
							
						};
					}
					
				});
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean processModelData(ChatData e) {
		messages = getConnectionBinder().getMessages().get(getPlayerName());
		if (super.processModelData(e)) {
			if (messages == null) {
				setAction(true);
				getModel().loadUnreadedMessages(getPlayerName(), new ModelActionListener<ChatData>() {
					
					@Override
					public void modelActionPerformed(ModelActionEvent<ChatData> e) {
						
						new MillModelActivityAdapter<ChatData>(ChatActivity.this, e) {
							
							@Override
							public void onEvent(final ChatData d) {
								getModel().updateReadDate(getPlayerName(), new ModelActionListener<Integer>() {
									
									@Override
									public void modelActionPerformed(ModelActionEvent<Integer> e) {
										new IntegerMillModelActivityAdapter(ChatActivity.this, e) {
											
											@Override
											public void onEvent(int e) {
												messages = new ArrayList<Message>();
												getConnectionBinder().getMessages().put(getPlayerName(), messages);
												messages.addAll(d.getMessages());
												initMessages(d.getMessages(), true);
												setAction(false);
											}
											
										};
									}
									
								});
							}
							
						};
					}
					
				});
			}
			else {
				initMessages(messages, true);
			}
			return true;
		}
		return false;
	}
	
	public static Spannable getSmiledText(Context context, String text) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		int index;
		for (index = 0; index < builder.length(); index++) {
		    for (Entry<String, Integer> entry : emoticons.entrySet()) {
		        int length = entry.getKey().length();
		        if (index + length > builder.length())
		            continue;
		        if (builder.subSequence(index, index + length).toString().equalsIgnoreCase(entry.getKey())) {
		            builder.setSpan(new ImageSpan(context, entry.getValue()), index, index + length,
		            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		            index += length - 1;
		            break;
		        }
		}
		}
		return builder;
	}

}