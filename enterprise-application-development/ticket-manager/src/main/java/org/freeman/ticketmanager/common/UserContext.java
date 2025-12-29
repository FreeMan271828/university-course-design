package org.freeman.ticketmanager.common;
import lombok.Data;
import java.util.List;

public class UserContext {
    private static final ThreadLocal<UserDetail> threadLocal = new ThreadLocal<>();

    public static void set(UserDetail user) { threadLocal.set(user); }
    public static UserDetail get() { return threadLocal.get(); }
    public static void remove() { threadLocal.remove(); }

    public static Long getUserId() {
        return get() != null ? get().getUserId() : null;
    }
    public static boolean isInternalUser() {
        return get() != null && get().isInternal();
    }
}