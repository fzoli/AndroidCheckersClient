package org.dyndns.fzoli.mvc.test.client.model;

import java.util.List;

import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
import org.dyndns.fzoli.mvc.client.model.json.CachedJSONModel;
import org.dyndns.fzoli.mvc.common.request.map.RequestMap;
import org.dyndns.fzoli.mvc.test.common.key.TestModelKeys;
import org.dyndns.fzoli.mvc.test.common.pojo.TestData;
import org.dyndns.fzoli.mvc.test.common.pojo.TestEvent;

/**
 *
 * @author zoli
 */
public class BaseTestModel extends CachedJSONModel<TestEvent, TestData> {
    
    public BaseTestModel(Connection<Object, Object> connection, String key) {
        this(connection, key, null);
    }

    public BaseTestModel(Connection<Object, Object> connection, String key, ModelActionListener<TestData> action) {
        super(connection, key, TestEvent.class, TestData.class, action);
    }

    public String getStr() {
        return getCache().getStr();
    }

    public String getStr2() {
        return getCache().getStr2();
    }
    
    private RequestMap createStrRequestMap(String key, String str) {
        RequestMap mp = new RequestMap();
        mp.setFirst(TestModelKeys.MSG_PROPERTY, key);
        mp.setFirst(TestModelKeys.MSG_VALUE, str);
        return mp;
    }
    
    public int setStr(String str) {
        return setStr(TestEvent.VAL_STR, str);
    }
    
    public int setStr2(String key, String str) {
        return setStr(TestEvent.VAL_STR_2, str);
    }
    
    private int setStr(String key, String str) {
        if (getStr().equals(str)) return 0;
        return setProperty(createStrRequestMap(key, str));
    }
    
    public void setStr(String str, ModelActionListener<Integer> action) {
        setStr(TestEvent.VAL_STR, str, action);
    }
    
    public void setStr2(String str, ModelActionListener<Integer> action) {
        setStr(TestEvent.VAL_STR_2, str, action);
    }
    
    private void setStr(String key, String str, ModelActionListener<Integer> action) {
        setProperty(createStrRequestMap(key, str), action);
    }
    
    @Override
    protected void updateCache(List<TestEvent> es, TestData cache) {
        for (TestEvent e : es) {
            if (e.getKey().equals(TestEvent.VAL_STR)) cache.setStr(e.getStr());
            if (e.getKey().equals(TestEvent.VAL_STR_2)) cache.setStr2(e.getStr());
        }
    }
    
}