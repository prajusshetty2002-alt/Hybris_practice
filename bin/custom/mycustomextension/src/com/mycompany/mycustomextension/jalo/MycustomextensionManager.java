package com.mycompany.mycustomextension.jalo;

import com.mycompany.mycustomextension.constants.MycustomextensionConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class MycustomextensionManager extends GeneratedMycustomextensionManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( MycustomextensionManager.class.getName() );
	
	public static final MycustomextensionManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (MycustomextensionManager) em.getExtension(MycustomextensionConstants.EXTENSIONNAME);
	}
	
}
