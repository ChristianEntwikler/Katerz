/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.controllers;

import com.ccenebeli.acw.platform.model.TransactionLogDao;
import com.ccenebeli.acw.platform.repository.TransactionLogRepository;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
//import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 *
 * @author Cendstudios
 */
@Scope(value = "session")
@Component(value = "transactionController")
@ELBeanName(value = "transactionController")
@Join(path = "/", to = "/index.xhtml")
public class TransactionController {
    
    @Autowired private TransactionLogRepository transactionRepo;
    
    private TransactionLogDao tns = new TransactionLogDao();
    private TransactionLogDao tnsw = new TransactionLogDao();
    private List<TransactionLogDao> listTns = new ArrayList<TransactionLogDao>();

    private String searchData;

    public TransactionLogDao getTns() {
        return tns;
    }

    public void setTns(TransactionLogDao tns) {
        this.tns = tns;
    }

    public TransactionLogDao getTnsw() {
        return tnsw;
    }

    public void setTnsw(TransactionLogDao tnsw) {
        this.tnsw = tnsw;
    }

    public List<TransactionLogDao> getListTns() {
        return listTns;
    }

    public void setListTns(List<TransactionLogDao> listTns) {
        this.listTns = listTns;
    }

    public String getSearchData() {
        return searchData;
    }

    public void setSearchData(String searchData) {
        this.searchData = searchData;
    }

    
    
    @PostConstruct
    public void init() {
      tns = new TransactionLogDao();
      listTns = new ArrayList<TransactionLogDao>();
    }
        
//    public void doTransaction(String ttype, TransactionLogDao tnsd) {
//            System.out.println("dshcxbkjbnumber" + tnsd.getAmount());
//        try
//        { 
//        if(tnsd.getAccountNumber()==null || tnsd.getAccountNumber().isEmpty() || validateAccount(tnsd.getAccountNumber())==false)
//        {
//            new Util().DisplayError("Valid Account Number required");
//        }
//        else if(tnsd.getAmount()<=0)
//        {
//            System.out.println("dshcxbkjbamt");
//            new Util().DisplayError("Valid Amount required");
//        }
//        else
//        {
//        System.out.println("dshcxbkjb");
//        tnsd.setReference(new Util().generateReference("TR"));
//        tnsd.setTransactionType(ttype);
//                
//        tnsd.setCreatedAt(new Timestamp(System.currentTimeMillis()));
//        transactionRepo.save(tnsd);
//        tns = new TransactionLogDao();
//        tnsw = new TransactionLogDao();
//        }
//        }
//        catch(Exception ex)
//        {
//            ex.printStackTrace();
//            new Util().DisplayError("Unable to " + tns.getTransactionType().toLowerCase() + " account");
//        }
//        
//        listTns = listTransactions("");
//        
//    }
//    
//    public void doTransactiond() {
//        
//       doTransaction("CREDIT", tns); 
//    }
//    
//    public void doTransactionw() {
//        
//       doTransaction("DEBIT", tnsw); 
//    }
//    
//    public List<TransactionLogDao> listTransactions(String accountNumber) {
//
//        listTns = new ArrayList<TransactionLogDao>();
//        List<TransactionLogDao> tns =new ArrayList<TransactionLogDao>();
//        try
//        {
//        if(accountNumber!=null && !accountNumber.isEmpty())
//        {
//        tns = transactionRepo.findByAccountNumber(Sort.by(Sort.Direction.DESC, "accountNumber"), accountNumber);
//        }
//        {
//        tns = transactionRepo.findAll(Sort.by(Sort.Direction.DESC, "accountNumber"));
//        }
//        
//        System.out.println("tns.size(): " + tns.size());
//        
//        if(tns.size() > 0)
//        {
//        List<TransactionLogDao> rep = new Util().convertTransactionList(tns);
//        listTns =rep.stream().sorted(Comparator.comparing(TransactionLogDao::getCreatedAt)).collect(Collectors.toList());
//        }
//        
//        }
//        catch(Exception ex)
//        {
//            ex.printStackTrace();
//            new Util().DisplayError("Error loading data");
//        }
//        
//        return listTns;
//        
//    }

    
}
