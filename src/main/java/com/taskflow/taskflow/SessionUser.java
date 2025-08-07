package com.taskflow.taskflow;

public class SessionUser {
    private static Long CurrentuserId=null;
    public static void login(Long id){
        CurrentuserId=id;
    }
    public static void logout(){
        CurrentuserId=null;
    }
    public static boolean isLoggedin(){
        return CurrentuserId!=null;
    }
    public static Long getUserId(){
        return CurrentuserId;
    }
    public static Long getCurrentUserId(){
        return CurrentuserId;
    }
}
