package com.bansheegz.site;

import java.util.Properties;

public class Utils
{
    public static void merge(Properties props, Properties toMerge)
    {
        for (String key : toMerge.stringPropertyNames())
        {
            if (!props.containsKey(key))
            {
                props.setProperty(key, toMerge.getProperty(key));
            }
        }
    }
}
