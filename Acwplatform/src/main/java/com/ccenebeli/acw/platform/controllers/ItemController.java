/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.controllers;

import com.ccenebeli.acw.platform.model.ItemDao;
import com.ccenebeli.acw.platform.repository.ItemRepository;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Cendstudios
 */
@Scope(value = "session")
@Component(value = "itemController")
@ELBeanName(value = "itemController")
public class ItemController {
    @Autowired private ItemRepository itemRepo;
    
    private ItemDao itm = new ItemDao();
    private List<ItemDao> listItm = new ArrayList<ItemDao>();

    public ItemDao getItm() {
        return itm;
    }

    public void setItm(ItemDao itm) {
        this.itm = itm;
    }

    public List<ItemDao> getListItm() {
        return listItm;
    }

    public void setListItm(List<ItemDao> listItm) {
        this.listItm = listItm;
    }

    @PostConstruct
    public void init() {
      itm = new ItemDao();
      listItm = new ArrayList<ItemDao>();
      listItm =itemRepo.findAll();
    }
    
    public List<ItemDao> fetchItems()
    {
      return itemRepo.findAll();
    }
    
    public String fetchItemName(String itemId)
    {
        return itemRepo.findAll().stream().filter(p->p.getId()==Long.parseLong(itemId)).findFirst().get().getName();           
    }
}
