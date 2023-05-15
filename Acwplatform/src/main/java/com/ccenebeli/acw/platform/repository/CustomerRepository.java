/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.repository;

import com.ccenebeli.acw.platform.model.CustomerDao;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Cendstudios
 */
public interface CustomerRepository extends JpaRepository<CustomerDao, Long>{
    
}
