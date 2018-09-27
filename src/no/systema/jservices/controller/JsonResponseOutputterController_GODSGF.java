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

import no.systema.jservices.common.dao.GodsgfDao;
import no.systema.jservices.common.dao.IDao;
import no.systema.jservices.entities.JsonGenericContainerDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.GodsgfDaoService;
import no.systema.jservices.common.util.StringUtils;


/**
 * 
 * @author oscardelatorre
 * @date Aug 2018
 */
@RestController
//@RequestMapping(value="/syjsSYGODSJF.do")
public class JsonResponseOutputterController_GODSGF {
	private static final Logger logger = Logger.getLogger(JsonResponseOutputterController_GODSGF.class.getName());
	private final Map<String, Object> ALL_RECORDS = null;
	
	@Autowired
	private GodsgfDaoService godsgfDaoService;
	@Autowired
	private BridfDaoService bridfDaoService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(path="/JSONEXAMPLE_GODSGF.do",method = RequestMethod.GET)
	public JsonGenericContainerDao getExample(){
		JsonGenericContainerDao container = new JsonGenericContainerDao();  
		logger.info("at the START...");
		List<GodsgfDao> list = new ArrayList<GodsgfDao>();
		GodsgfDao obj = new GodsgfDao();
		obj.setGggn1("111");
		list.add(obj);
		  
		obj = new GodsgfDao();
		obj.setGggn1("03");
		list.add(obj);
		  
		container.setList(list);
		return container;
		  
	}
	
	/**
	 * Db-file: 	GODSGF
	 * 
	 * @Example 
	 * (1) SELECT list http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSGF.do?user=OSCAR
	 * (2) Default list (last x-days from today):  http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSGF.do?user=OSCAR
	 * (3) Exact match godsnr: http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSGF.do?user=OSCAR&gggn1=201801062173001
	 * 
	 * @return
	 */
	@RequestMapping(path="/syjsSYGODSGF.do",method = { RequestMethod.GET, RequestMethod.POST } )
	public JsonGenericContainerDao getSygodsjf(@RequestParam("user") String user, HttpSession session, HttpServletRequest request ){
		JsonGenericContainerDao container = new JsonGenericContainerDao();
		
		String gggn1 = request.getParameter("gggn1");
		
		List<GodsgfDao> list = new ArrayList<GodsgfDao>();
		
		try {
			logger.info("Inside syjsSYGODSGF.do");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				GodsgfDao dao = new GodsgfDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				
				list = this.fetchRecords(gggn1, dao);
				
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
	 * (1) Update: http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSGF_U.do?user=OSCAR&mode=U/A/D...&gggn1=201801062173001...etc
	 * 
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "syjsSYGODSGF_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JsonGenericContainerDao syjsSYGODSGF_U(HttpSession session, HttpServletRequest request) {
		JsonGenericContainerDao<IDao> container = new JsonGenericContainerDao<IDao>();
		
		String userName = null;
		String errMsg = null;
		String status = null;
		
		try {
			logger.info("Inside syjsSYGODSGF_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			GodsgfDao dao = new GodsgfDao();
			GodsgfDao resultDao = new GodsgfDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			if (userName != null && !"".equals(userName)) {
				if ("D".equals(mode)) {
					//logger.info("DELETE GODSJF");
					//There is no removal just and update. The update will be on column: gotrnr=*SLETTET
				} else if ("A".equals(mode)) {
					logger.info("CREATE NEW GODSJF");
					resultDao = godsgfDaoService.create(dao);
				} else if ("U".equals(mode)) {
					logger.info("UPDATE GODSJF");
					resultDao = godsgfDaoService.update(dao);
				}
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
	 * @param gogn (godsnr)
	 * @return
	 */
	private List<GodsgfDao> fetchRecords(String gggn1, GodsgfDao dao) {
		List<GodsgfDao> list = new ArrayList<GodsgfDao>();
		String ORDER_BY = "order by gggn1, gggn2 desc";
		
		if(StringUtils.hasValue(gggn1)){
			logger.info("MATCH: gggn1");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("gggn1", gggn1 + "%");
			if(StringUtils.hasValue(dao.getGggn2())){
				params.put("gggn2", dao.getGggn2());
			}
			
			list = godsgfDaoService.findAll(params, new StringBuffer(ORDER_BY));
			
		}
		logger.info(list.size());
		return list;
	}
	
}
