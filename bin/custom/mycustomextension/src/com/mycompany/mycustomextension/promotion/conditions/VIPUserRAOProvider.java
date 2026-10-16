package com.mycompany.mycustomextension.promotion.conditions;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;

import java.util.HashSet;
import java.util.Set;

public class VIPUserRAOProvider implements RAOProvider<CartModel>
{
    private static final String VIP_USER_GROUP = "VIPuser";

    @Override
    public Set<Object> expandFactModel(final CartModel cart)
    {
        final VIPUserRAO vipUserRAO = new VIPUserRAO();

        boolean isVipUser = false;

        if (cart != null && cart.getUser() != null)
        {
            final UserModel user = cart.getUser();

            if (user.getGroups() != null)
            {
                for (final PrincipalGroupModel group : user.getGroups())
                {
                    if (group != null && VIP_USER_GROUP.equals(group.getUid()))
                    {
                        isVipUser = true;
                        break;
                    }
                }
            }
        }

        vipUserRAO.setVipUser(isVipUser);

        final Set<Object> result = new HashSet<>();
        result.add(vipUserRAO);

        return result;
    }
}