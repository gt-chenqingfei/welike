/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/7/25
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/7/25
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/7/25, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.view;

import java.util.Comparator;

class ItemInfoComparator implements Comparator<ItemInfo> {

    @Override
    public int compare(ItemInfo lhs, ItemInfo rhs) {
        return lhs.position - rhs.position;
    }
}
