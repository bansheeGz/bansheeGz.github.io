package com.bansheegz.site;

import com.bansheegz.site.tags.PageTags;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PageProcessor
{
    private final Page page;
    private String indexContent;
    private final File targetFolder;
    private final PageProcessor parent;

    protected final List<PageProcessor> children = new ArrayList<PageProcessor>();
    private final File targetFile;
    private String templateContent;
    private Properties props = new Properties();

    public PageProcessor(PageProcessor parent, Page page, File targetFolder) throws Exception
    {
        this.parent = parent;
        this.page = page;
        this.targetFolder = targetFolder;

        //-------------------Files
        targetFile = new File(targetFolder, "index.html");
        if(!targetFile.exists() && !targetFile.createNewFile())
        {
            throw new IOException("Can not create a file " + targetFile);
        }
        if (targetFile.exists() && targetFile.isDirectory())
        {
            throw new IOException("Can not create a file " + targetFile + ".  File exists, but it's a folder");
        }

        //-------------------Set tags
        indexContent = PageTags.fillTagSet(page, new StringBuilder(page.getIndexPageContent()), props).toString();
        if(page.getTemplatePageContent()!=null)
        {
            Properties templateProps = new Properties();
            templateContent = PageTags.fillTagSet(page, new StringBuilder(page.getTemplatePageContent()), templateProps).toString();
            Utils.merge(props, templateProps);
        }

        //-------------------children
        for (Page child : page.getChildren())
        {
            File childDir = new File(targetFolder, child.getFolderName());
            if (!childDir.exists() && !childDir.mkdir())
            {
                throw new IOException("Can not create a directory for a child " + child);
            }
            if (childDir.exists() && !childDir.isDirectory())
            {
                throw new IOException("Can not create a directory for a child " + child + ". File exists, but it's not a folder");
            }
            children.add(new PageProcessor(this, child, childDir));
        }
    }

    public Properties getProps()
    {
        return props;
    }

    public File getTargetFile()
    {
        return targetFile;
    }

    public String getTemplateContent()
    {
        return templateContent;
    }

    public String getIndexContent()
    {
        return indexContent;
    }

    public int getLevel()
    {
        return page.getLevel();
    }

    public Page getPage()
    {
        return page;
    }

    public String getFileContent(String fileName) throws FileNotFoundException
    {
        return page.getFileContent(fileName);
    }

    @Override
    public String toString()
    {
        return page.toString();
    }
}
