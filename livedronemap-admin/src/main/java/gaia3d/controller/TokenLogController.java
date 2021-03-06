package gaia3d.controller;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import gaia3d.domain.PageType;
import gaia3d.domain.Pagination;
import gaia3d.domain.TokenLog;
import gaia3d.service.TokenLogService;
import gaia3d.util.DateUtil;
import gaia3d.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO spinner 처리를 해야 함
 * @author
 *
 */
@Slf4j
@RequestMapping("/log/")
@Controller
public class TokenLogController {
	
	@Autowired
	TokenLogService tokenLogService;
	
	@RequestMapping(value="list-token-log")
	public String listTokenLog(HttpServletRequest request, TokenLog tokenLog, @RequestParam(defaultValue="1") String pageNo, Model model) {
		
		log.info("@@ tokenLog = {}", tokenLog);
		if(StringUtil.isNotEmpty(tokenLog.getStart_date())) {
			tokenLog.setStart_date(tokenLog.getStart_date().substring(0, 8) + DateUtil.START_TIME);
		}
		if(StringUtil.isNotEmpty(tokenLog.getEnd_date())) {
			tokenLog.setEnd_date(tokenLog.getEnd_date().substring(0, 8) + DateUtil.END_TIME);
		}
		
		long totalCount = tokenLogService.getTokenLogTotalCount(tokenLog);
		Pagination pagination = new Pagination(	request.getRequestURI(), 
												getSearchParameters(PageType.LIST, request, tokenLog), 
												totalCount, 
												Long.valueOf(pageNo).longValue(), 
												tokenLog.getList_counter());
		log.info("@@ pagination = {}", pagination);
		
		tokenLog.setOffset(pagination.getOffset());
		tokenLog.setLimit(pagination.getPageRows());
		List<TokenLog> tokenLogList = new ArrayList<>();
		if(totalCount > 0l) {
			// TODO spinner 처리를 해야 함
			tokenLogList = tokenLogService.getListTokenLog(tokenLog);
		}
		
		model.addAttribute(pagination);
		model.addAttribute("tokenLog", tokenLog);
		model.addAttribute("tokenLogList", tokenLogList);
		
		return "/log/list-token-log";
	}
	
	/**
	 * 검색 조건
	 * @param tokenLog
	 * @return
	 */
	private String getSearchParameters(PageType pageType, HttpServletRequest request, TokenLog tokenLog) {
		StringBuffer buffer = new StringBuffer();
		boolean isListPage = true;
		if(pageType.equals(PageType.MODIFY) || pageType.equals(PageType.DETAIL)) {
			isListPage = false;
		}
		
		if(!isListPage) {
			buffer.append("pageNo=" + request.getParameter("pageNo"));
		}
		buffer.append("&");
		buffer.append("search_option=" + StringUtil.getDefaultValue(isListPage ? tokenLog.getSearch_option() : request.getParameter("search_option")));
		buffer.append("&");
		try {
			buffer.append("search_value=" + URLEncoder.encode(StringUtil.getDefaultValue(
					isListPage ? tokenLog.getSearch_value() : request.getParameter("search_value")), "UTF-8"));
		} catch(Exception e) {
			e.printStackTrace();
			buffer.append("search_value=");
		}
		buffer.append("&");
		buffer.append("search_word=" + StringUtil.getDefaultValue(isListPage ? tokenLog.getSearch_word() : request.getParameter("search_type")));
		buffer.append("&");
		buffer.append("token_status=" + StringUtil.getDefaultValue(isListPage ? tokenLog.getToken_status() : request.getParameter("token_status")));
		buffer.append("&");
		buffer.append("start_date=" + StringUtil.getDefaultValue(isListPage ? tokenLog.getStart_date() : request.getParameter("start_date")));
		buffer.append("&");
		buffer.append("end_date=" + StringUtil.getDefaultValue(isListPage ? tokenLog.getEnd_date() : request.getParameter("end_date")));
		buffer.append("&");
		buffer.append("order_word=" + StringUtil.getDefaultValue(isListPage ? tokenLog.getOrder_word() : request.getParameter("order_word")));
		buffer.append("&");
		buffer.append("order_value=" + StringUtil.getDefaultValue(isListPage ? tokenLog.getOrder_value() : request.getParameter("order_value")));
		if(!isListPage) {
			buffer.append("&");
			buffer.append("list_count=" + tokenLog.getList_counter());
		}
		return buffer.toString();
	}

}
