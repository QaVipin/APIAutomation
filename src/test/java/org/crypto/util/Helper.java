package org.crypto.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    private static String CONFIG = "/src/test/resources/config.properties";
    private static Properties prop = new Properties();

    public static File getFilePath(String fileName){
        return new File(System.getProperty("user.dir")+File.separator+fileName);
    }

    public static Properties loadProperties(){
        if(prop.isEmpty()){
            try(InputStream input = new FileInputStream(getFilePath(CONFIG))){
                prop.load(input);
            }catch (IOException ex){
                ex.printStackTrace();
            }

        }
        return  prop;
    }



    public static TemporalUnit getUnit(String unit){
        Pattern pattern = Pattern.compile("^([0-9]+)([a-zA-Z])");
        Matcher m = pattern.matcher(unit);
        if(m.find()){
            if(m.group(2).equals("m")){
                return ChronoUnit.MINUTES;
            }else if(m.group(2).equals("h")){
                return ChronoUnit.HOURS;
            }else if(m.group(2).equals("D")){
                return ChronoUnit.DAYS;
            }else if(m.group(2).equals("M")){
                return ChronoUnit.MONTHS;
            }
        }
        return null;
    }

    public static int getTime(String unit){
        Pattern pattern = Pattern.compile("^([0-9]+)([a-zA-Z])");
        Matcher m = pattern.matcher(unit);
        if(m.find()){
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    public static Double max(ArrayList<Double> list){

        return Collections.max(list);
    }

    public static Double min(ArrayList<Double> list){
        return Collections.min(list);
    }

    public static Double sum(ArrayList<Double> list){
        Double acc=0.00000;
        return new BigDecimal(list.stream().reduce(acc,Double::sum))
                .setScale(6,BigDecimal.ROUND_HALF_UP)
                .doubleValue();

    }

}
