package org.dyndns.fzoli.mill.android;

import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineExpandableListActivity;
import org.dyndns.fzoli.mill.android.activity.IntegerMillModelActivityAdapter;
import org.dyndns.fzoli.mill.android.activity.MillModelActivityUtil;
import org.dyndns.fzoli.mill.android.activity.home.PlayerAdapter;
import org.dyndns.fzoli.mill.android.activity.home.PlayerGroupAdapter;
import org.dyndns.fzoli.mill.android.activity.home.PlayerInfo;
import org.dyndns.fzoli.mill.android.activity.home.PlayerInfo.Status;
import org.dyndns.fzoli.mill.android.entity.UserInfo;
import org.dyndns.fzoli.mill.android.service.MillConnectionBinder.LoginMode;
import org.dyndns.fzoli.mill.client.model.PlayerModel;
import org.dyndns.fzoli.mill.common.key.PlayerReturn;
import org.dyndns.fzoli.mill.common.model.entity.BasePlayer;
import org.dyndns.fzoli.mill.common.model.entity.OnlineStatus;
import org.dyndns.fzoli.mill.common.model.entity.Player;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

//TODO: képfeltöltés - Tárolás byte tömbbel objectdb-ben. Feltöltés előtt gallériában vágható a kép.
public class HomeActivity extends AbstractMillOnlineExpandableListActivity<PlayerEvent, PlayerData> {
	
	private PlayerAdapter pa;
	private PlayerGroupAdapter adapter;
	
	@Override
	public boolean onConnectionBinded() {
		// ha egy előző ablakban már megjelent a kapcsolódás hiba ablak és kilépésre mentek,
		// bejelentkező ablak megnyitása, és továbblépés megállítása
		Log.i("test","onBind");
		switch (getConnectionBinder().getLoginMode()) {
			case INVISIBLE_ERROR:
				getContextUtil().openSignIn();
				return false;
		}
		return super.onConnectionBinded();
	}
	
	private static String KEY_IS_SIGNING_IN = "is_signing_in";
	
