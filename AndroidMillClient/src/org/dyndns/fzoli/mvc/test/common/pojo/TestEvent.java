package org.dyndns.fzoli.mvc.test.common.pojo;

import org.dyndns.fzoli.mvc.test.common.key.TestModelKeys;

/**
 *
 * @author zoli
 */
public class TestEvent implements TestModelKeys {
    
    private String key, str;

    public TestEvent(String key, String str) {
        this.key = key;
        this.str = str;
    }

    public String getKey() {
        return key;
    }
    
    public String getStr() {
        return str;
    }
    
}