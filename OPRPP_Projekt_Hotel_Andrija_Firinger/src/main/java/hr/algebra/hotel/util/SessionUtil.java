package hr.algebra.hotel.util;

import hr.algebra.hotel.model.User;

public final class SessionUtil {

    private static User loggedInUser;

    private SessionUtil() {}

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static boolean isAdmin() {
        return loggedInUser != null && loggedInUser.isAdmin();
    }

    public static void clear() {
        loggedInUser = null;
    }
}