	@SuppressWarnings("unchecked")
	public static boolean isSigningIn(MillModelActivityUtil util) {
		try {
			return Boolean.parseBoolean(util.getConnectionBinder().getVars().get(KEY_IS_SIGNING_IN));
		}
		catch (Exception e) {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void setSigningIn(MillModelActivityUtil util, boolean value) {
		try {
			util.getConnectionBinder().getVars().put(KEY_IS_SIGNING_IN, Boolean.toString(value));
		}
		catch (Exception e) {
			Log.i("test", "ex", e);
		}
	}
	
	@Override
	public boolean onPrepareModelCreate(ModelActionEvent<PlayerData> e) {
		if (getConnectionBinder() != null) {
			if (e.getType() == ModelActionEvent.TYPE_EVENT) {
				switch (getConnectionBinder().getLoginMode()) {
					case SIGNED_IN: // kliens szerint be van lépve
						if (e.getEvent().getPlayerName() != null) { // szerver szerint is
							setProgressMessage(R.string.loading);
							if (isSigningIn(getContextUtil())) {
								if (!e.getEvent().getPlayer().isValidated()) { // ha nincs még validálva, beállítás ablak előhozása
									startActivity(new Intent(this, PlayerAccountSettingsActivity.class));
									return false;
								}
								setSigningIn(getContextUtil(), false);
							}
						}
						else if (signIn(e)) { // ha a szerveren nincs belépve valamiért, belépés
								return false;
						}
						break;
					case SIGNING_IN: // bejelentkezés kérelem
						if (signIn(e)) return false;
						break;
					case SIGNING_OUT: // kijelentkezés kérelem
						if(signOut(e)) return false;
						break;
					case KICKED:
						getModelMap().clear();
						getContextUtil().openSignIn();
						return false;
					default: // egyébként bejelentkező képernyőre vissza
						getContextUtil().openSignIn();
						return false;
				}
			}
			else {
				getConnectionBinder().setLoginMode(LoginMode.ERROR);
				getContextUtil().openSignIn();
				return false;
			}
		}
		return super.onPrepareModelCreate(e);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		getExpandableListView().setGroupIndicator(null);
	}
	
	@Override
	public void onBackPressed() {
		if (getConnectionBinder() != null && getConnectionBinder().getLoginMode() == LoginMode.SIGNED_IN) {
			getConnectionBinder().setLoginMode(LoginMode.SIGNING_OUT);
			setProgressDialog(true);
			rebindConnectionService();
			return;
		}
		super.onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.entryAccountSettings:
				startActivity(new Intent(this, PlayerSettingsActivity.class));
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public PlayerModel createModel(Connection<Object, Object> connection) {
		return new PlayerModel(connection);
	}
	
	@Override
	public PlayerModel getModel() {
		return (PlayerModel) super.getModel();
	}
	
	@Override
	public boolean processModelChange(PlayerEvent e) {
		if (super.processModelChange(e)) {
			if (e.getType() != null) {
				switch(e.getType()) {
					case SIGNIN:
						if (adapter.findPlayerInfo(e.getChangedPlayer()) == null) {
							//TODO: adat lekérése
						}
						adapter.setStatus(e.getChangedPlayer(), PlayerInfo.Status.ONLINE);
						break;
					case SIGNOUT:
						adapter.setStatus(e.getChangedPlayer(), PlayerInfo.Status.OFFLINE);
						break;
					case SUSPEND:
						//TODO: törölni a listából
						break;
				}
			}
		}
		return true;
	}

	@Override
	public boolean processModelData(PlayerData e) {
		if (super.processModelData(e)) {
			//bejelentkezett felhasználó inicializálása
			ListView lvPlayer = (ListView) findViewById(R.id.lvPlayer);
			pa = new PlayerAdapter(this);
			pa.getPlayerList().add(new PlayerInfo(e.getPlayer().isOnline() ? Status.ONLINE : Status.INVISIBLE, e.getPlayer().getPlayerName(), e.getPlayer().getName(), "", null));
			lvPlayer.setAdapter(pa);
			final AlertDialog stateSelectDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.state_change)
			.setItems(new CharSequence[]{getText(R.string.online), getText(R.string.invisible)}, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case 0:
							setOnlineStatus(OnlineStatus.ONLINE);
							break;
						case 1:
							setOnlineStatus(OnlineStatus.INVISIBLE);
					}
				}
				
			})
			.create();
			lvPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					stateSelectDialog.show();
				}
				
			});
			//felhasználólista inicializálása
			adapter = new PlayerGroupAdapter(this);
			final String friends = getString(R.string.friends);
			final String wishedFriends = getString(R.string.wished_friends);
			final String blockedUsers = getString(R.string.blocked_users);
			Player p = getModel().getCache().getPlayer();
			for (BasePlayer bp : p.getFriendList()) {
				adapter.addItem(new PlayerInfo(bp.isOnline() ? Status.ONLINE : Status.OFFLINE, bp.getPlayerName(), bp.getName(), friends, null));
			}
			for (BasePlayer bp : p.getFriendWishList()) {
				adapter.addItem(new PlayerInfo(Status.INVISIBLE, bp.getPlayerName(), bp.getName(), wishedFriends, null));
			}
			for (BasePlayer bp : p.getBlockedUserList()) {
				adapter.addItem(new PlayerInfo(Status.BLOCKED, bp.getPlayerName(), bp.getName(), blockedUsers, null));
			}
			setListAdapter(adapter);
			
			if (adapter.isEmpty()) {
				TextView tvEmpty = (TextView) findViewById(R.id.tvEmpty);
				tvEmpty.setVisibility(View.VISIBLE);
			}
			
		}
		return true;
	}
	
	private boolean signIn(ModelActionEvent<PlayerData> e) {
		try {
			setProgressMessage(R.string.signing_in);
			UserInfo ui = getConnectionBinder().getUserInfo();
			PlayerReturn ret = getReturn(getModel().signIn(ui.getUser(), ui.getPasswordHash(), true));
			switch (ret) {
				case OK:
					setSigningIn(getContextUtil(), true);
					if (getModelMap() != null) {
						getModelMap().free(SignUpActivity.class);
					}
					Log.i("test","sign in ok");
					getConnectionBinder().setLoginMode(LoginMode.SIGNED_IN);
					getModel().reinitCache();
					rebindConnectionService();
					return true;
				default:
					Log.i("test","sign in NOT ok");
					getConnectionBinder().setLoginMode(LoginMode.SIGN_IN_FAILED);
					getContextUtil().openSignIn();
					return true;
			}
		}
		catch (Exception ex) {
			return false;
		}
	}
	
	private boolean signOut(ModelActionEvent<PlayerData> e) {
		try {
			if (e.getEvent().getPlayerName() != null) {
				setProgressMessage(R.string.signing_out);
				getModel().signOut();
				getConnectionBinder().setLoginMode(LoginMode.SIGNED_OUT);
				getContextUtil().openSignIn();
			}
			else {
				getConnectionBinder().setLoginMode(LoginMode.SIGNED_OUT);
			}
			return true;
		}
		catch (Exception ex) {
			return false;
		}
	}
	
	private void setOnlineStatus(final OnlineStatus ps) {
		getModel().setOnlineStatus(ps, new ModelActionListener<Integer>() {
			
			@Override
			public void modelActionPerformed(ModelActionEvent<Integer> e) {
				new IntegerMillModelActivityAdapter(HomeActivity.this, e) {
					
					@Override
					public void onEvent(int e) {
						switch (getReturn(e)) {
							case OK: //TODO: Status és State ugyan az...
								pa.setStatus(0, ps.equals(OnlineStatus.ONLINE) ? Status.ONLINE : Status.INVISIBLE);
						}
					}
					
				};
			}
			
		});
	}
	
	private void setProgressMessage(final int msg) {
		if (getProgressDialog() == null) return;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				getProgressDialog().setMessage(getText(msg));
			}
			
		});
	}
	
	private PlayerReturn getReturn(int i) {
		return getEnumValue(PlayerReturn.class, i);
	}
	
}