/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.util;

import com.ccenebeli.acw.platform.controllers.CartController;
import com.ccenebeli.acw.platform.controllers.ItemController;
import com.ccenebeli.acw.platform.dto.ResponseCodeDto;
import com.ccenebeli.acw.platform.dto.ResponseDto;
import com.ccenebeli.acw.platform.model.CartLogDao;
import com.ccenebeli.acw.platform.model.ItemDao;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Cendstudios
 */
public class PayPalUtil {
    private static SSLSocketFactory sslSocketFactory = null;

    public ResponseDto getToken()
    {
         ResponseDto resp = new ResponseDto();
        try {    
            URL uri = new URL(ConfigProvider.getStrings("pay.base.url") + ConfigProvider.getStrings("pay.gen.token"));        
            
            String reqString1 = "grant_type=client_credentials&ignoreCache&return_client_metadata=true&return_unconsented_scopes=true";
            byte[] reqString = reqString1.getBytes( StandardCharsets.UTF_8 );
            
            String AUTHORIZATION = "Basic " + Base64.getEncoder().encodeToString((ConfigProvider.getStrings("client.intel") + ":" + ConfigProvider.getStrings("sec.token")).getBytes());
        
            HttpURLConnection con = (HttpURLConnection) uri.openConnection();
//        HttpsURLConnection con = (HttpsURLConnection) uri.openConnection();
//        setAcceptAllVerifier((HttpsURLConnection)con);
        con.setDoOutput(true);
        con.setInstanceFollowRedirects(false);
        con.setRequestMethod("POST");   
        con.setRequestProperty("authenticated", "true");
        con.setRequestProperty("Authorization", AUTHORIZATION);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            con.setHostnameVerifier(new HostnameVerifier()
//            {
//                public boolean verify(String hostname, SSLSession session)
//                {
//                    return true;
//                }
//            });
        con.connect();

        DataOutputStream  writer = new DataOutputStream (con.getOutputStream());
            writer.write(reqString);
            writer.flush();
            writer.close();
        
            // reading the response
            InputStreamReader reader = new InputStreamReader(con.getInputStream());
            StringBuilder buf = new StringBuilder();
            char[] cbuf = new char[ 2048 ];
            int num;
            while ( -1 != (num=reader.read( cbuf )))
            {
                buf.append( cbuf, 0, num );
            }
            String result = buf.toString();
            
            int responseCode = con.getResponseCode();

            result = result.replace("null", "\"\"");
//            System.out.println(result);
             
          if(result!=null && !result.isEmpty())
          {
              resp.setResponseCode(ResponseCodeDto.SUCCESSFUL);
              resp.setResponseMessage(result);
          }
          
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(PayPalUtil.class.getName()).log(Level.SEVERE, (String)null, e);
        }
        
        return resp;
    }
    
    public ResponseDto CreateOrderService(List<CartLogDao> reqs, List<ItemDao> itemList, String payMethodPref, String payMethodSelect, String brandName, String locale, String landingPage, String shippingPref, String userAction, String returnUrl, String cancelUrl)
    {
         ResponseDto resp = new ResponseDto();
        try { 
//            JSONObject reqUnitAmt = new JSONObject();
//            reqUnitAmt.put("currency_code", ConfigProvider.getStrings("pay.curr"));
//            reqUnitAmt.put("value", amount);
            
                float respTotal = 0;
                for(CartLogDao ct : reqs)
                {
                    respTotal = respTotal + (Float.parseFloat(ct.getUnitPrice()) * Float.parseFloat(ct.getQuantity()));
                }
            JSONArray reqItems = new JSONArray();
            for(CartLogDao itm : reqs)
            {
                JSONObject reqItemUnit = new JSONObject();
                reqItemUnit.put("currency_code", ConfigProvider.getStrings("pay.curr"));
                reqItemUnit.put("value", itm.getUnitPrice());
                JSONObject reqItem = new JSONObject();
                reqItem.put("name", itemList.stream().filter(p->p.getId()==Long.parseLong(itm.getItemId())).findFirst().get().getName());
                reqItem.put("description", itemList.stream().filter(p->p.getId()==Long.parseLong(itm.getItemId())).findFirst().get().getDescription());
                reqItem.put("quantity", itm.getQuantity());
                reqItem.put("unit_amount", reqItemUnit);
                reqItems.put(reqItem);
            }
            
            JSONObject reqItemTotal = new JSONObject();
                reqItemTotal.put("currency_code", ConfigProvider.getStrings("pay.curr"));
                reqItemTotal.put("value", String.valueOf(respTotal));
                
            JSONObject reqBreakDown = new JSONObject();
            reqBreakDown.put("item_total", reqItemTotal);
            
            JSONObject reqAmtTotal = new JSONObject();
            reqAmtTotal.put("currency_code", ConfigProvider.getStrings("pay.curr"));
            reqAmtTotal.put("value", String.valueOf(respTotal));
            reqAmtTotal.put("breakdown", reqBreakDown);
            
            JSONObject reqPurchaseUnit = new JSONObject();
            reqPurchaseUnit.put("items", reqItems);
            reqPurchaseUnit.put("amount", reqAmtTotal);
            
            JSONArray reqPurchaseUnitaArr = new JSONArray();
            reqPurchaseUnitaArr.put(reqPurchaseUnit);
            
            JSONObject expContext = new JSONObject();
            expContext.put("return_url", returnUrl);
            expContext.put("cancel_url", cancelUrl);
            
            JSONObject req = new JSONObject();
            req.put("intent", ConfigProvider.getStrings("pay.intent"));
            req.put("purchase_units", reqPurchaseUnitaArr);
            req.put("application_context", expContext);

            String reqString = String.valueOf(req).replace("\\", "");
            System.out.println("reqString: " + reqString.replace("\\", ""));
            
            URL uri = new URL(ConfigProvider.getStrings("pay.base.url") + ConfigProvider.getStrings("pay.order.create"));        
                      
            ResponseDto respToken = getToken();
            String bearerIntel = "";
            String tokenIntel = "";
            if(respToken != null && !respToken.getResponseMessage().isEmpty())
            {
                try
                {
            JSONObject tokenResp = new JSONObject(respToken.getResponseMessage());
           bearerIntel = tokenResp.get("token_type").toString();
            tokenIntel =tokenResp.get("access_token").toString();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        
            tokenIntel = bearerIntel + " " + tokenIntel;
            
        HttpURLConnection con = (HttpURLConnection) uri.openConnection();
//        HttpsURLConnection con = (HttpsURLConnection) uri.openConnection();
//        setAcceptAllVerifier((HttpsURLConnection)con);
        con.setDoOutput(true);
        con.setInstanceFollowRedirects(false);
        con.setRequestMethod("POST");   
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("PayPal-Request-Id", new Utils().RandomStringUUID());
        con.setRequestProperty("Prefer", "return=representation");
        con.setRequestProperty("Authorization", tokenIntel);
        
//            con.setHostnameVerifier(new HostnameVerifier()
//            {
//                public boolean verify(String hostname, SSLSession session)
//                {
//                    return true;
//                }
//            });
        con.connect();

        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(reqString);
            writer.flush();
            writer.close();
        
            // reading the response
            InputStreamReader reader = new InputStreamReader(con.getInputStream());
            StringBuilder buf = new StringBuilder();
            char[] cbuf = new char[ 2048 ];
            int num;
            while ( -1 != (num=reader.read( cbuf )))
            {
                buf.append( cbuf, 0, num );
            }
            String result = buf.toString();
            
            int responseCode = con.getResponseCode();
            //System.out.println("responseCode: " + responseCode);
            
            ////System.out.println(result);

            result = result.replace("null", "\"\"");
            System.out.println(result);
              
          if(result!=null && !result.isEmpty())
          {
              if(responseCode ==200)
              {
              resp.setResponseCode(reqString);
              }
              else
              {
               resp.setResponseCode(reqString);
              }
              resp.setResponseMessage(result);
          }
          
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(PayPalUtil.class.getName()).log(Level.SEVERE, (String)null, e);
        }
        
        return resp;
    }
    
    public ResponseDto OrderCaptureService(String reference)
    {
         ResponseDto resp = new ResponseDto();
        try { 
            URL uri = new URL(ConfigProvider.getStrings("pay.base.url") + ConfigProvider.getStrings("pay.order.capture").replace("{id}", reference));        
                      
            ResponseDto respToken = getToken();
            String bearerIntel = "";
            String tokenIntel = "";
            if(respToken != null && !respToken.getResponseMessage().isEmpty())
            {
                try
                {
            JSONObject tokenResp = new JSONObject(respToken.getResponseMessage());
           bearerIntel = tokenResp.get("token_type").toString();
            tokenIntel =tokenResp.get("access_token").toString();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        
            tokenIntel = bearerIntel + " " + tokenIntel;
            
        HttpURLConnection con = (HttpURLConnection) uri.openConnection();
//        HttpsURLConnection con = (HttpsURLConnection) uri.openConnection();
//        setAcceptAllVerifier((HttpsURLConnection)con);
        con.setDoOutput(true);
        con.setInstanceFollowRedirects(false);
        con.setRequestMethod("POST");   
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("PayPal-Request-Id", new Utils().RandomStringUUID());
        con.setRequestProperty("Prefer", "return=representation");
        con.setRequestProperty("Authorization", tokenIntel);        
        con.connect();
        
//            con.setHostnameVerifier(new HostnameVerifier()
//            {
//                public boolean verify(String hostname, SSLSession session)
//                {
//                    return true;
//                }
//            });    
        
        
            InputStream responseStream = con.getResponseCode() / 100 == 2
				? con.getInputStream()
				: con.getErrorStream();
		Scanner s = new Scanner(responseStream).useDelimiter("\\A");
		String result = s.hasNext() ? s.next() : "";
		System.out.println(result);
            int responseCode = con.getResponseCode();
              System.out.println(responseCode);
          if(result!=null && !result.isEmpty())
          {
              if(responseCode ==200)
              {
              resp.setResponseCode(ResponseCodeDto.SUCCESSFUL);
              }
              else
              {
               resp.setResponseCode(ResponseCodeDto.SYSTEM_MALFUNCTION);
              }
              resp.setResponseMessage(result);
          }
          
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(PayPalUtil.class.getName()).log(Level.SEVERE, (String)null, e);
        }
        
        return resp;
    }
    
    protected static void setAcceptAllVerifier(HttpsURLConnection connection) throws NoSuchAlgorithmException, KeyManagementException {
        if( null == sslSocketFactory) {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, ALL_TRUSTING_TRUST_MANAGER, new java.security.SecureRandom());
            sslSocketFactory = sc.getSocketFactory();
        }
        connection.setSSLSocketFactory(sslSocketFactory);
        connection.setHostnameVerifier(ALL_TRUSTING_HOSTNAME_VERIFIER);
    }

    private static final TrustManager[] ALL_TRUSTING_TRUST_MANAGER = new TrustManager[] {
        new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        }
    };

    private static final HostnameVerifier ALL_TRUSTING_HOSTNAME_VERIFIER  = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
