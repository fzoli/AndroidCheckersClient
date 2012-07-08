package org.dyndns.fzoli.mill.client.model;

import java.io.InputStream;
import org.dyndns.fzoli.mill.common.key.BaseKeys;
import org.dyndns.fzoli.mill.common.model.pojo.BaseCaptchaPojo;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
import org.dyndns.fzoli.mvc.client.model.json.CachedJSONModel;
import org.dyndns.fzoli.mvc.common.request.map.RequestMap;

/**
 *
 * @author zoli
 */
public abstract class AbstractMillModel<EventObj, PropsObj> extends CachedJSONModel<EventObj, PropsObj> implements BaseKeys {

    protected final Class<PropsObj> PROPS_CLASS;
    
    public AbstractMillModel(Connection<Object, Object> connection, String modelKey, Class<EventObj> eventClass, Class<PropsObj> propsClass) {
        super(connection, modelKey, eventClass, propsClass);
        PROPS_CLASS = propsClass;
    }
    
    public boolean hasCaptchaInfo(PropsObj data) {
        try {
            @SuppressWarnings("unused")
            BaseCaptchaPojo d = (BaseCaptchaPojo)data;
            return true;
        }
        catch (ClassCastException ex) {
            return false;
        }
    }
    
    public int getCaptchaWidth(PropsObj data) {
        if (hasCaptchaInfo(data)) return ((BaseCaptchaPojo)data).getCaptchaWidth();
        return 0;
    }
    
    public boolean isCaptchaValidated(PropsObj data) {
        if (hasCaptchaInfo(data)) return ((BaseCaptchaPojo)data).isCaptchaValidated();
        return false;
    }
    
    public InputStream getCaptcha(boolean reinit) {
        return getImage(createCaptchaMap(reinit));
    }
    
    public void getCaptcha(boolean reinit, ModelActionListener<InputStream> callback) {
        getImage(createCaptchaMap(reinit), callback);
    }
    
    public void validateCaptcha(String answer, ModelActionListener<Integer> callback) {
        RequestMap map = new RequestMap();
        map.setFirst(KEY_REQUEST, REQ_VALIDATE_CAPTCHA);
        map.setFirst(KEY_VALUE, answer);
        askModel(map, callback);
    }
    
    public int setCaptchaSize(int w) {
        if (w < 200) w = 200;
        if (w > 1000) w = 1000;
        RequestMap map = new RequestMap();
        map.setFirst(KEY_REQUEST, REQ_SET_CAPTCHA_SIZE);
        map.setFirst(KEY_VALUE, String.valueOf(w));
        return askModel(map);
    }
    
    public int setCaptchaBgColor(boolean white) {
        RequestMap map = new RequestMap();
        map.setFirst(KEY_REQUEST, REQ_SET_CAPTCHA_COLOR);
        map.setFirst(KEY_VALUE, Boolean.toString(white));
        return askModel(map);
    }
    
    private RequestMap createCaptchaMap(boolean reinit) {
        RequestMap map = new RequestMap();
        map.setFirst(KEY_REQUEST, reinit ? REQ_CREATE_CAPTCHA : REQ_GET_CAPTCHA);
        return map;
    }
    
    public static <T> T getEnumValue(Class<T> clazz, ModelActionEvent<Integer> mae) {
        return getEnumValue(clazz, mae.getEvent());
    }
    
    public static <T> T getEnumValue(Class<T> clazz, int i) {
        return clazz.getEnumConstants()[i];
    }
    
}