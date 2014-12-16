package com.yangc.ichat.database.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table T_ICHAT_HISTORY.
 */
public class TIchatHistory {

    private Long id;
    private String uuid;
    private String username;
    private String chat;
    private Long type;
    private Long chatStatus;
    private Long transmitStatus;
    private java.util.Date date;

    public TIchatHistory() {
    }

    public TIchatHistory(Long id) {
        this.id = id;
    }

    public TIchatHistory(Long id, String uuid, String username, String chat, Long type, Long chatStatus, Long transmitStatus, java.util.Date date) {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
        this.chat = chat;
        this.type = type;
        this.chatStatus = chatStatus;
        this.transmitStatus = transmitStatus;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(Long chatStatus) {
        this.chatStatus = chatStatus;
    }

    public Long getTransmitStatus() {
        return transmitStatus;
    }

    public void setTransmitStatus(Long transmitStatus) {
        this.transmitStatus = transmitStatus;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

}
