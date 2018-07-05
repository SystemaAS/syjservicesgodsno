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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
	private final Map<String, Object> ALL_RECORDS = null;
	
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
	 * @Example 
	 * (1) SELECT list http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSJF.do?user=OSCAR
	 * (2) Default list (last x-days from today):  http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSJF.do?user=OSCAR&dftdg=3
	 * (3) Exact match godsnr: http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSJF.do?user=OSCAR&gogn=201801062173001
	 * 
	 * @return
	 */
	@RequestMapping(path="/syjsSYGODSJF.do",method = { RequestMethod.GET, RequestMethod.POST } )
	public JsonGenericContainerDao getSygodsjf(@RequestParam("user") String user, HttpSession session, HttpServletRequest request ){
		JsonGenericContainerDao container = new JsonGenericContainerDao();
		
		String gogn = request.getParameter("gogn");
		String dftdg = request.getParameter("dftdg");
		
		List<GodsjfDao> list = new ArrayList<GodsjfDao>();
		
		try {
			logger.info("Inside syjsSYGODSJF.do");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				GodsjfDao dao = new GodsjfDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				
				list = this.fetchRecords(gogn, dftdg, dao);
				
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
	 * (1) Update: http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSJF_U.do?user=OSCAR&mode=U/A/D...&gogn=201801062173001...etc
	 * 
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "syjsSYGODSJF_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JsonGenericContainerDao syjsSYGODSJF_U(HttpSession session, HttpServletRequest request) {
		JsonGenericContainerDao container = new JsonGenericContainerDao();
		
		JsonResponseWriter2<GodsjfDao> jsonWriter = new JsonResponseWriter2<GodsjfDao>();
		StringBuffer sb = new StringBuffer();
		String userName = null;
		String errMsg = null;
		String status = null;
		StringBuffer dbErrorStackTrace = null;

		try {
			logger.info("Inside syjsDOKUF_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			dbErrorStackTrace = new StringBuffer();
			GodsjfDao dao = new GodsjfDao();
			GodsjfDao resultDao = new GodsjfDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			//TODO:  rulerLord ? or data validated in UI?

			if (userName != null && !"".equals(userName)) {
				if ("D".equals(mode)) {
					//logger.info("DELETE DOKUF");
					//dokufDaoService.delete(dao);
				} else if ("A".equals(mode)) {
					//logger.info("CREATE NEW DOKUF");
					//resultDao = dokufDaoService.create(dao);
				} else if ("U".equals(mode)) {
					//logger.info("UPDATE DOKUF");
					resultDao = godsjfDaoService.update(dao);
				}
				if (resultDao == null) {
					errMsg = "ERROR on UPDATE Could not add/update dao " + ReflectionToStringBuilder.toString(dao);
					status = "error ";
					//dbErrorStackTrace.append("Could not add/update dao=" + ReflectionToStringBuilder.toString(dao));
					//sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
					logger.info(status + errMsg);
					container.setErrMsg(errMsg);
				} else {
					// OK UPDATE
					container.setUser(user);
					Collection listUpd = new ArrayList<GodsjfDao>();
					listUpd.add(dao);
					container.setList(listUpd);
				}

			} else {
				// write JSON error output
				errMsg = "ERROR on UPDATE request input parameters are invalid: <user>";
				status = "error";
				//dbErrorStackTrace.append("request input parameters are invalid: <user>");
				//sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
				logger.info(status + errMsg);
				container.setErrMsg(errMsg);
			}

		} catch (Exception e) {
			errMsg = "ERROR on UPDATE ";
			status = "error ";
			//dbErrorStackTrace.append(e.getMessage());
			//sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status,dbErrorStackTrace));
			logger.info("Error:" + e.toString());
			container.setErrMsg(errMsg);
		}
		session.invalidate();
		return container;

	}
	
	/**
	 * 
	 * @param gogn (godsnr)
	 * @return
	 */
	private List<GodsjfDao> fetchRecords(String gogn, String dftdg, GodsjfDao dao) {
		List<GodsjfDao> list = new ArrayList<GodsjfDao>();
		Calendar calendar = Calendar.getInstance();
		
		if(StringUtils.hasValue(gogn)){
			logger.info("MATCH: gogn");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("gogn", gogn + "%");
			//Exact match
			list = godsjfDaoService.findAll(params);
			
		}else if(StringUtils.hasValue(dftdg)){
			logger.info("DEFAULT from x-days to now");
			String currentYear = String.valueOf(calendar.get(Calendar.YEAR)); 
			list = godsjfDaoService.findDefault(currentYear, this.getFromDay(dftdg), dao);
		
		}else{
			Map<String, Object> params = this.getParams(dao);
			if(params != null && params.size()>0){
				list = godsjfDaoService.findAll(params, new StringBuffer("order by gogn desc"));
			}else{
				logger.info("ALL RECORDS...");
				list = godsjfDaoService.findAll(this.ALL_RECORDS, new StringBuffer("order by gogn desc"));
			}
			
		}
		logger.info(list.size());
		return list;
	}
	/**
	 * 
	 * @param dao
	 * @return
	 */
	private Map <String, Object> getParams ( GodsjfDao dao){
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.hasValue(dao.getGotrnr())){
			params.put("gotrnr", dao.getGotrnr());
		}
		if(StringUtils.hasValue(dao.getGoturn())){
			params.put("goturn", dao.getGoturn());
		}
		if(StringUtils.hasValue(dao.getGobiln())){
			params.put("gobiln", dao.getGobiln());
		}
		return params;
	}
	
	/**
	 * 
	 * @param dftdg
	 * @return
	 */
	private String getFromDay(String dftdg){
		int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		int daysBack = Integer.parseInt(dftdg);
		return String.valueOf(dayOfYear - daysBack);
	}
	
	
	
}
