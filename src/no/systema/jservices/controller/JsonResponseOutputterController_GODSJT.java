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

import no.systema.jservices.common.dao.GodsjtDao;
import no.systema.jservices.common.dao.IDao;
import no.systema.jservices.entities.JsonGenericContainerDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.GodsjtDaoService;
import no.systema.jservices.common.util.StringUtils;


/**
 * 
 * @author oscardelatorre
 * @date Dec 2018
 */
@RestController
//@RequestMapping(value="/syjsSYGODSJT.do")
public class JsonResponseOutputterController_GODSJT {
	private static final Logger logger = Logger.getLogger(JsonResponseOutputterController_GODSJT.class.getName());
	private final Map<String, Object> ALL_RECORDS = null;
	
	@Autowired
	private GodsjtDaoService godsjtDaoService;
	@Autowired
	private BridfDaoService bridfDaoService;

	/**
	 * TEST example for JSON & connectivity issues
	 * @return
	 */
	@RequestMapping(path="/JSONEXAMPLE_GODSJT.do",method = RequestMethod.GET)
	public JsonGenericContainerDao getExample(){
		JsonGenericContainerDao container = new JsonGenericContainerDao();  
		logger.info("at the START...");
		List<GodsjtDao> list = new ArrayList<GodsjtDao>();
		GodsjtDao obj = new GodsjtDao();
		obj.setGtgn("111");
		list.add(obj);
		  
		obj = new GodsjtDao();
		obj.setGtgn("03");
		list.add(obj);
		  
		container.setList(list);
		return container;
		  
	}
	
	/**
	 * Db-file: 	Godsjt
	 * 
	 * @Example 
	 * (1) SELECT list http://10.13.3.22:8080/syjservicesgodsno/syjsSYGODSJT.do?user=OSCAR
	 * (2) Exact match godsnr: http://10.13.3.22:8080/syjservicesgodsno/syjsSYGODSJT.do?user=OSCAR&gtgn=201801062339501
	 * 
	 * @return
	 */
	@RequestMapping(path="/syjsSYGODSJT.do",method = { RequestMethod.GET, RequestMethod.POST } )
	public JsonGenericContainerDao syjsSYGODSJT(@RequestParam("user") String user, HttpSession session, HttpServletRequest request ){
		JsonGenericContainerDao container = new JsonGenericContainerDao();
		
		List<GodsjtDao> list = new ArrayList<GodsjtDao>();
		
		try {
			logger.info("Inside syjsSYGODSJT.do");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				GodsjtDao dao = new GodsjtDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				
				list = this.fetchRecords(dao);
				
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
	 * (1) Update: http://gw.systema.no:8080/syjservicesgodsno/syjsSYGODSJT_U.do?user=OSCAR&mode=U/A/D...&gtgn=201801062173001...etc
	 * 
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "syjsSYGODSJT_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JsonGenericContainerDao syjsSYGODSJT_U(HttpSession session, HttpServletRequest request) {
		JsonGenericContainerDao<IDao> container = new JsonGenericContainerDao<IDao>();
		
		String userName = null;
		String errMsg = null;
		String status = null;
		
		try {
			logger.info("Inside syjsSYGODSJT_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			GodsjtDao dao = new GodsjtDao();
			GodsjtDao resultDao = new GodsjtDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			if (userName != null && !"".equals(userName)) {
				if ("D".equals(mode)) {
					if(StringUtils.hasValue(dao.getGtgn()) && StringUtils.hasValue(dao.getGttrnr()) && StringUtils.hasValue(dao.getGtpos1()) ){
						logger.info("DELETE GODSJT");
						godsjtDaoService.delete(dao);
					}else{
						logger.info("DELETE GODSJT - not enough keys ...");
					}
				} else if ("A".equals(mode)) {
					if(StringUtils.hasValue(dao.getGtgn()) && StringUtils.hasValue(dao.getGttrnr()) && StringUtils.hasValue(dao.getGtpos1()) ){
						logger.info("CREATE NEW GODSJT");
						resultDao = godsjtDaoService.create(dao);
					}else{
						logger.info("CREATE NEW GODSJT - not enough keys ...");
					}
				} else if ("U".equals(mode)) {
					if(StringUtils.hasValue(dao.getGtgn()) && StringUtils.hasValue(dao.getGttrnr()) && StringUtils.hasValue(dao.getGtpos1()) ){
						logger.info("UPDATE GODSJT");
						resultDao = godsjtDaoService.update(dao);
					}else{
						logger.info("UPDATE GODSJT - not enough keys ...");
					}
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
	private List<GodsjtDao> fetchRecords(GodsjtDao dao) {
		List<GodsjtDao> list = new ArrayList<GodsjtDao>();
		String ORDER_BY = "order by gtgn, gttrnr";
		
		if(StringUtils.hasValue(dao.getGtgn())){
			logger.info("MATCH: gtgn");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("gtgn", dao.getGtgn() );
			if(StringUtils.hasValue(dao.getGttrnr())){
				logger.info("MATCH: gttrnr");
				params.put("gttrnr", dao.getGttrnr());
			}
			if(StringUtils.hasValue(dao.getGtpos1())){
				logger.info("MATCH: gtpos1");
				params.put("gtpos1", dao.getGtpos1());
			}
			
			list = godsjtDaoService.findAll(params, new StringBuffer(ORDER_BY));
			
		}
		logger.info(list.size());
		return list;
	}
	
}
