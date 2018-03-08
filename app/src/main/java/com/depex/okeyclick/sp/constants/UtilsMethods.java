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

    public static String getRatingText(float rate){
        String rateText="Excellent";
        if(rate<=1){
            rateText="Poor";
        }
        else if(rate>1 && rate<=2){
            rateText="Fair";
        }
        else if(rate>2 && rate<=3){
            rateText="Average";
        }
        else if(rate>3 && rate<=4){
            rateText="Good";
        }
        else if (rate>4 && rate<=5){
            rateText="Excellent";
        }
        return rateText;
    }

}