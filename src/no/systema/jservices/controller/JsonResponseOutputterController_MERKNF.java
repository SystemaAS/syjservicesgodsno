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

import no.systema.jservices.common.dao.MerknfDao;
import no.systema.jservices.common.dao.IDao;
import no.systema.jservices.entities.JsonGenericContainerDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.MerknfDaoService;
import no.systema.jservices.common.util.StringUtils;


/**
 * 
 * @author oscardelatorre
 * @date Okt 2018
 */
@RestController
//@RequestMapping(value="/syjsSYMERKNF.do")
public class JsonResponseOutputterController_MERKNF {
	private static final Logger logger = LogManager.getLogger(JsonResponseOutputterController_MERKNF.class.getName());
	private final Map<String, Object> ALL_RECORDS = null;
	
	@Autowired
	private MerknfDaoService merknfDaoService;
	@Autowired
	private BridfDaoService bridfDaoService;

	
	
	/**
	 * Db-file: 	MERKNF
	 * 
	 * @Example 
	 * (1) SELECT list http://gw.systema.no:8080/syjservicesgodsno/syjsSYMERKNF.do?user=OSCAR
	 * (2) Exact match godsnr/gotrnr/gopos: http://gw.systema.no:8080/syjservicesgodsno/syjsSYMERKNF.do?user=OSCAR&gogn=201801062173001
	 * 
	 * @return
	 */
	@RequestMapping(path="/syjsSYMERKNF.do",method = { RequestMethod.GET, RequestMethod.POST } )
	public JsonGenericContainerDao getSymerknf(@RequestParam("user") String user, HttpSession session, HttpServletRequest request ){
		JsonGenericContainerDao container = new JsonGenericContainerDao();
		
		String gogn = request.getParameter("gogn");
		String gotrnr = request.getParameter("gotrnr");
		String gopos = request.getParameter("gopos");
		
		List<MerknfDao> list = new ArrayList<MerknfDao>();
		
		try {
			logger.info("Inside syjsSYMERKNF.do");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				MerknfDao dao = new MerknfDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				
				list = this.fetchRecords(gogn, gotrnr, gopos, dao);
				
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
	 * Db-file: 	MERKNF
	 * 
	 * @Example 
	 * (1) SELECT list http://gw.systema.no:8080/syjservicesgodsno/syjsSYMERKNF_COUNT.do?user=OSCAR
	 * 
	*/
	@RequestMapping(path="/syjsSYMERKNF_COUNT.do",method = { RequestMethod.GET, RequestMethod.POST } )
	public JsonGenericContainerDao getSymerknfCount(@RequestParam("user") String user, HttpSession session, HttpServletRequest request ){
		JsonGenericContainerDao container = new JsonGenericContainerDao();
		
		String gogn = request.getParameter("gogn");
		String gotrnr = request.getParameter("gotrnr");
		
		try {
			logger.info("Inside syjsSYMERKNF_COUNT.do");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();
			int retval = 0;
			if (StringUtils.hasValue(userName)) {
				MerknfDao dao = new MerknfDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				retval = this.childrenExist(gogn, gotrnr);
				logger.info("COUNTER:"+ retval);
				container.setUser(user);
				Collection<IDao> listUpd = new ArrayList<IDao>(); 
				
				if (retval > 0) {
					dao.setGogn(gogn);
					listUpd.add(dao);
					container.setList(listUpd);
				} else {
					container.setList(listUpd);
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
	 * (1) Update: http://gw.systema.no:8080/syjservicesgodsno/syjsSYMERKNF_U.do?user=OSCAR&mode=U/A/D...&gogn=201801062173001...etc
	 * 
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "syjsSYMERKNF_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JsonGenericContainerDao syjsSYMERKNF_U(HttpSession session, HttpServletRequest request) {
		JsonGenericContainerDao<IDao> container = new JsonGenericContainerDao<IDao>();
		
		String userName = null;
		String errMsg = null;
		String status = null;
		
		try {
			logger.info("Inside syjsSYMERKNF_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			MerknfDao dao = new MerknfDao();
			MerknfDao resultDao = new MerknfDao();
			//
			int crud = 0;
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			if (userName != null && !"".equals(userName)) {
				if ("D".equals(mode)) {
					logger.info("DELETE MERKNF");
					//There is no removal just and update. The update will be on column: gotrnr=*SLETTET
					merknfDaoService.delete(dao);
					
				} else if ("A".equals(mode)) {
					logger.info("CREATE NEW MERKNF");
					resultDao = merknfDaoService.create(dao);
				} else if ("U".equals(mode)) {
					logger.info("UPDATE MERKNF");
					resultDao = merknfDaoService.update(dao);
				} 
				//
				if (resultDao == null && !"D".equals(mode)) {
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
	private List<MerknfDao> fetchRecords(String gogn, String gotrnr, String gopos, MerknfDao dao) {
		List<MerknfDao> list = new ArrayList<MerknfDao>();
		String ORDER_BY = "order by gopos asc";
		
		if(StringUtils.hasValue(gopos) && !"0".equals(gopos)){
			logger.info("MATCH: gogn");
			Map<String, Object> params = dao.getKeys();
			//params.put("gopos", gopos);
			//Exact match
			list = merknfDaoService.findAll(params);
			
		}else if(filterExists(dao)){
			logger.info("Filter in action...");
			Map<String, Object> params = this.getParams(dao); 
			list = merknfDaoService.findAll(params, new StringBuffer(ORDER_BY));
		
		}else{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("gogn", gogn);
			params.put("gotrnr", gotrnr);
			if(params != null && params.size()>0){
				list = merknfDaoService.findAll(params, new StringBuffer(ORDER_BY));
			}
		}
		logger.info(list.size());
		return list;
	}
	
	/**
	 * To know if a parent table (usually GODSJF) has children
	 * @param gogn
	 * @param gotrnr
	 * @return
	 */
	private int childrenExist(String gogn, String gotrnr) {
		int retval = 0;
		if(StringUtils.hasValue(gogn) && gotrnr!=null){
			retval= merknfDaoService.findById(gogn, gotrnr);
		}
		return retval;
	}
	/**
	 * 
	 * @param dao
	 * @return
	 */
	private boolean filterExists(MerknfDao dao){
		boolean retval = false;
		
		if(dao.getGoantk()>0){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGovsla())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGosted())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGomotm())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGomerk())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGomerb())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGomerc())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGomerd())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGomer1())){
			retval = true;
		}
		if(StringUtils.hasValue(dao.getGomkod())){
			retval = true;
		}
		return retval;
	}
	/**
	 * 
	 * @param dao
	 * @return
	 */
	private Map <String, Object> getParams ( MerknfDao dao){
		Map<String, Object> params = dao.getKeys();
		
		if(dao.getGoantk()>0){
			params.put("goantk", dao.getGoantk());
		}
		if(StringUtils.hasValue(dao.getGovsla())){
			params.put("govsla", dao.getGovsla());
		}
		if(StringUtils.hasValue(dao.getGosted())){
			params.put("gosted", dao.getGosted());
		}
		if(StringUtils.hasValue(dao.getGomotm())){
			params.put("gomotm", dao.getGomotm());
		}
		if(StringUtils.hasValue(dao.getGomerk())){
			params.put("gomerk", dao.getGomerk());
		}
		if(StringUtils.hasValue(dao.getGomerb())){
			params.put("gomerb", dao.getGomerb());
		}
		if(StringUtils.hasValue(dao.getGomerc())){
			params.put("gomerc", dao.getGomerc());
		}
		if(StringUtils.hasValue(dao.getGomerd())){
			params.put("gomerd", dao.getGomerd());
		}
		if(StringUtils.hasValue(dao.getGomer1())){
			params.put("gomer1", dao.getGomer1());
		}
		if(StringUtils.hasValue(dao.getGomkod())){
			params.put("gomkod", dao.getGomkod());
		}
		
		return params;
	}	
	
}
