package com.romail.romail.entity;

import java.util.Date;

public class Email {

    private int index;
    private String sender;
    private String content;
    private String subject;
    private String date;
    private String receiver;

    public Email(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Email(String sender, String content, String subject, String date) {
        this.sender = sender;
        this.content = content;
        this.subject = subject;
        this.date = date;
    }


    public String[] getSender() {
        return sender.split(" ");
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        Date dateString=new Date(date);
        StringBuilder result = new StringBuilder(8);
        result.append(String.valueOf(dateString.getYear() - 100));
        result.append('/');
        int month = dateString.getMonth() + 1;
        if(month < 10){
            result.append('0');
        }
        result.append(String.valueOf(month));
        result.append('/');
        int day = dateString.getDate();
        if(day < 10){
            result.append('0');
        }
        result.append(String.valueOf(day));
        return result.toString();
    }

    public String getDetailDate(){
        Date dateString=new Date(date);
        StringBuilder result = new StringBuilder(17);
        result.append(getDate());
        result.append(' ');
        int hour = dateString.getHours();
        if(hour < 10){
            result.append('0');
        }
        result.append(String.valueOf(hour));
        result.append(':');
        int minute = dateString.getMinutes();
        if(minute < 10){
            result.append('0');
        }
        result.append(String.valueOf(minute));
        result.append(':');
        int second = dateString.getSeconds();
        if(second < 10){
            result.append('0');
        }
        result.append(String.valueOf(second));
        return result.toString();
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

}
