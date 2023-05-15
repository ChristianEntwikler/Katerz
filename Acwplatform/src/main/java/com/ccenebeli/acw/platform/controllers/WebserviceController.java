/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.controllers;

import com.ccenebeli.acw.platform.dto.ResponseCodeDto;
import com.ccenebeli.acw.platform.dto.ResponseDto;
import com.ccenebeli.acw.platform.dto.TransactionLogDto;
import com.ccenebeli.acw.platform.model.ApiLogDao;
import com.ccenebeli.acw.platform.model.CartLogDao;
import com.ccenebeli.acw.platform.model.CustomerDao;
import com.ccenebeli.acw.platform.model.ItemDao;
import com.ccenebeli.acw.platform.model.OrderLogDao;
import com.ccenebeli.acw.platform.model.TransactionLogDao;
import com.ccenebeli.acw.platform.repository.ApiLogRepository;
import com.ccenebeli.acw.platform.repository.CartRepository;
import com.ccenebeli.acw.platform.repository.CustomerRepository;
import com.ccenebeli.acw.platform.repository.ItemRepository;
import com.ccenebeli.acw.platform.repository.OrderRepository;
import com.ccenebeli.acw.platform.repository.TransactionLogRepository;
import static com.ccenebeli.acw.platform.util.ConverterUtil.toJson;
import com.ccenebeli.acw.platform.util.LoggerUtil;
import com.ccenebeli.acw.platform.util.PayPalUtil;
import com.ccenebeli.acw.platform.util.Utils;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Cendstudios
 */
@Scope(value = "session")
@Component(value = "webController")
@ELBeanName(value = "webController")
@RestController
@RequestMapping("/api")
public class WebserviceController {
    @Autowired ApiLogRepository ApiRepo;
    @Autowired TransactionLogRepository tnsRepo;
    @Autowired OrderRepository orderRepo;
    @Autowired CartRepository cartRepo;
    @Autowired CustomerRepository custRepo;
    @Autowired ItemRepository itemRepo;
    
    private CustomerDao cst = new CustomerDao();
    private String amountPaid;

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public CustomerDao getCst() {
        return cst;
    }

    public void setCst(CustomerDao cst) {
        this.cst = cst;
    }
    
