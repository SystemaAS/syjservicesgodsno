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

import no.systema.jservices.common.dao.GodshfDao;
import no.systema.jservices.common.dao.IDao;
import no.systema.jservices.entities.JsonGenericContainerDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.GodshfDaoService;
import no.systema.jservices.common.util.StringUtils;


/**
 * 
 * @author oscardelatorre
 * @date Sep 2018
 */
@RestController
//@RequestMapping(value="/syjsSYGODSHF.do")
public class JsonResponseOutputterController_GODSHF {
	private static final Logger logger = Logger.getLogger(JsonResponseOutputterController_GODSHF.class.getName());
	private final Map<String, Object> ALL_RECORDS = null;
	
	@Autowired
	private GodshfDaoService godshfDaoService;
	@Autowired
	private BridfDaoService bridfDaoService;

	
	/**
	 * Db-file: 	GODSHF
	 * 
	 * @Example 
	 * (1) SELECT list http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSHF.do?user=OSCAR
	 * (2) Default list (last x-days from today):  http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSHF.do?user=OSCAR&dftdg=3
	 * (3) Exact match godsnr: http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSHF.do?user=OSCAR&gogn=201801062173001
	 * 
	 * @return
	 */
	@RequestMapping(path="/syjsSYGODSHF.do",method = { RequestMethod.GET, RequestMethod.POST } )
	public JsonGenericContainerDao getSygodshf(@RequestParam("user") String user, HttpSession session, HttpServletRequest request ){
		JsonGenericContainerDao container = new JsonGenericContainerDao();
		
		String gogn = request.getParameter("gogn");
		String gotrnr = request.getParameter("gotrnr");
		String dftdg = request.getParameter("dftdg");
		
		List<GodshfDao> list = new ArrayList<GodshfDao>();
		
		try {
			logger.info("Inside syjsSYGODSHF.do");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				GodshfDao dao = new GodshfDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				
				list = this.fetchRecords(gogn, gotrnr, dftdg, dao);
				
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
	
	/**
	 * (1) Update: http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSHF_U.do?user=OSCAR&mode=U/A/D...&gogn=201801062173001...etc
	 * 
	 * @param session
	 * @param request
	 * @return
	 */
	
	@RequestMapping(value = "syjsSYGODSHF_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JsonGenericContainerDao syjsSYGODSHF_U(HttpSession session, HttpServletRequest request) {
		JsonGenericContainerDao<IDao> container = new JsonGenericContainerDao<IDao>();
		
		String userName = null;
		String errMsg = null;
		String status = null;
		
		try {
			logger.info("Inside syjsSYGODSJF_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			GodshfDao dao = new GodshfDao();
			GodshfDao resultDao = new GodshfDao();
			//
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			if (userName != null && !"".equals(userName)) {
				//Only create records (no deletes, no updates
				if ("A".equals(mode)) {
					logger.info("CREATE NEW GODSHF");
					resultDao = godshfDaoService.create(dao);
				} 
				//
				if (resultDao == null) {
					errMsg = "ERROR on UPDATE Could not add/update dao " + ReflectionToStringBuilder.toString(dao);
					logger.info(errMsg);
					container.setErrMsg(errMsg);
				} else {
					// OK UPDATE
					logger.info("Update OK");
					container.setUser(user);
					Collection<IDao> listUpd = new ArrayList<IDao>(); 
					listUpd.add(dao);
					container.setList(listUpd);
				}

			} else {
				// write JSON error output
				errMsg = "ERROR on UPDATE request input parameters are invalid: <user>";
				logger.info(errMsg);
				container.setErrMsg(errMsg);
			}

		} catch (Exception e) {
			logger.info("ERROR:" + e.toString());
			errMsg = "ERROR on UPDATE " + e.toString();
			container.setErrMsg(errMsg);
		}
		session.invalidate();
		return container;

	}
	/**
	 * 
	 * @param gogn
	 * @param gotrnr
	 * @param dftdg
	 * @param dao
	 * @return
	 */
	private List<GodshfDao> fetchRecords(String gogn, String gotrnr, String dftdg, GodshfDao dao) {
		List<GodshfDao> list = new ArrayList<GodshfDao>();
		Calendar calendar = Calendar.getInstance();
		String ORDER_BY_DESC = "order by gogn desc";
		
		if(StringUtils.hasValue(gogn)){
			logger.info("MATCH: gogn");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("gogn", gogn + "%");
			if(StringUtils.hasValue(gotrnr)){
				params.put("gotrnr", gotrnr);
			}
			//Exact match
			list = godshfDaoService.findAll(params);
			
		}else if(filterExists(dao)){
			logger.info("Filter in action...");
			Map<String, Object> params = this.getParams(dao); 
			list = godshfDaoService.findAll(params, new StringBuffer(ORDER_BY_DESC));
		
		}else if(StringUtils.hasValue(dftdg)){
			logger.info("DEFAULT from x-days to now");
			String currentYear = String.valueOf(calendar.get(Calendar.YEAR)); 
			list = godshfDaoService.findDefault(currentYear, this.getFromDay(dftdg), dao);
		
		}else{
			Map<String, Object> params = this.getParams(dao);
			if(params != null && params.size()>0){
				list = godshfDaoService.findAll(params, new StringBuffer(ORDER_BY_DESC));
			}else{
				logger.info("ALL RECORDS...");
				list = godshfDaoService.findAll(this.ALL_RECORDS, new StringBuffer(ORDER_BY_DESC));
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
	private boolean filterExists(GodshfDao dao){
		boolean retval = false;
		
		if(StringUtils.hasValue(dao.getGohpgm())){
			retval = true;
		}
		if(dao.getGohdat()>0){
			retval = true;
		}
		if(dao.getGohtim()>0){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGohusr())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGohkod())){
			retval = true;
		}
		
		return retval;
	}
	/**
	 * 
	 * @param dao
	 * @return
	 */
	private Map <String, Object> getParams ( GodshfDao dao){
		Map<String, Object> params = new HashMap<String, Object>();
		
		if(StringUtils.hasValue(dao.getGohpgm())){
			params.put("gohpgm", dao.getGohpgm());
		}
		if(dao.getGohdat()>0){
			params.put("gohdat", dao.getGohdat());
		}
		if(dao.getGohtim()>0){
			params.put("gohtim", dao.getGohtim() );
		}
		if(StringUtils.hasValue(dao.getGohusr())){
			params.put("gohusr", dao.getGohusr());
		}
		if(StringUtils.hasValue(dao.getGohkod())){
			params.put("gohkod", dao.getGohkod());
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
