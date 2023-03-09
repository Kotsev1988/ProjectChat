package com.example.projectchat.RoomDB;

import androidx.room.TypeConverter;

import java.math.BigInteger;

public class bigintconverter {
    @TypeConverter
    public Integer fromHobbies(BigInteger hobbies) {

        return hobbies.intValue();
    }


}
