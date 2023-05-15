/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.controllers;

import com.ccenebeli.acw.platform.model.CartLogDao;
import com.ccenebeli.acw.platform.model.ItemDao;
import com.ccenebeli.acw.platform.repository.CartRepository;
import com.ccenebeli.acw.platform.repository.CustomerRepository;
import com.ccenebeli.acw.platform.repository.ItemRepository;
import com.ccenebeli.acw.platform.util.Utils;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Cendstudios
 */
@Scope(value = "session")
@Component(value = "cartController")
@ELBeanName(value = "cartController")
public class CartController {
    @Autowired private CartRepository cartRepo;
    @Autowired private CustomerRepository custRepo;
    @Autowired private ItemRepository itemRepo;
    
    private ItemDao itemReq = new ItemDao();
    private CartLogDao crt = new CartLogDao();
    private List<CartLogDao> listCrt = new ArrayList<CartLogDao>();
    

    public ItemDao getItemReq() {
        return itemReq;
    }

    public void setItemReq(ItemDao itemReq) {
        this.itemReq = itemReq;
    }

    public CartLogDao getCrt() {
        return crt;
    }

    public void setCrt(CartLogDao crt) {
        this.crt = crt;
    }

    public List<CartLogDao> getListCrt() {
        return listCrt;
    }

    public void setListCrt(List<CartLogDao> listCrt) {
        this.listCrt = listCrt;
    }


    @PostConstruct
    public void init() {
      crt = new CartLogDao();
      listCrt = new ArrayList<CartLogDao>();

      try
      {
      String deviceId = new Utils().getMacAddress();
      listCrt =cartRepo.findAll().stream().filter(p->p.getSessionId().equalsIgnoreCase(deviceId)).collect(Collectors.toList());
      }
      catch(Exception ex)
      {
          ex.printStackTrace();
      }
    }
    
    public float subTotal()
    {
        float resp = 0;
        String deviceId = new Utils().getMacAddress();
        List<CartLogDao> listCrt1 =cartRepo.findAll().stream().filter(p->p.getSessionId().equalsIgnoreCase(deviceId) && p.getStatus()==null).collect(Collectors.toList());
        for(CartLogDao ct : listCrt1)
        {
            resp = resp + (Float.parseFloat(ct.getUnitPrice()) * Float.parseFloat(ct.getQuantity()));
        }
        
        return resp;
    }
    
    public float grandTotal()
    {
        return subTotal();
    }
    
    public void addCartItem(ItemDao req)
    {
        this.itemReq = req;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg').show();");
    }
    
    public void submitCartItem(ItemDao req)
    {
        ItemDao reqi =itemRepo.findAll().stream().filter(p->p.getId()==req.getId()).findFirst().get();
        String qty = reqi.getQuantity();
        if(Long.parseLong(req.getQuantity()) > 0 && Long.parseLong(req.getQuantity()) <= Long.parseLong(qty))
        {
        String deviceId = new Utils().getMacAddress();
        CartLogDao crtVal = new CartLogDao();
        crtVal.setDateCreated(new Timestamp(System.currentTimeMillis()));
        crtVal.setItemId(String.valueOf(req.getId()));
        crtVal.setQuantity(req.getQuantity());
        crtVal.setSessionId(deviceId);
        crtVal.setTotalAmount(String.valueOf(Integer.parseInt(req.getQuantity()) * Float.parseFloat(req.getUnitPrice())));
        crtVal.setUnitPrice(req.getUnitPrice());
        cartRepo.save(crtVal);
        reqi.setQuantity(String.valueOf(Integer.parseInt(reqi.getQuantity()) - Integer.parseInt(req.getQuantity())));
        itemRepo.save(reqi);
        new Utils().DisplayInfo("Successfully added to cart");
        }
        else
        {
            new Utils().DisplayError("Quantity value invalid");
        }
    }
    
    public List<CartLogDao> listCartItems()
    {
       List<CartLogDao> listCrt1 = new ArrayList<CartLogDao>();
      try
      {
      String deviceId = new Utils().getMacAddress();
      listCrt1 =cartRepo.findAll().stream().filter(p->p.getSessionId().equalsIgnoreCase(deviceId) && p.getStatus()==null).collect(Collectors.toList());
      
              }
      catch(Exception ex)
      {
          ex.printStackTrace();
      }
      
      return listCrt1;
    }
    
    public void removeItem(CartLogDao req)
    {
        ItemDao reqi =itemRepo.findAll().stream().filter(p->p.getId()==Long.parseLong(req.getItemId())).findFirst().get();
        reqi.setQuantity(String.valueOf(Integer.parseInt(reqi.getQuantity()) + Integer.parseInt(req.getQuantity())));
        itemRepo.save(reqi);
        cartRepo.delete(req);

        new Utils().DisplayInfo("Successfully removed from cart");
        
    }
    
    
}
