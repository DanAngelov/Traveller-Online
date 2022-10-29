package com.example.travelleronline.general.util.converters;

import com.example.travelleronline.users.User;
import org.modelmapper.AbstractConverter;

import java.util.List;

public class UserSubscribersListToIntegerConverter extends AbstractConverter<List<User>,Integer> {

    @Override
    protected Integer convert(List<User> subscribers) {
        if(subscribers != null) {
            return subscribers.size();
        } else {
            return 0;
        }
    }

}