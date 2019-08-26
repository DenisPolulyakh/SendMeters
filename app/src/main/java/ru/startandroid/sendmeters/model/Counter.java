package ru.startandroid.sendmeters.model;

import android.view.View;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Counter {
    private String room;
    private String description;
    private String number;
    private String meter;
    private String valiable;

}
