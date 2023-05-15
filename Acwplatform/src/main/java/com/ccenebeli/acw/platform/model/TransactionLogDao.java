/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Cendstudios
 */
@Entity
@Table(name="transactionlog")
public class TransactionLogDao {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    long id;
    
    @Column(name="itemid")
    private String itemid;
    
    @Column(name="customername")
    private String customerName;
    
    @Column(name="quantitypurchased")
    private String quantityPurchased;
    
    @Column(name="unitprice")
    private String unitPrice;
    
    @Column(name="totalprice")
    private String totalPrice;
    
    @Column(name="datecreated")
    private Timestamp dateCreated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getQuantityPurchased() {
        return quantityPurchased;
    }

    public void setQuantityPurchased(String quantityPurchased) {
        this.quantityPurchased = quantityPurchased;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }
}
