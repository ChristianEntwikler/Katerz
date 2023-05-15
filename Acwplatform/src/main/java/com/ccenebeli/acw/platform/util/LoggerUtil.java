/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.util;

import com.ccenebeli.acw.platform.dto.ResponseDto;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cendstudios
 */
public class LoggerUtil {
    
    public void LogDisplay(String req, Level logType)
    {
        System.out.println(req);
        Logger.getLogger(LoggerUtil.class.getName()).log(logType, (String)null, req);
    }
    
//    public static void main(String[] args) {
//        
//        ResponseDto res =new PayPalUtil().getToken();
//        System.out.println(res.getResponseMessage());
//        
////        ResponseDto respOrderCreate = new PayPalUtil().CreateOrderService(new ArrayList<CartLogDao>(),
////         "IMMEDIATE_PAYMENT_REQUIRED", "PAYPAL", "EXAMPLE INC", 
////        "en-GB", "LOGIN", "SET_PROVIDED_ADDRESS", "PAY_NOW", 
////        "https://example.com/returnUrl", "https://example.com/cancelUrl");
////        
////        System.out.println(respOrderCreate.getResponseMessage());
////        TransactionLogDto tdfigbn=new TransactionLogDto();
////        System.out.println(toJson(tdfigbn));
////        
////        ResponseDto respCapture = new PayPalUtil().OrderCaptureService("20M76439NF479715L");
////        System.out.println(respCapture.getResponseMessage());
//        
//	}
}
