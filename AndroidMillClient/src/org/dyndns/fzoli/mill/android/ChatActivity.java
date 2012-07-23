package org.dyndns.fzoli.mill.android;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.dyndns.fzoli.android.widget.ConfirmDialog;
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

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//TODO: bugos a szerver-kliens időeltérés kompenzálás

public class ChatActivity extends AbstractMillOnlineActivity<ChatEvent, ChatData> {

	public static final String KEY_PLAYER = "player";
	
	private static final Map<String, Integer> emoticons = new HashMap<String, Integer>() {
		
		private static final long serialVersionUID = 1L;

		{
			put(":-)", R.drawable.emo_im_happy);
			put(":)", R.drawable.emo_im_happy);
			put(":-(", R.drawable.emo_im_sad);
			put(":(", R.drawable.emo_im_sad);
			put(";-)", R.drawable.emo_im_winking);
			put(";)", R.drawable.emo_im_winking);
			put(":-P", R.drawable.emo_im_tongue_sticking_out);
			put(":P", R.drawable.emo_im_tongue_sticking_out);
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
			put(":-D", R.drawable.emo_im_laughing);
			put(":D", R.drawable.emo_im_laughing);
			put("o_O", R.drawable.emo_im_wtf);
		}
		
	};
	
	private static final List<Integer> emoticonList = new ArrayList<Integer>();
	
	private static final Map<Integer, Integer> emoticonTexts = new HashMap<Integer, Integer>() {
		
		private static final long serialVersionUID = 1L;

		{
			emoticonList.clear();
			put(R.drawable.emo_im_happy, R.string.emo_im_happy);
			put(R.drawable.emo_im_sad, R.string.emo_im_sad);
			put(R.drawable.emo_im_winking, R.string.emo_im_winking);
			put(R.drawable.emo_im_tongue_sticking_out, R.string.emo_im_tongue_sticking_out);
			put(R.drawable.emo_im_surprised, R.string.emo_im_surprised);
			put(R.drawable.emo_im_kissing, R.string.emo_im_kissing);
			put(R.drawable.emo_im_yelling, R.string.emo_im_yelling);
			put(R.drawable.emo_im_cool, R.string.emo_im_cool);
			put(R.drawable.emo_im_money_mouth, R.string.emo_im_money_mouth);
			put(R.drawable.emo_im_foot_in_mouth, R.string.emo_im_foot_in_mouth);
			put(R.drawable.emo_im_embarrassed, R.string.emo_im_embarrassed);
			put(R.drawable.emo_im_angel, R.string.emo_im_angel);
			put(R.drawable.emo_im_undecided, R.string.emo_im_undecided);
			put(R.drawable.emo_im_crying, R.string.emo_im_crying);
			put(R.drawable.emo_im_lips_are_sealed, R.string.emo_im_lips_are_sealed);
			put(R.drawable.emo_im_laughing, R.string.emo_im_laughing);
			put(R.drawable.emo_im_wtf, R.string.emo_im_wtf);
		}
		
		public Integer put(Integer key, Integer value) {
			emoticonList.add(key);
			return super.put(key, value);
		};
		
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
		
		etChat.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View paramView) {
				showSmileyList();
				return true;
			}
			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.entryInsertSmiley:
				showSmileyList();
				break;
			case R.id.entryLoadMessages:
				showLoadDialog();
				break;
			case R.id.entryDeleteMessages:
				showRemoveDialog();
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public ChatModel createModel(Connection<Object, Object> connection) {
		return new ChatModel(connection);
	}
	
	@Override
	public ChatModel getModel() {
		return (ChatModel) super.getModel();
	}
	
