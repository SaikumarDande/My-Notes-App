package com.example.notes.model;

public class Notes {

    private String input_notes;
    private long notes_date;


    public Notes(){}
    public Notes(String input_notes, long notes_date) {
        this.input_notes = input_notes;
        this.notes_date = notes_date;
    }

    public String getInput_notes() {
        return input_notes;
    }

    public void setInput_notes(String input_notes) {
        this.input_notes = input_notes;
    }

    public long getNotes_date() {
        return notes_date;
    }

    public void setNotes_date(long notes_date) {
        this.notes_date = notes_date;
    }
}
