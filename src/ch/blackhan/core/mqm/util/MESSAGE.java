package ch.blackhan.core.mqm.util;

public class MESSAGE {

    public class CLIENT
    {
        public static final String LOGIN = "CLIENT|login|%s|%s|%s";
        public static final String LOGOUT = "CLIENT|logout|%s|%s|%s";
        public static final String REFRESH = "CLIENT|refresh|%s|%s|%s";
        public static final String GET_SERVER_TIME = "CLIENT|get_server_time";
    }

    public class PAIR
    {
        public static final String GET_HALTED = "PAIR|get_halted|%s|%s";
    }

    public class RATE_TABLE
    {
        public static final String LOGGED_IN = "RATE_TABLE|logged_in|%s";
        public static final String GET_RATE = "RATE_TABLE|get_rate|%s|%s";
        public static final String GET_HISTORY = "RATE_TABLE|get_history|%s|%s|%s|%s";
    }
}
