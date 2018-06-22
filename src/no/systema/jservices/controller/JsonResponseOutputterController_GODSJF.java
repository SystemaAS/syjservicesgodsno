package no.systema.jservices.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.systema.jservices.common.dao.DokufDao;
import no.systema.jservices.common.dao.GodsjfDao;
import no.systema.jservices.entities.JsonGenericContainerDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.GodsjfDaoService;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.util.StringUtils;


/**
 * 
 * @author oscardelatorre
 * @date Jun 2018
 */
@RestController
//@RequestMapping(value="/syjsSYGODSJF.do")
public class JsonResponseOutputterController_GODSJF {
	private static final Logger logger = Logger.getLogger(JsonResponseOutputterController_GODSJF.class.getName());
	
	@Autowired
	private GodsjfDaoService godsjfDaoService;
	@Autowired
	private BridfDaoService bridfDaoService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(path="/JSONEXAMPLE.do",method = RequestMethod.GET)
	public JsonGenericContainerDao getExample(){
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
	
	/**
	 * Db-file: 	GODSJF
	 * 
	 * @Example SELECT list http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSJF.do?user=OSCAR&avd=1&sign=OT
	 * @return
	 */
	@RequestMapping(path="/syjsSYGODSJF.do",method = RequestMethod.GET)
	public JsonGenericContainerDao getSygodsjf(@RequestParam("user") String user, HttpSession session, HttpServletRequest request ){
		JsonGenericContainerDao container = new JsonGenericContainerDao();
		
		String gogn = request.getParameter("gogn");
		List<GodsjfDao> list = new ArrayList<GodsjfDao>();
		
		try {
			logger.info("Inside syjsSYGODSJF.do");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				list = this.fetchRecords(gogn);
				
				if (list != null) {
					container.setUser(user);
					container.setList(list);
				} else {
					errMsg = "ERROR on SELECT: Can not find DokufDao list";
					status = "error";
					logger.info(status + errMsg);
					//sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
					container.setErrMsg(errMsg);
				}

			} else {
				errMsg = "ERROR on SELECT";
				status = "error";
				dbErrorStackTrace.append(" request input parameters are invalid: <user> <dfavd> <dfopd>");
				//sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
				container.setErrMsg(errMsg + dbErrorStackTrace.toString());
			}
		} catch (Exception e) {
			logger.info("Error :", e);
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);
			//return "ERROR [JsonResponseOutputterController]" + writer.toString();
			container.setErrMsg("ERROR [JsonResponseOutputterController]" + writer.toString());
			
		}

		session.invalidate();
		return container;

		  
	}
	/**
	 * 
	 * @param gogn (godsnr)
	 * @return
	 */
	private List<GodsjfDao> fetchRecords(String gogn) {
		List<GodsjfDao> list = new ArrayList<GodsjfDao>();
		
		if(StringUtils.hasValue(gogn)){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("gogn", gogn);
			
			//Exact match
			list = godsjfDaoService.findAll(params);
		}else{
			list = godsjfDaoService.findAll(null);
		}
		logger.info(list.size());
		return list;
	}	
	
}
