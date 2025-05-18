package com.example.chat.service.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatStorageImpl implements ChatStorage{
    List<Chat> chatList = new ArrayList<>();

    @Override
    public List<Chat> getChats(String fromId) {
        return chatList.stream().filter(chat -> chat.getSenderId().equals(fromId)).collect(Collectors.toList());
    }

    @Override
    public void saveChat(Chat chat) {
        chatList.add(chat);
    } 
}
