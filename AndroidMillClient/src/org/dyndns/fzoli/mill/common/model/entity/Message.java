package org.dyndns.fzoli.mill.common.model.entity;

import java.util.Date;

/**
 *
 * @author zoli
 */
public class Message {
    
    private Boolean sync;
    private MessageType type;
    private String sender, address, text;
    private Date sendDate;
    protected long sendTime;

    public Message(String address, String sender, String text, Date sendDate, MessageType type) {
        this.type = type;
        this.address = address;
        this.sender = sender;
        this.text = text;
        this.sendDate = sendDate;
        if (sendDate != null) this.sendTime = sendDate.getTime();
    }

    public MessageType getType() {
        return type;
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
    
    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public void syncSendDate(int diff) {
        if (sync != null && sync) return;
        sync = true;
        sendDate = new Date(sendDate.getTime() + diff);
    }
    
}