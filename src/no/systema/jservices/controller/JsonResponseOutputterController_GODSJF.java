package no.systema.jservices.controller;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import no.systema.jservices.common.dao.GodsjfDao;
import no.systema.jservices.entities.JsonGenericContainerDao;



/**
 * 
 * @author oscardelatorre
 * @date Jun 2018
 */
@RestController
//@RequestMapping(value="/syjsSYGODSJF.do")
public class JsonResponseOutputterController_GODSJF {
	private static final Logger logger = Logger.getLogger(JsonResponseOutputterController_GODSJF.class.getName());
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(path="/JSONEXAMPLE.do",method = RequestMethod.GET)
	public JsonGenericContainerDao getAll(){
		JsonGenericContainerDao container = new JsonGenericContainerDao();  
		logger.info("at the START...");
		List<GodsjfDao> list = new ArrayList<GodsjfDao>();
		GodsjfDao obj = new GodsjfDao();
		obj.setGogn("111");
		list.add(obj);
		  
		obj = new GodsjfDao();
		obj.setGogn("222");
		list.add(obj);
		  
		container.setList(list);
		return container;
		  
	}
	
	
}
