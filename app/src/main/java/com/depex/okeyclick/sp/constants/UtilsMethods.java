package com.depex.okeyclick.sp.constants;



public class UtilsMethods {

    public static final int genrateASCIIforHashCode(String str){
        int result=0;
        if(str!=null){
            for(int i =0;i<str.length();i++){
                result=str.charAt(i);
            }
            return result;
        }
        return result;
    }
}