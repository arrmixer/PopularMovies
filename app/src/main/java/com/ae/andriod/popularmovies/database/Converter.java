package com.ae.andriod.popularmovies.database;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converter {


    @TypeConverter
    public static List<String> toList(String s) {

        List<String> strings = new ArrayList<>();

        String[] array = s.split("......LL");

        strings = Arrays.asList(array);

        return strings;
    }

    @TypeConverter
    public static String toString(List<String> strings) {
        StringBuilder sb = new StringBuilder();

        for (String s : strings) {
            if ((strings.size() - 1) == strings.indexOf(s)) {
                sb.append(s);
                break;
            }
            sb.append(s + "......LL");
        }


        return sb.toString();
    }
}
