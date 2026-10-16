/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.mycompany.mycustomextension.controller;

import static com.mycompany.mycustomextension.constants.MycustomextensionConstants.PLATFORM_LOGO_CODE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mycompany.mycustomextension.service.MycustomextensionService;


@Controller
public class MycustomextensionHelloController
{
	@Autowired
	private MycustomextensionService mycustomextensionService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(final ModelMap model)
	{
		model.addAttribute("logoUrl", mycustomextensionService.getHybrisLogoUrl(PLATFORM_LOGO_CODE));
		return "welcome";
	}
}
