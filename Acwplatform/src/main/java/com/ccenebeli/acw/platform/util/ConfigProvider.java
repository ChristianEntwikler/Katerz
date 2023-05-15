/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.util;

import java.util.ResourceBundle;

/**
 *
 * @author Cendstudios
 */
public class ConfigProvider {
    private static ResourceBundle configBundle = ResourceBundle.getBundle("application");

    public static void initialize() {
        configBundle = ResourceBundle.getBundle("application");
    }
    
    public String getString(String key)
    {
        return configBundle.getString(key);
    }
    
    public static String getStrings(String key)
    {
        return configBundle.getString(key);
    }
}
