package com.example.anas.bonusassingment;

import java.util.List;

/**
 * Created by Anas on 11/11/2017.
 */

public class PhoneEvent {
    private List<Phone> message;

    public PhoneEvent(List<Phone> message) {
        this.message = message;
    }

    public PhoneEvent(String string) {

    }


    public List<Phone> getMessage() {
        return message;
    }
}
