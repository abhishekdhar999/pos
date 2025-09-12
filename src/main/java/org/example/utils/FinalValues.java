package org.example.utils;


import java.time.ZonedDateTime;

public class FinalValues {
    public static final Integer MAXIMUM_LENGTH = 50;
    public static final String START_DATE = ZonedDateTime.now().minusDays(1).toString();
    public static final Integer FINAL_PRICE = 5000000;
    public static final Integer FINAL_INVENTORY = 100000;
    public static final Integer URL_LENGTH = 2048;
}