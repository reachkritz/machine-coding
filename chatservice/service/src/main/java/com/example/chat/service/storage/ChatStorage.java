package com.example.chat.service.storage;

import java.util.List;

public interface ChatStorage {
    
    public List<Chat> getChats(String fromId);

    public void saveChat(Chat chat);
}
