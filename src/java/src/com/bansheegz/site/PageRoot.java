package com.bansheegz.site;

import java.io.File;

public class PageRoot extends Page
{
    public PageRoot(File indexPage, File templatePage)
    {
        super(null, indexPage, templatePage, "");
    }

    public void build(File targetFolder) throws Exception
    {
        new PageRootProcessor(this, targetFolder).build();
    }

}
