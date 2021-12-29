package com.getaroom.app.entity;

public class RoomStyle {
    
    private String room;
    private String top;
    private String left;
    private String width;
    private String height;

    public RoomStyle() {}

    public RoomStyle(String room, String top, String left, String width, String height) {
        this.room = room;
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
    }

    public String getRoom() { return room; }
    public String getTop() { return top; }
    public String getLeft() { return left; }
    public String getWidth() { return width; }
    public String getHeight() { return height; }

    public void setRoom(String room) { this.room = room; }
    public void setTop(String top) { this.top = top; }
    public void setLeft(String left) { this.left = left; }
    public void setWidth(String width) { this.width = width; }
    public void setHeight(String height) { this.height = height; }
}
