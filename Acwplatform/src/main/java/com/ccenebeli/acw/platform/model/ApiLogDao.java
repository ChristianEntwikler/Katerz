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
@Table(name="apilog")
public class ApiLogDao {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    long id;
    
    @Column(name="requesttype")
    private String requestType;
    
    @Column(name="requestpayload", columnDefinition="TEXT", length=2048)
    private String requestPayload;
    
    @Column(name="requestdate")
    private Timestamp requestDate;
    
    @Column(name="responsepayload", columnDefinition="TEXT", length=2048)
    private String responsePayload;
    
    @Column(name="responseDate")
    private Timestamp responseDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

    public Timestamp getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Timestamp responseDate) {
        this.responseDate = responseDate;
    }

   
}
