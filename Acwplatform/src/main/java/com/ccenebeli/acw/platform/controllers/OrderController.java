/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.controllers;

import com.ccenebeli.acw.platform.dto.OrderResponseDto;
import com.ccenebeli.acw.platform.model.CartLogDao;
import com.ccenebeli.acw.platform.model.ItemDao;
import com.ccenebeli.acw.platform.model.OrderLogDao;
import com.ccenebeli.acw.platform.repository.CartRepository;
import com.ccenebeli.acw.platform.repository.CustomerRepository;
import com.ccenebeli.acw.platform.repository.ItemRepository;
import com.ccenebeli.acw.platform.repository.OrderRepository;
import com.ccenebeli.acw.platform.util.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Cendstudios
 */
@Scope(value = "session")
@Component(value = "orderController")
@ELBeanName(value = "orderController")
public class OrderController {
    @Autowired private OrderRepository orderRepo;
    @Autowired private CartRepository cartRepo;
    @Autowired private CustomerRepository custRepo;
    @Autowired private ItemRepository itemRepo;
    
    public List<OrderResponseDto> listOrderItems()
    {
       List<OrderResponseDto> listOrder = new ArrayList<OrderResponseDto>();
      try
      {
      String deviceId = new Utils().getMacAddress();
     List<CartLogDao> listCrt =cartRepo.findAll().stream().filter(p->(p.getStatus()!=null && p.getStatus().equalsIgnoreCase("ORDERCOMPLETED")) && p.getSessionId().equalsIgnoreCase(deviceId)).collect(Collectors.toList());
     System.out.println("listCrt: " + listCrt.size());
     for(CartLogDao crt : listCrt)
     {   
         OrderResponseDto resp = new OrderResponseDto();
         OrderLogDao orderItem =orderRepo.findAll().stream().filter(p->p.getCartId().equalsIgnoreCase(String.valueOf(crt.getId()))).findFirst().get();
         ItemDao itm = itemRepo.findAll().stream().filter(p->p.getId()==Long.parseLong(crt.getItemId())).findFirst().get();
         
         resp.setCartId(orderItem.getCartId());
         resp.setDateCreated(orderItem.getDateCreated());
         resp.setDescription(itm.getDescription());
         resp.setId(orderItem.getId());
         resp.setName(itm.getName());
         resp.setOrderId(orderItem.getOrderId());
         resp.setPaymentMode(orderItem.getPaymentMode());
         resp.setPaymentStatus(orderItem.getPaymentStatus());
         resp.setQuantity(crt.getQuantity());
         resp.setStatus(orderItem.getStatus());
         resp.setTotalAmountPaid(crt.getTotalAmount());
         resp.setUnitPrice(crt.getUnitPrice());
         listOrder.add(resp);
     }
      
              }
      catch(Exception ex)
      {
          ex.printStackTrace();
      }
      
      return listOrder;
    }
}
