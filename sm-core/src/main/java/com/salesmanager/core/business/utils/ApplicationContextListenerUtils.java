package com.salesmanager.core.business.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.salesmanager.core.business.services.search.SearchService;

public class ApplicationContextListenerUtils implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		 ApplicationContext applicationContext = event.getApplicationContext();
		 /** init search service **/
		 SearchService searchService = (SearchService)applicationContext.getBean("productSearchService");
		 searchService.initService();
		
	}

}
