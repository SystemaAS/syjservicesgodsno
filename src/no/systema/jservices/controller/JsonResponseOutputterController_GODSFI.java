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

import no.systema.jservices.common.dao.GodsfiDao;
import no.systema.jservices.common.dao.IDao;
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
	private static final Logger logger = LogManager.getLogger(JsonResponseOutputterController_GODSFI.class.getName());
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
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("gflbko", dao.getGflbko());
					//Exact match
					logger.info("MATCH: unique");
					params.put("gfenh", dao.getGfenh());
					
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

	/**
	 * (1) Update: http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSFI_U.do?user=OSCAR&mode=U/A/D...&gflbko=1111&gfenh=2...etc
	 * 
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "syjsSYGODSFI_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JsonGenericContainerDao syjsSYGODSFI_U(HttpSession session, HttpServletRequest request) {
		JsonGenericContainerDao<IDao> container = new JsonGenericContainerDao<IDao>();
		
		String userName = null;
		String errMsg = null;
		String status = null;
		
		try {
			logger.info("Inside syjsSYGODSFI_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			GodsfiDao dao = new GodsfiDao();
			GodsfiDao resultDao = new GodsfiDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			if (userName != null && !"".equals(userName)) {
				if ("D".equals(mode)) {
					logger.info("DELETE GODSFI");
					godsfiDaoService.delete(dao);
				} else if ("A".equals(mode)) {
					logger.info("CREATE NEW GODSFI");
					resultDao = godsfiDaoService.create(dao);
				} else if ("U".equals(mode)) {
					logger.info("UPDATE GODSFI");
					resultDao = godsfiDaoService.update(dao);
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
	
	
}