    @RequestMapping(value ="/payment/send",produces = "application/json",method=RequestMethod.POST)
    public ResponseEntity<String> sendPayment(@RequestHeader HttpHeaders headers)
        {
            String resp = new String();
            try
      {
      String deviceId = new Utils().getMacAddress();
      
                List<CartLogDao> req =cartRepo.findAll().stream().filter(p->p.getSessionId().equalsIgnoreCase(deviceId) && p.getStatus()==null).collect(Collectors.toList());

                Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
                List<ItemDao> itemReq = itemRepo.findAll();
                ResponseDto respOrderCreate = new PayPalUtil().CreateOrderService(req, itemReq,
         "IMMEDIATE_PAYMENT_REQUIRED", "PAYPAL", "EXAMPLE INC", 
        "en-GB", "LOGIN", "SET_PROVIDED_ADDRESS", "PAY_NOW", 
        "https://example.com/returnUrl", "https://example.com/cancelUrl");
                resp = respOrderCreate.getResponseMessage();
                
                try
                {
                    
                ApiLogDao reqVal = new ApiLogDao();
                reqVal.setRequestType("CREATEORDER");               
                reqVal.setRequestPayload(respOrderCreate.getResponseCode());
                reqVal.setRequestDate(timestamp2);
                reqVal.setResponsePayload(respOrderCreate.getResponseMessage());
                reqVal.setResponseDate(new Timestamp(System.currentTimeMillis()));
               ApiRepo.save(reqVal);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                return ResponseEntity.ok().body(respOrderCreate.getResponseMessage());
                
            
            }
            catch(DataIntegrityViolationException dup)
		{
                        new LoggerUtil().LogDisplay("Error: " + dup.getStackTrace().toString(), Level.SEVERE);
			ResponseDto reply = new ResponseDto();
			reply.setResponseCode(ResponseCodeDto.DUPLICATE_RECORD);
			reply.setResponseMessage("Data flagged as duplicate");			
			return new ResponseEntity<String>(reply.getResponseMessage(), HttpStatus.OK);
		}
		catch(Exception e)
		{
			e.printStackTrace();
                        new LoggerUtil().LogDisplay("Error: " + e.getStackTrace().toString(), Level.SEVERE);
			return new ResponseEntity<String>("System failure", HttpStatus.INTERNAL_SERVER_ERROR);
		}
            
        }
    
    @RequestMapping(value ="/payment/{orderid}/capture",produces = "application/json",method=RequestMethod.POST)
        public ResponseEntity<String> capturePayment(@PathVariable("orderid") String orderid, @RequestHeader HttpHeaders headers)
        {
            new LoggerUtil().LogDisplay("request: " + orderid, Level.INFO);
            String resp = new String();
            
            try
            {
                Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
                ResponseDto respCapture = new PayPalUtil().OrderCaptureService(orderid);

                //if(respCapture.getResponseCode().equals(ResponseCodeDto.SUCCESSFUL))
               // {
               try
               {
                    String deviceId = new Utils().getMacAddress();
                    List<CartLogDao> req =cartRepo.findAll().stream().filter(p->p.getSessionId().equalsIgnoreCase(deviceId) && p.getStatus()==null).collect(Collectors.toList());
                    
                    float respTotal = 0;
                    for(CartLogDao ct : req)
                    {
                        respTotal = respTotal + (Float.parseFloat(ct.getUnitPrice()) * Float.parseFloat(ct.getQuantity()));
                    }
                    
                    for(CartLogDao crt : req)
                    {
                        OrderLogDao odl =new OrderLogDao();
                        odl.setCartId(String.valueOf(crt.getId()));
                        odl.setDateCreated(new Timestamp(System.currentTimeMillis()));
                        odl.setOrderId(orderid);
                        odl.setPaymentMode("PAYPAL");
                        odl.setPaymentStatus("COMPLETED");
                        odl.setStatus("COMPLETED");
                        odl.setTotalAmountPaid(String.valueOf(respTotal));
                        orderRepo.save(odl);
                        crt.setStatus("ORDERCOMPLETED");
                        cartRepo.save(crt);
                    }
               }
               catch(Exception ex)
               {
                   ex.printStackTrace();
               }
               // }
                
                try
                {    
                ApiLogDao reqVal = new ApiLogDao();
                reqVal.setRequestType("CAPTUREAPPROVAL");               
                reqVal.setRequestPayload(orderid);
                reqVal.setRequestDate(timestamp2);
                reqVal.setResponsePayload(respCapture.getResponseMessage());
                reqVal.setResponseDate(new Timestamp(System.currentTimeMillis()));
               ApiRepo.save(reqVal);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                    resp =respCapture.getResponseMessage();
                    return ResponseEntity.ok().body(respCapture.getResponseMessage());
            
            }
            catch(DataIntegrityViolationException dup)
		{
                        new LoggerUtil().LogDisplay("Error: " + dup.getStackTrace().toString(), Level.SEVERE);
			ResponseDto reply = new ResponseDto();
			reply.setResponseCode(ResponseCodeDto.DUPLICATE_RECORD);
			reply.setResponseMessage("Data flagged as duplicate");			
			return new ResponseEntity<String>(reply.getResponseMessage(), HttpStatus.OK);
		}
		catch(Exception e)
		{
			e.printStackTrace();
                        new LoggerUtil().LogDisplay("Error: " + e.getStackTrace().toString(), Level.SEVERE);

			return new ResponseEntity<String>("System failure", HttpStatus.INTERNAL_SERVER_ERROR);
		}
            
        }
       
    @RequestMapping(value ="/payment/list",produces = "application/json",method=RequestMethod.GET)
    public ResponseEntity<List<TransactionLogDao>> fetchAllPayments()
        {    
             List<TransactionLogDao> resp = new ArrayList<TransactionLogDao>();
             try
             {
             //FETCH ALL PAYMENTS
             resp = tnsRepo.findAll();
             //FETCH ALL PAYMENTS
             }
		catch(Exception e)
		{
			e.printStackTrace();
                        new LoggerUtil().LogDisplay("Error: " + e.getStackTrace().toString(), Level.SEVERE);
			return new ResponseEntity<List<TransactionLogDao>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
            return ResponseEntity.ok().body(resp);
        } 
    
    @PostConstruct
    public void init() {
      cst = new CustomerDao();
      
      try
      {  
      String deviceId = new Utils().getMacAddress();
      cst = custRepo.findAll().stream().filter(p->p.getDeviceId().equalsIgnoreCase(deviceId)).findFirst().get();
      }
      catch(Exception ex)
      {
          ex.printStackTrace();
      }
    }
    
    public void regCustomer()
    {
        String deviceId = new Utils().getMacAddress();
        if(cst.getFirstName()==null || cst.getFirstName().isEmpty())
        {
            new Utils().DisplayError("First name is required");
        }
        else if(cst.getLastName()==null || cst.getLastName().isEmpty())
        {
             new Utils().DisplayError("Last name is required");
        }
        else if(cst.getEmailAddress()==null || cst.getEmailAddress().isEmpty())
        {
             new Utils().DisplayError("Email address is required");
        }
        else if(cst.getAddress()==null || cst.getAddress().isEmpty())
        {
             new Utils().DisplayError("Address is required");
        }
        else if(!cartRepo.findAll().stream().filter(p->p.getSessionId().equalsIgnoreCase(deviceId) && p.getStatus()==null).findAny().isPresent())
        {
            new Utils().DisplayError("Cart is empty. Please add item(s) to cart before checkout");
        }
        else
        {
              try
      {
     
      if(custRepo.findAll().stream().filter(p->p.getDeviceId().equalsIgnoreCase(deviceId) || (!cst.getEmailAddress().isEmpty() && p.getEmailAddress().equalsIgnoreCase(cst.getEmailAddress()))).findAny().isPresent())
      { }
      else
      {
          cst.setDeviceId(deviceId);
          cst.setDateCreated(new Timestamp(System.currentTimeMillis()));
          custRepo.save(cst);
      }
                    
                     try{
                    FacesContext fContext = FacesContext.getCurrentInstance();
                   ExternalContext extContext = fContext.getExternalContext();
                   String pageholder=extContext.getRequestContextPath() + "/" + "checkout.xhtml";  
                   System.out.println("pageholder: " + pageholder);
                   Logger.getLogger(WebserviceController.class.getName()).log(Level.SEVERE, (String)null, "pageholder: " + pageholder);
                   extContext.redirect(pageholder);}
                   catch(Exception ex)
                   {
                       ex.printStackTrace();
                   }
      }
      catch(Exception ex)
      {
          ex.printStackTrace();
    }
      
        }
    }
    
    public void cashPayment()
    {
         String deviceId = new Utils().getMacAddress();
    
              try
      {
        
                    List<CartLogDao> req =cartRepo.findAll().stream().filter(p->p.getSessionId().equalsIgnoreCase(deviceId) && p.getStatus()==null).collect(Collectors.toList());
                    System.out.println("req.size(): " + req.size());
                    
                float respTotal = 0;
                for(CartLogDao ct : req)
                {
                    respTotal = respTotal + (Float.parseFloat(ct.getUnitPrice()) * Float.parseFloat(ct.getQuantity()));
                }
                    
                    for(CartLogDao crt : req)
                    {
                        OrderLogDao odl =new OrderLogDao();
                        odl.setCartId(String.valueOf(crt.getId()));
                        odl.setDateCreated(new Timestamp(System.currentTimeMillis()));
                        odl.setOrderId(new Utils().generateId("CSH"));
                        odl.setPaymentMode("CASH");
                        odl.setPaymentStatus("COMPLETED");
                        odl.setStatus("COMPLETED");
                        odl.setTotalAmountPaid(String.valueOf(respTotal));
                        orderRepo.save(odl);
                        crt.setStatus("ORDERCOMPLETED");
                        cartRepo.save(crt);
                    }
                    
                    try{
                    FacesContext fContext = FacesContext.getCurrentInstance();
                   ExternalContext extContext = fContext.getExternalContext();
                   String pageholder=extContext.getRequestContextPath() + "/" + "order.xhtml";  
                   System.out.println("pageholder: " + pageholder);
                   Logger.getLogger(WebserviceController.class.getName()).log(Level.SEVERE, (String)null, "pageholder: " + pageholder);
                   extContext.redirect(pageholder);}
                   catch(Exception ex)
                   {
                       ex.printStackTrace();
                   }
      }
      catch(Exception ex)
      {
          ex.printStackTrace();
    }
              
    }
    
}
