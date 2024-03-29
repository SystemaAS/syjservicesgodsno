package no.systema.jservices.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import no.systema.jservices.common.dao.GodsjfDao;
import no.systema.jservices.common.dao.IDao;
import no.systema.jservices.entities.JsonGenericContainerDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.GodsjfDaoService;
import no.systema.jservices.common.util.StringUtils;


/**
 * 
 * @author oscardelatorre
 * @date Jun 2018
 */
@RestController
//@RequestMapping(value="/syjsSYGODSJF.do")
public class JsonResponseOutputterController_GODSJF {
	private static final Logger logger = LogManager.getLogger(JsonResponseOutputterController_GODSJF.class.getName());
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
		String gogn2 = request.getParameter("gogn2");
		String gotrnr = request.getParameter("gotrnr");
		String dftdg = request.getParameter("dftdg");
		String dftdg2 = request.getParameter("dftdg2");
		
		
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
				logger.info("gotrnr:" + gotrnr);
				list = this.fetchRecords(gogn, gotrnr, dftdg, dftdg2, dao, gogn2);
				
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
	 * (1) Update: http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSJF_U.do?user=OSCAR&mode=U/A/D...&gogn=201801062173001...etc
	 * 
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "syjsSYGODSJF_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JsonGenericContainerDao syjsSYGODSJF_U(HttpSession session, HttpServletRequest request) {
		JsonGenericContainerDao<IDao> container = new JsonGenericContainerDao<IDao>();
		
		String userName = null;
		String errMsg = null;
		String status = null;
		
		try {
			logger.info("Inside syjsSYGODSJF_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			String gotrnrOrig = request.getParameter("gotrnrOrig");
			
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			GodsjfDao dao = new GodsjfDao();
			GodsjfDao resultDao = new GodsjfDao();
			//
			int crud = 0;
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			if (userName != null && !"".equals(userName)) {
				if ("D".equals(mode)) {
					logger.info("DELETE GODSJF");
					//There is no removal just and update. The update will be on column: gotrnr=*SLETTET
					crud = godsjfDaoService.deleteSpecialCase(dao);
					resultDao = new GodsjfDao();//dummy to avoid general ERROR below...
				} else if ("A".equals(mode)) {
					logger.info("CREATE NEW GODSJF");
					resultDao = godsjfDaoService.create(dao);
				} else if ("U".equals(mode)) {
					logger.info("UPDATE GODSJF");
					resultDao = godsjfDaoService.update(dao);
				} else if ("UTR".equals(mode)) {
					logger.info("UPDATE SPECIAL CASE TRANSITTNR-key update GODSJF");
					crud = godsjfDaoService.updateTransittSpecialCase(dao, gotrnrOrig);
					resultDao = new GodsjfDao();//dummy to avoid general ERROR below...
				}
				//
				if (resultDao == null || crud < 0) {
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
	 * @param gogn2
	 * @return
	 */
	private List<GodsjfDao> fetchRecords(String gogn, String gotrnr, String dftdg, String dftdg2, GodsjfDao dao, String gogn2) {
		List<GodsjfDao> list = new ArrayList<GodsjfDao>();
		Calendar calendar = Calendar.getInstance();
		String ORDER_BY_DESC = "order by gogn desc";
		
		if(StringUtils.hasValue(gogn)){
			logger.info("MATCH: gogn");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("gogn", gogn + "%");
			if(gotrnr!=null){ //in this special case we must allow empty string but not NULL
				params.put("gotrnr", gotrnr + "%");
			}
			if(StringUtils.hasValue(gogn2)){
				//Special case when search must be within a gogn interval
				list = godsjfDaoService.findGognInterval(gogn2, dao);
			}else{
				//Generic find
				list = godsjfDaoService.findAll(params);
			}
			
		}else if(filterExists(dao)){
			logger.info("Filter in action...");
			Map<String, Object> params = this.getParams(dao); 
			list = godsjfDaoService.findAll(params, new StringBuffer(ORDER_BY_DESC));
		
		}else if(StringUtils.hasValue(dftdg)){
			logger.info("DEFAULT from x-days to now");
			String currentYear = String.valueOf(calendar.get(Calendar.YEAR)); 
			list = godsjfDaoService.findDefault(currentYear, this.getDay(dftdg), this.getDay(dftdg2), dao);
			logger.info("LIST SIZE:" + list.size());
		}else{
			Map<String, Object> params = this.getParams(dao);
			if(params != null && params.size()>0){
				list = godsjfDaoService.findAll(params, new StringBuffer(ORDER_BY_DESC));
			}else{
				logger.info("ALL RECORDS...");
				list = godsjfDaoService.findAll(this.ALL_RECORDS, new StringBuffer(ORDER_BY_DESC));
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
	private boolean filterExists(GodsjfDao dao){
		boolean retval = false;
		if(StringUtils.hasValue(dao.getGotrnr())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGoturn())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGobiln())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGomott())){
			retval = true;
		}
		return retval;
	}
	/**
	 * 
	 * @param dao
	 * @return
	 */
	private Map <String, Object> getParams ( GodsjfDao dao){
		Map<String, Object> params = new HashMap<String, Object>();
		
		if(StringUtils.hasValue(dao.getGotrnr())){
			params.put("gotrnr", dao.getGotrnr() + "%");
		}
		if(StringUtils.hasValue(dao.getGoturn())){
			params.put("goturn", dao.getGoturn());
		}
		if(StringUtils.hasValue(dao.getGobiln())){
			params.put("gobiln", dao.getGobiln());
		}
		if(StringUtils.hasValue(dao.getGomott())){
			params.put("gomott", dao.getGomott() + "%");
		}
		
		return params;
	}
	
	/**
	 * 
	 * @param dftdg
	 * @return
	 */
	private String getDay(String dftdg){
		String retval = dftdg;
		if(StringUtils.hasValue(dftdg)){
			int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
			int daysBack = Integer.parseInt(dftdg);
			retval = String.valueOf(dayOfYear - daysBack);
			logger.info("DAY of YEAR:" + retval);
		}
		return retval;
	}
	
	
	
}
