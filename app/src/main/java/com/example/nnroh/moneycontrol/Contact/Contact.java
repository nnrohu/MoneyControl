package com.example.nnroh.moneycontrol.Contact;

import java.util.ArrayList;

public class Contact {
    public String id;
    public String photo;
    public String name;
    public ArrayList<ContactPhone> numbers;

    public Contact(String id, String photo, String name) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.numbers = new ArrayList<ContactPhone>();
    }

    public Contact(String id, String photo, String name, ArrayList<ContactPhone> numbers) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.numbers = numbers;
    }

    @Override
    public String toString() {
        String result = name;
        if (numbers.size() > 0) {
            ContactPhone number = numbers.get(0);
            result += " (" + number.number + " - " + number.type + ")";
        }

        return result;
    }


    public void addNumber(String number, String type) {
        numbers.add(new ContactPhone(number, type));
    }


}
