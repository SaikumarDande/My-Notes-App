package com.example.notes.NotesDate;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class NoteDate {

    public static String dateFromLong(long time){
        DateFormat format1 = new SimpleDateFormat("EEE, dd MMM yyyy 'at' hh:mm aaa", Locale.US);
        return format1.format(new Date(time));
    }
}
