/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 14-Oct-2026, 7:32:28 pm                     ---
 * ----------------------------------------------------------------
 */
package com.mycompany.mycustombackoffice.jalo;

import com.mycompany.mycustombackoffice.constants.MycustombackofficeConstants;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.Order;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type <code>MycustombackofficeManager</code>.
 */
@SuppressWarnings({"deprecation","unused","cast"})
public abstract class GeneratedMycustombackofficeManager extends Extension
{
	protected static final Map<String, Map<String, AttributeMode>> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, Map<String, AttributeMode>> ttmp = new HashMap();
		Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put("deliveryReference", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.jalo.order.Order", Collections.unmodifiableMap(tmp));
		DEFAULT_INITIAL_ATTRIBUTES = ttmp;
	}
	@Override
	public Map<String, AttributeMode> getDefaultAttributeModes(final Class<? extends Item> itemClass)
	{
		Map<String, AttributeMode> ret = new HashMap<>();
		final Map<String, AttributeMode> attr = DEFAULT_INITIAL_ATTRIBUTES.get(itemClass.getName());
		if (attr != null)
		{
			ret.putAll(attr);
		}
		return ret;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Order.deliveryReference</code> attribute.
	 * @return the deliveryReference
	 */
	public String getDeliveryReference(final SessionContext ctx, final Order item)
	{
		return (String)item.getProperty( ctx, MycustombackofficeConstants.Attributes.Order.DELIVERYREFERENCE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Order.deliveryReference</code> attribute.
	 * @return the deliveryReference
	 */
	public String getDeliveryReference(final Order item)
	{
		return getDeliveryReference( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Order.deliveryReference</code> attribute. 
	 * @param value the deliveryReference
	 */
	public void setDeliveryReference(final SessionContext ctx, final Order item, final String value)
	{
		item.setProperty(ctx, MycustombackofficeConstants.Attributes.Order.DELIVERYREFERENCE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Order.deliveryReference</code> attribute. 
	 * @param value the deliveryReference
	 */
	public void setDeliveryReference(final Order item, final String value)
	{
		setDeliveryReference( getSession().getSessionContext(), item, value );
	}
	
	@Override
	public String getName()
	{
		return MycustombackofficeConstants.EXTENSIONNAME;
	}
	
}
