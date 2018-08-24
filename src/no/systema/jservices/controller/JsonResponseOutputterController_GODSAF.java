package no.systema.jservices.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import no.systema.jservices.common.dao.GodsafDao;
import no.systema.jservices.entities.JsonGenericContainerDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.GodsafDaoService;
import no.systema.jservices.common.util.StringUtils;


/**
 * 
 * @author oscardelatorre
 * @date Jun 2018
 */
@RestController
//@RequestMapping(value="/syjsSYGODSAF.do")
public class JsonResponseOutputterController_GODSAF {
	private static final Logger logger = Logger.getLogger(JsonResponseOutputterController_GODSAF.class.getName());
	private final Map<String, Object> ALL_RECORDS = null;
	String ORDER_BY = "order by gflavd asc";
	
	@Autowired
	private GodsafDaoService godsafDaoService;
	@Autowired
	private BridfDaoService bridfDaoService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(path="/JSONEXAMPLE_GODSAF.do",method = RequestMethod.GET)
	public JsonGenericContainerDao getExample(){
		JsonGenericContainerDao container = new JsonGenericContainerDao();  
		logger.info("at the START...");
		List<GodsafDao> list = new ArrayList<GodsafDao>();
		GodsafDao obj = new GodsafDao();
		obj.setGflavd("111");
		list.add(obj);
		  
		obj = new GodsafDao();
		obj.setGflavd("222");
		list.add(obj);
		  
		container.setList(list);
		return container;
		  
	}
	
	/**
	 * Db-file: 	GODSAF
	 * 
	 * @Example 
	 * (1) SELECT list http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSAF.do?user=OSCAR
	 * (2) Exact match avd: http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSAF.do?user=OSCAR&gflavd=1
	 * 
	 * @return
	 */
	@RequestMapping(path="/syjsSYGODSAF.do",method = { RequestMethod.GET, RequestMethod.POST } )
	public JsonGenericContainerDao getSygodsjf(@RequestParam("user") String user, HttpSession session, HttpServletRequest request ){
		JsonGenericContainerDao container = new JsonGenericContainerDao();
		
	
		List<GodsafDao> list = new ArrayList<GodsafDao>();
		
		try {
			logger.info("Inside syjsSYGODSAF.do");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				GodsafDao dao = new GodsafDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				

				if(StringUtils.hasValue(dao.getGflavd())){
					logger.info("MATCH: avd");
					Map<String, Object> params = dao.getKeys();
					//Exact match
					list = godsafDaoService.findAll(params);
				}else{
					logger.info("ALL RECORDS...");
					list = godsafDaoService.findAll(this.ALL_RECORDS, new StringBuffer(ORDER_BY));
				}
				
				if (list != null) {
					container.setUser(user);
					container.setList(list);
				} else {
					errMsg = "ERROR on SELECT: Can not find Dao list";
					status = "error";
					logger.info(status + errMsg);
					//sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
					container.setErrMsg(errMsg);
				}

			} else {
				errMsg = "ERROR on SELECT";
				status = "error";
				dbErrorStackTrace.append(" request input parameters are invalid: <user> ...");
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

	
	
}
