package org.dyndns.fzoli.mill.common.key;

/**
 *
 * @author zoli
 */
public interface PlayerKeys extends BaseKeys {
    
    String KEY_USER = "user";
    String KEY_PASSWORD = "password";
    
    String REQ_IS_EMAIL_FREE = "is_email_free";
    String REQ_SAFE_SUSPEND_ACCOUNT = "safe_suspend_account";
    String REQ_SUSPEND_ACCOUNT = "suspend_account";
    String REQ_SAFE_REVALIDATE_EMAIL = "safe_revalidate_email";
    String REQ_REVALIDATE_EMAIL = "revalidate_email";
    String REQ_SAFE_SET_EMAIL = "safe_set_email";
    String REQ_SET_EMAIL = "set_email";
    String REQ_SET_PASSWORD = "set_password";
    String REQ_SET_SAFE_PASSWORD = "set_safe_password";
    String REQ_SIGN_OUT = "sign_out";
    String REQ_SIGN_IN = "sign_in";
    String REQ_SAFE_SIGN_IN = "safe_sign_in";
    String REQ_SET_PLAYER_STATE = "set_player_state";
    
}