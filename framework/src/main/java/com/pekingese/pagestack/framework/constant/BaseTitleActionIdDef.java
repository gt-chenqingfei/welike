/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/10/18
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/10/18
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/10/18, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.constant;

public class BaseTitleActionIdDef {
    private static int id_base = 0x00000000;

    private static int generateID() {
        return id_base++;
    }
    public static final int ACTION_BACK = generateID();
}
