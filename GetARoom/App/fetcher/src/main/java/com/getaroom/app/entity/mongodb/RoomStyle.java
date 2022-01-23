package com.getaroom.app.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class RoomStyle {
    
    @Id
    public String id;
    
    public String room;
    public String top;
    public String left;
    public String width;
    public String height;

    public RoomStyle() {}

    // public String getRoom() { return room; }
    // public String getTop() { return top; }
    // public String getLeft() { return left; }
    // public String getWidth() { return width; }
    // public String getHeight() { return height; }

    // public void setRoom(String room) { this.room = room; }
    // public void setTop(String top) { this.top = top; }
    // public void setLeft(String left) { this.left = left; }
    // public void setWidth(String width) { this.width = width; }
    // public void setHeight(String height) { this.height = height; }
}
