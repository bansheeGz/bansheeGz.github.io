package com.bansheegz.site;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Page
{
    private final boolean hasTemplate;
    private final int level;
    private final File indexPage;
    private final File templatePage;
    private final String path;
    private final Page parent;
    private final List<Page> children = new ArrayList<Page>();

    public Page(Page parent, File indexPage, File templatePage, String path)
    {
        this.parent = parent;
        this.level = parent != null ? parent.level + 1 : 0;
        this.indexPage = indexPage;
        this.templatePage = templatePage;
        this.path = path + "/index.html" ;

        hasTemplate = templatePage!= null;

        if (parent != null)
        {
            parent.addChild(this);
        }
    }


    private void addChild(Page child)
    {
        children.add(child);
    }

    public String getFolderName()
    {
        return indexPage.getParentFile().getName();
    }

    @Override
    public String toString()
    {
        return "Level="+level + " path=" + indexPage.getAbsolutePath();
    }

    public String getPath()
    {
        return path;
    }

    public int count()
    {
        int result = 1;
        for (Page child : children)
        {
            result += child.count();
        }
        return result;
    }

    public String getIndexPageContent() throws FileNotFoundException
    {
        return read(indexPage);
    }

    private String read(File file) throws FileNotFoundException
    {
        return new Scanner(file).useDelimiter("\\Z").next();
    }

    public String getTemplatePageContent() throws FileNotFoundException
    {
        return hasTemplate ? read(templatePage) :null;
    }

    public Page getParent()
    {
        return parent;
    }

    public List<Page> getChildren()
    {
        return children;
    }

    public int getLevel()
    {
        return level;
    }

    public String getFileContent(String fileName) throws FileNotFoundException
    {
        return read(new File(indexPage.getParent(), fileName));
    }
}
