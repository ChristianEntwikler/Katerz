package com.ccenebeli.acw.platform;

import com.ccenebeli.acw.platform.model.ItemDao;
import com.ccenebeli.acw.platform.repository.ItemRepository;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PlatformApplication {

    @Value ("${items.default.list}")
     private String items;
     
     @Autowired ItemRepository itemRepo;
    
	public static void main(String[] args) {
		SpringApplication.run(PlatformApplication.class, args);
	}

         @Bean
        ServletRegistrationBean jsfServletRegistration (ServletContext servletContext) {
            servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", Boolean.TRUE.toString());

            ServletRegistrationBean srb = new ServletRegistrationBean();
            srb.setServlet(new FacesServlet());
            srb.setUrlMappings(Arrays.asList("*.xhtml"));
            srb.setLoadOnStartup(1);
            return srb;
        }
        
         @Bean
	CommandLineRunner runner()
	{
                return args ->
		{
            if(itemRepo.findAll().size()<1)
            {
                try
                {
                String[] itemsArr = items.split(",");
                for(int i=0; i<itemsArr.length; i++)
                {
                    String itemUnitArr = itemsArr[i].trim();
                    
                    String[] itemUnit = itemUnitArr.split(";");
                    
                    ItemDao req =new ItemDao();
                    req.setName(itemUnit[0]);
                    req.setDescription(itemUnit[1]);                     
                    req.setQuantity(itemUnit[2]);
                    req.setUnitPrice(itemUnit[3]);
                    req.setDateCreated(new Timestamp(System.currentTimeMillis()));
                    itemRepo.save(req);
                }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                    Logger.getLogger(PlatformApplication.class.getName()).log(Level.SEVERE, (String)null, ex);
                }
            }			
		};
	}
}
