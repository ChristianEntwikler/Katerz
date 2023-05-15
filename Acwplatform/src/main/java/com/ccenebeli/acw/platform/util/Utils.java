/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Cendstudios
 */
public class Utils {
    
    public void DisplayError(String message) {
       FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + message, message));
        }

        public void DisplayInfo(String message) {
               FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info: " + message, message));
        }
    
    public static int getDateDiff(Date startDate, Date endDate, int mode) {
        LocalDateTime start = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
        switch (mode) {

            case GregorianCalendar.MINUTE:

                return (int) Duration.between(start, end).toMinutes();

            //return Minutes.minutesBetween(new DateTime(startDate), new DateTime(endDate)).getMinutes();
            case GregorianCalendar.DAY_OF_YEAR:
                //return Days.daysBetween(new DateTime(startDate), new DateTime(endDate)).getDays();
                return (int) Duration.between(start, end).toDays();
            case GregorianCalendar.DAY_OF_MONTH:
                //return Days.daysBetween(new DateTime(startDate), new DateTime(endDate)).getDays();
                return (int) Duration.between(start, end).toDays();
            case GregorianCalendar.MONTH:
                //return Months.monthsBetween(new DateTime(startDate), new DateTime(endDate)).getMonths();
                return (int) Period.between(start.toLocalDate(), end.toLocalDate()).getMonths();
            case GregorianCalendar.YEAR:
                //return Years.yearsBetween(new DateTime(startDate), new DateTime(endDate)).getYears();
                return (int) Period.between(start.toLocalDate(), end.toLocalDate()).getYears();
            default:
                return 0;
        }
    }
    
    public static String leftStr(String value, int length) {
        // To get rightStr characters from a string, change the begin index.
        if (length > value.length()) {
            length = value.length();
        }
        return value.substring(0, length);
    }
    
    public static String hash(String plainText, String signMeth) {
        StringBuffer hexString = new StringBuffer();
        if (signMeth.equals("SHA512")) {
            signMeth = "SHA-512";
        }
        try {
            MessageDigest md = MessageDigest.getInstance(signMeth);
            md.update(plainText.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            System.out.println("Hex format : " + sb.toString());

            //convert the byte to hex format method 2
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            return hexString.toString().toUpperCase();
        }
    }
    
    public String generateId(String reqCode)
    {
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyMMddHHmmss");
                Timestamp timestamp3 = new Timestamp(System.currentTimeMillis());
                String timestampz3=sdf3.format(timestamp3);               
                //long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
                long number = (long) Math.floor(Math.random() * 9_00_00L) + 1_00_0L;
             String fcinum=reqCode + timestampz3 + number;
             
             return fcinum;
    }
    
    public static String RandomStringUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    
    public String getMacAddress(){
    String macAddress = null;
    try
    {
    String command = "ifconfig";

    String osName = System.getProperty("os.name");
    System.out.println("Operating System is " + osName);

    if (osName.startsWith("Windows")) {
        command = "ipconfig /all";
    } else if (osName.startsWith("Linux") || osName.startsWith("Mac") || osName.startsWith("HP-UX")
            || osName.startsWith("NeXTStep") || osName.startsWith("Solaris") || osName.startsWith("SunOS")
            || osName.startsWith("FreeBSD") || osName.startsWith("NetBSD")) {
        command = "ifconfig -a";
    } else if (osName.startsWith("OpenBSD")) {
        command = "netstat -in";
    } else if (osName.startsWith("IRIX") || osName.startsWith("AIX") || osName.startsWith("Tru64")) {
        command = "netstat -ia";
    } else if (osName.startsWith("Caldera") || osName.startsWith("UnixWare") || osName.startsWith("OpenUNIX")) {
        command = "ndstat";
    } else {// Note: Unsupported system.
        throw new Exception("The current operating system '" + osName + "' is not supported.");
    }

    Process pid = Runtime.getRuntime().exec(command);
    BufferedReader in = new BufferedReader(new InputStreamReader(pid.getInputStream()));
    Pattern p = Pattern.compile("([\\w]{1,2}(-|:)){5}[\\w]{1,2}");
    while (true) {
        String line = in.readLine();
        System.out.println("line " + line);
        if (line == null)
            break;

        Matcher m = p.matcher(line);
        if (m.find()) {
            macAddress = m.group();
            break;
        }
    }
    in.close();
    }
    catch(Exception ex)
    {
        ex.printStackTrace();
    }
    return macAddress;
}
}
