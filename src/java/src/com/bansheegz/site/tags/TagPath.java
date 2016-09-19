package com.bansheegz.site.tags;

import com.bansheegz.site.Page;
import com.bansheegz.site.PageException;

public class TagPath extends TagGet
{
    public TagPath(Page page, StringBuilder content, int start) throws PageException
    {
        super(page, "path", content, start);
    }
}
