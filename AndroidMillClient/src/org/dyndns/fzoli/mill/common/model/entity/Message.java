package org.dyndns.fzoli.mill.common.model.entity;

import java.util.Date;

/**
 *
 * @author zoli
 */
public class Message {
    
    private String sender, address, text;
    private Date sendDate;
    protected long sendTime;

    public Message(String address, String sender, String text, Date sendDate) {
        this.address = address;
        this.sender = sender;
        this.text = text;
        this.sendDate = sendDate;
        if (sendDate != null) this.sendTime = sendDate.getTime();
    }

    public String getText() {
        return text;
    }

    public String getAddress() {
        return address;
    }

    public String getSender() {
        return sender;
    }

    public Date getSendDate() {
        return sendDate;
    }
    
}