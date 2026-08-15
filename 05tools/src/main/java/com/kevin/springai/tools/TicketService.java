package com.kevin.springai.tools;

import org.springframework.stereotype.Service;

@Service
public class TicketService {

    public void cancel(String ticketNumber, String name) {
        System.out.println("退票成功");
    }

}
