package ru.practicum.ewm.constant;

import java.time.format.DateTimeFormatter;

public class Constant {
    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final Short HOURS_BEFORE_EVENT = 2;
    public static final Short HOUR_BEFORE_EVENT = 1;
    public static final String START = "2000-01-01 00:00:00";
    public static final String END = "2100-01-01 00:00:00";
    public static final String URI = "/events/";
    public static final String UNIQUE = "true";
    public static final String APPLICATION_NAME = "ewm-main-service";
    public static final String INCORRECT_TIME_MSG = "Incorrect time input";
    public static final String INCORRECT_TIME_REASON = "The time of event must be at least in 2 hours before published";
    public static final String INCORRECT_STATE_MSG = "Incorrect state for updating";
    public static final String INCORRECT_STATE_REASON = "The event can't be published when updating";
    public static final String INCORRECT_EVENT_STATE_MSG = "Impossible to public";
    public static final String INCORRECT_EVENT_STATE_REASON = "Event is published or canceled";
    public static final String INCORRECT_EVENT_ANNOTATION = "Event annotation is incorrect";
    public static final String INCORRECT_EVENT_DECSRIPTION = "Event discription is incorrect";
    public static final String INCORRECT_REQUEST_STATUS_MSG = "The request must have the status pending";
    public static final String INCORRECT_REQUEST_STATUS_REASON = "Incorrect id of request for modification";
    public static final String INCORRECT_REQUEST_MSG = "The request can't be created";
    public static final String INCORRECT_REQUEST_UPDATE_MSG = "The request can't be modified";
    public static final String INCORRECT_REQUEST_REASON = "Such request already exist";
    public static final String INCORRECT_REQUEST_EVENT_LIMIT_REASON = "Event already reached the limit";
    public static final String INCORRECT_REQUESTER_REASON = "Requester can't be initiator of event";
    public static final String INCORRECT_REQUEST_EVENT_STATE_REASON = "Event is not published";
    public static final String INCORRECT_DATA_INPUT_MSG = "Incorrect data input";
    public static final String INCORRECT_NAME_UNIQUE_REASON = "Field name must be unique";
    public static final String INCORRECT_CATEGORY_REL_REASON = "Category related to events";
    public static final String NOT_FOUND_ID_REASON = "Incorrect Id";
    public static final String NOT_FOUND_EVENT_MSG = "Event not found";
    public static final String NOT_FOUND_USER_MSG = "User not found";
    public static final String NOT_FOUND_REQUEST_MSG = "Request not found";
    public static final String NOT_FOUND_CATEGORY_MSG = "Category not found";
    public static final String NOT_FOUND_COMPILATION_MSG = "Compilation not found";
}