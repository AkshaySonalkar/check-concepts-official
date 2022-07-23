package com.checkconcepts.menu;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.ApplicationScope;

@Controller
@ApplicationScope
public class AppSidebarMenu {
	
	
	@PostConstruct
	public List<MenuCategory> getAllSidebarCategories(){
		
		List<MenuCategory> categoryList = new ArrayList<>();
		MenuCategory category1 = new MenuCategory(1L,"Category Name 1", "1 Desc", true, false,"");
		MenuCategory category2 = new MenuCategory(2L,"Category Name 2", "2 Desc", false, false,"");
		
		categoryList.add(category1);
		categoryList.add(category2);
		
		return categoryList;
	}

}