	private void showLoadDialog() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int day) {
				Calendar c = Calendar.getInstance();
				c.set(year, month, day);
				Date date = c.getTime();
				if (date.after(new Date())) {
					Toast.makeText(ChatActivity.this, R.string.wrong_value, Toast.LENGTH_SHORT).show();
					return;
				}
				setAction(true);
				getModel().loadMessages(getPlayerName(), date, new ModelActionListener<ChatData>() {
					
					@Override
					public void modelActionPerformed(ModelActionEvent<ChatData> e) {
						new MillModelActivityAdapter<ChatData>(ChatActivity.this, e) {
							
							@Override
							public void onEvent(ChatData d) {
								for (Message m : d.getMessages()) {
									m.syncSendDate(getModel().getCache().getSync());
								}
								initMessages(d.getMessages(), true);
								setAction(false);
							}
							
						};
					}
					
				});
			}
			
		}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
	}
	
	private void showRemoveDialog() {
		new ConfirmDialog(ChatActivity.this, android.R.drawable.ic_dialog_alert, getString(R.string.delete_messages), getString(R.string.delete_messages_confirm), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				setAction(true);
				getModel().deleteMessages(getPlayerName(), new ModelActionListener<Integer>() {
					
					@Override
					public void modelActionPerformed(ModelActionEvent<Integer> e) {
						new IntegerMillModelActivityAdapter(ChatActivity.this, e) {
							
							@Override
							public void onEvent(int e) {
								setAction(false);
								lMessages.removeAllViews();
							}
							
						};
					}
					
				});
			}
			
		}).show();
	}
	
	private void showSmileyList() {
		final String KEY_ICON = "icon", KEY_LABEL = "label", KEY_TEXT = "text";
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.insert_smiley));
       
        final List<Map<String,Object>> objRows = new ArrayList<Map<String,Object>>();
        for (Integer key : emoticonList) {
        	String text = "";
        	Iterator<Entry<String, Integer>> it = emoticons.entrySet().iterator();
        	while (it.hasNext()) {
        		Entry<String, Integer> e2 = it.next();
        		if (e2.getValue().equals(key)) {
        			text = e2.getKey();
        			break;
        		}
        	}
        	
        	Map<String, Object> objRow = new HashMap<String, Object>();
            objRow.put(KEY_ICON, key);
            objRow.put(KEY_LABEL, getString(emoticonTexts.get(key)));
            objRow.put(KEY_TEXT, text);
        	objRows.add(objRow);
        }
        
        String[] strKeys = new String[] {KEY_ICON, KEY_LABEL, KEY_TEXT};
        int[] intViewIds = new int[] {R.id.ivEmoIcon, R.id.tvEmoLabel, R.id.tvEmoText};
        SimpleAdapter objEmoticonList = new SimpleAdapter(this, objRows, R.layout.emoticons, strKeys, intViewIds);
        builder.setAdapter(objEmoticonList , new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				String text = objRows.get(paramInt).get(KEY_TEXT).toString();
				int position = etChat.getSelectionStart();
				String msg = etChat.getText().toString();
				String start = msg.substring(0, position);
				start += text;
				String end = msg.substring(position);
				msg = start + end;
				etChat.setText(msg);
				etChat.setSelection(position + text.length());
			}
			
		});
        
        builder.show();
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
		if (reset) {
			lMessages.removeAllViews();
		}
		else {
			int index = lMessages.getChildCount() - 1;
			if (index >= 0) lMessages.removeViewAt(index);
		}
		LayoutInflater infalInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (Message msg : l) {
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
		View msgView = infalInflater.inflate(R.layout.chat_msg, null);
		msgView.setVisibility(View.INVISIBLE);
		lMessages.addView(msgView);
		if (!reset) {
			svChat.fullScroll(ScrollView.FOCUS_DOWN);
			etChat.requestFocus();
		}
	}
	
	@Override
	public boolean processModelChange(final ChatEvent e) {
		if (super.processModelChange(e)) {new String();
			if (messages != null) {
//				e.getMessage().setSendDate(new Date());
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
								for (Message m : d.getMessages()) {
									m.syncSendDate(getModel().getCache().getSync());
								}
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