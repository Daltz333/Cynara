package Handlers;


public class RateLimitHandler {
    private static boolean isRatedLimited = false;
    private static long  systemTime = -1;
    private static int rateLimit = -1;


    public static void setRateLimit(int timeoutInSeconds) {
        systemTime = System.currentTimeMillis();
        rateLimit = timeoutInSeconds;
    }

    public static boolean isRateLimited() {

        if (systemTime == -1 || rateLimit == -1) {
            isRatedLimited = false;
        } else {
            if (systemTime + rateLimit*1000 < System.currentTimeMillis()) {
                isRatedLimited = false;
            } else {
                isRatedLimited = true;
            }
        }

        return isRatedLimited;
    }
}
