package com.bansheegz.site;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PageRootProcessor extends PageProcessor
{
    public PageRootProcessor(PageRoot page, File targetFolder) throws Exception
    {
        super(null, page, targetFolder);

    }

    public void build() throws Exception
    {
        process(new ArrayList<PageProcessor>(), this);
    }

    private static void process(List<PageProcessor> parentList, PageProcessor processor) throws Exception
    {
        final ArrayList<PageProcessor> listForChildren = new ArrayList<PageProcessor>(parentList);
        listForChildren.add(processor);
        for (PageProcessor child : processor.children)
        {
            process(listForChildren, child);
        }

        new PageProcessorsChain(processor, parentList).build();
    }

}
