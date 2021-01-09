package com.example.myapplication;

public class listt {
    private int listId;
    private String name, listMS, codeimei;
    private int deviceId;
    private int userId;

    public listt(int listId, String name, int deviceId, int userId, String listMS, String codeimei) {
        this.listId = listId;
        this.name = name;
        this.deviceId = deviceId;
        this.userId = userId;
        this.listMS = listMS;
    }

    public listt(String name, int deviceId, int userId, String listMS, String codeimei) {
        this.name = name;
        this.deviceId = deviceId;
        this.userId = userId;
        this.listMS = listMS;
    }

    public listt() {

    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public String getName() {
        return name;
    }

    public String getlistMS() {
        return listMS;
    }

    public String getCodeimei() {
        return codeimei;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "List{" +
                "listId = " + listId +
                ", name = '" + name + '\'' +
                ", listMS = '" + listMS + '\'' +
                ", deviceId = " + deviceId +
                ", userId = " + userId +
                '}';
    }


}
