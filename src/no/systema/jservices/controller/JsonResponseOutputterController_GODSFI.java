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

import no.systema.jservices.common.dao.GodsfiDao;
import no.systema.jservices.entities.JsonGenericContainerDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.GodsfiDaoService;
import no.systema.jservices.common.util.StringUtils;


/**
 * 
 * @author oscardelatorre
 * @date Jun 2018
 */
@RestController
public class JsonResponseOutputterController_GODSFI {
	private static final Logger logger = Logger.getLogger(JsonResponseOutputterController_GODSFI.class.getName());
	private final Map<String, Object> ALL_RECORDS = null;
	String ORDER_BY_DESC = "order by gflbko desc";
	
	@Autowired
	private GodsfiDaoService godsfiDaoService;
	@Autowired
	private BridfDaoService bridfDaoService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(path="/JSONEXAMPLE_GODSFI.do",method = RequestMethod.GET)
	public JsonGenericContainerDao getExample(){
		JsonGenericContainerDao container = new JsonGenericContainerDao();  
		logger.info("at the START...");
		List<GodsfiDao> list = new ArrayList<GodsfiDao>();
		GodsfiDao obj = new GodsfiDao();
		obj.setGflbko("VEB");
		list.add(obj);
		  
		obj = new GodsfiDao();
		obj.setGflbko("222");
		list.add(obj);
		  
		container.setList(list);
		return container;
		  
	}
	
	/**
	 * Db-file: 	GODSFI
	 * 
	 * @Example 
	 * (1) SELECT list http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSFI.do?user=OSCAR
	 * (2) Exact match avd: http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSFI.do?user=OSCAR&gflbko=VEB
	 * 
	 * @return
	 */
	@RequestMapping(path="/syjsSYGODSFI.do",method = { RequestMethod.GET, RequestMethod.POST } )
	public JsonGenericContainerDao getSygodsjf(@RequestParam("user") String user, HttpSession session, HttpServletRequest request ){
		JsonGenericContainerDao container = new JsonGenericContainerDao();
		
	
		List<GodsfiDao> list = new ArrayList<GodsfiDao>();
		
		try {
			logger.info("Inside syjsSYGODSFI.do");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				GodsfiDao dao = new GodsfiDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				

				if(StringUtils.hasValue(dao.getGflbko())){
					logger.info("MATCH: bko");
					Map<String, Object> params = dao.getKeys();
					//Exact match
					list = godsfiDaoService.findAll(params);
				}else{
					logger.info("ALL RECORDS...");
					list = godsfiDaoService.findAll(this.ALL_RECORDS, new StringBuffer(ORDER_BY_DESC));
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
