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

import gaia3d.domain.HealthCheckLog;
import gaia3d.domain.PageType;
import gaia3d.domain.Pagination;
import gaia3d.service.HealthCheckLogService;
import gaia3d.util.DateUtil;
import gaia3d.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/scheduler/")
public class HealthCheckLogController {
	
	@Autowired
	HealthCheckLogService healthCheckLogService;
	
	@RequestMapping(value = "list-health-check")
	public String listHealthCheckLog(HttpServletRequest request, HealthCheckLog healthCheckLog, 
			@RequestParam(defaultValue="1") String pageNo, Model model) {
		
		log.info("@@ healthCheckLog = {}", healthCheckLog);
		if(StringUtil.isNotEmpty(healthCheckLog.getSearch_start_date())) {
			healthCheckLog.setSearch_start_date(healthCheckLog.getSearch_start_date().substring(0, 8) + DateUtil.START_TIME);
		}
		if(StringUtil.isNotEmpty(healthCheckLog.getSearch_end_date())) {
			healthCheckLog.setSearch_end_date(healthCheckLog.getSearch_end_date().substring(0, 8) + DateUtil.END_TIME);
		}
		
		long totalCount = healthCheckLogService.getHealthCheckLogTotalCount(healthCheckLog);
		Pagination pagination = new Pagination(	request.getRequestURI(), 
													getSearchParameters(PageType.LIST, request, healthCheckLog), 
													totalCount, 
													Long.valueOf(pageNo).longValue(), 
													healthCheckLog.getList_counter());
		log.info("@@ pagination = {}", pagination);
		
		healthCheckLog.setOffset(pagination.getOffset());
		healthCheckLog.setLimit(pagination.getPageRows());
		List<HealthCheckLog> healthCheckLogList = new ArrayList<>();
		if(totalCount > 0l) {
			healthCheckLogList = healthCheckLogService.gethealthCheckList(healthCheckLog);
		}
		
		model.addAttribute(pagination);
		model.addAttribute("healthCheckLog", healthCheckLog);
		model.addAttribute("healthCheckLogList", healthCheckLogList);
		
		return "/scheduler/list-health-check";
	}
	
	/**
	 * 검색 조건
	 * @param droneProject
	 * @return
	 */
	private String getSearchParameters(PageType pageType, HttpServletRequest request, HealthCheckLog healthCheckLog) {
		StringBuffer buffer = new StringBuffer();
		boolean isListPage = true;
		if(pageType.equals(PageType.MODIFY) || pageType.equals(PageType.DETAIL)) {
			isListPage = false;
		}
		
		if(!isListPage) {
			buffer.append("pageNo=" + request.getParameter("pageNo"));
		}
		buffer.append("&");
		buffer.append("search_option=" + StringUtil.getDefaultValue(isListPage ? healthCheckLog.getSearch_option() : request.getParameter("search_option")));
		buffer.append("&");
		try {
			buffer.append("search_value=" + URLEncoder.encode(StringUtil.getDefaultValue(
					isListPage ? healthCheckLog.getSearch_value() : request.getParameter("search_value")), "UTF-8"));
		} catch(Exception e) {
			e.printStackTrace();
			buffer.append("search_value=");
		}
		buffer.append("&");
		buffer.append("search_status=" + StringUtil.getDefaultValue(isListPage ? healthCheckLog.getSearch_status() : request.getParameter("search_status")));
		buffer.append("&");
		buffer.append("search_start_date=" + StringUtil.getDefaultValue(isListPage ? healthCheckLog.getSearch_start_date() : request.getParameter("search_start_date")));
		buffer.append("&");
		buffer.append("search_end_date=" + StringUtil.getDefaultValue(isListPage ? healthCheckLog.getSearch_end_date() : request.getParameter("search_end_date")));
		buffer.append("&");
		buffer.append("order_word=" + StringUtil.getDefaultValue(isListPage ? healthCheckLog.getOrder_word() : request.getParameter("order_word")));
		buffer.append("&");
		buffer.append("order_value=" + StringUtil.getDefaultValue(isListPage ? healthCheckLog.getOrder_value() : request.getParameter("order_value")));
		if(!isListPage) {
			buffer.append("&");
			buffer.append("list_count=" + healthCheckLog.getList_counter());
		}
		return buffer.toString();
	}
}