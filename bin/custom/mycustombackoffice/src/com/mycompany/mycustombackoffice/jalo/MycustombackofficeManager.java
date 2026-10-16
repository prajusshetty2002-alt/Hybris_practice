/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.mycompany.mycustombackoffice.jalo;

import com.mycompany.mycustombackoffice.constants.MycustombackofficeConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

public class MycustombackofficeManager extends GeneratedMycustombackofficeManager
{
	public static final MycustombackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (MycustombackofficeManager) em.getExtension(MycustombackofficeConstants.EXTENSIONNAME);
	}
	
}
