package com.fiiiiive.zippop.chat.model.dto;

import lombok.Data;


@Data
public class ChatMessageReq {
    private String sender;
    private String content;
    private String roomName;

    private String customerEmail;
    private String companyEmail;

}
