package com.designpattern.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    // Action Types
    public final String ACTION_LOGIN = "LOGIN";
    public final String ACTION_LOGOUT = "LOGOUT";
    public final String ACTION_CREATE = "CREATE";
    public final String ACTION_UPDATE = "UPDATE";
    public final String ACTION_DELETE = "DELETE";
    public final String ACTION_VIEW = "VIEW";

    // Messages
    public final String LOG_SUCCESS = "Audit log recorded successfully.";
    public final String LOGS_CLEARED = "All audit logs cleared successfully.";
    public final String INVALID_ACTION = "Invalid action type: %s. Supported: LOGIN, LOGOUT, CREATE, UPDATE, DELETE, VIEW";

}