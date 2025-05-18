package com.example.chat.service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.service.storage.Chat;
import com.example.chat.service.storage.ChatStorage;
import com.example.chat.service.storage.ChatStorageImpl;


@RestController
public class ChatService {
    ChatStorage chatStorage = new ChatStorageImpl();
    
    @GetMapping("/chats/{senderId}")
    public List<Chat> getChats(@PathVariable(name = "senderId") String senderId) {
        return chatStorage.getChats(senderId);
    }

    @GetMapping("/health")
    public String getHealth() {
        return "Healthy";
    }

    @PutMapping("/savechat")
    public void saveChats(@RequestBody Chat chat) {
        chatStorage.saveChat(chat);
    }
}
