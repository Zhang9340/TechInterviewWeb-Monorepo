package com.zzy.techInterviewWeb.constant;

public interface RedisConstant {

    /**
     * Redis Key prefix for user sign-in records
     */
    String USER_SIGN_IN_REDIS_KEY_PREFIX = "user:signins";

    /**
     * Get the Redis Key for user sign-in records
     * @param year The year
     * @param userId The user ID
     * @return The concatenated Redis Key
     */
    static String getUserSignInRedisKey(int year, long userId) {
        return String.format("%s:%s:%s", USER_SIGN_IN_REDIS_KEY_PREFIX, year, userId);
    }

}
