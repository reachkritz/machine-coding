package com.example.chat.service.storage;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class Chat {
    String senderId;
    String receiverId;
    String message;

    public Chat(String senderId, String receiverId, String message){
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }
}
