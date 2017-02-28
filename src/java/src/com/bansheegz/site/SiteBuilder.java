package com.bansheegz.site;

import java.io.File;
import java.io.IOException;

public class SiteBuilder
{
    private static final String TEMPLATES_PATH = "-templates";
    private static final String DESTINATION_PATH = "-destination";

    private File templatesFolder = null;
    private File destinationFolder = null;


    private SiteBuilder(String templatesPath, String destinationPath) throws IOException
    {
        templatesFolder = getFolder(templatesPath);
        destinationFolder = getFolder(destinationPath);
    }

    private void Build() throws Exception
    {
        //find templates
        System.out.println("Scanning...");
        PageRoot root = (PageRoot) scanPage(null, "", templatesFolder, true);

        //log templates
        if (root == null)
        {
            System.out.println("No pages found");
        }
        else
        {
            System.out.println("Found " + root.count() + " pages.");
        }

        //build!
        System.out.println("");
        System.out.println("Building...");

        root.build(destinationFolder);

        System.out.println("DONE!");

    }

    private Page scanPage(Page parent, String path, File folder, boolean isRoot)
    {
        File[] files = folder.listFiles();
        if (files == null || files.length <= 0)
        {
            return null;
        }

        File indexPage = null;
        File templatePage = null;
        for (File file : files)
        {
            if (file.getName().equals("template.html"))
            {
                templatePage = file;
            }
            if (file.getName().equals("index.html"))
            {
                indexPage = file;
            }
        }

        if (indexPage == null)
        {
            return null;
        }

        Page page = isRoot ? new PageRoot(indexPage, templatePage) : new Page(parent, indexPage, templatePage, path);


        for (File file : files)
        {
            if (file == indexPage || file == templatePage || !file.isDirectory())
            {
                continue;
            }

            if(file.getName().startsWith("Testt"))
            {
                continue;
            }
            scanPage(page, path + "/" + folder.getName(), file, false);
        }

        return page;
    }


    //================================================== static
    private static File getFolder(String path) throws IOException
    {
        File folder = new File(new File(".").getCanonicalPath() + path);
        if (!folder.exists())
        {
            throw new IllegalArgumentException("Folder " + path + " does not exists.");
        }

        if (!folder.isDirectory())
        {
            throw new IllegalArgumentException("Path " + path + " is not a folder.");
        }

        return folder;
    }

    public static void main(String[] args) throws Exception
    {
        try
        {
            String templatesPath = null;
            String destinationPath = null;
            for (String arg : args)
            {
                if (arg.startsWith(TEMPLATES_PATH))
                {
                    templatesPath = getArgumentValue(arg);
                }
                if (arg.startsWith(DESTINATION_PATH))
                {
                    destinationPath = getArgumentValue(arg);
                }
            }

            if (templatesPath == null)
            {
                throw new IllegalArgumentException("Missing argument " + TEMPLATES_PATH);
            }
            if (destinationPath == null)
            {
                throw new IllegalArgumentException("Missing argument " + DESTINATION_PATH);
            }

            new SiteBuilder(templatesPath, destinationPath).Build();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();

            System.out.println("========= BansheeGz.SiteBuilder ==========");
            System.out.println("    Builds html files from templates");
            System.out.println("    Usage: java com.bansheegz.site.SiteBuilder -templates={templates_src_folder} -destination={destination_folder}");
        }
    }

    private static String getArgumentValue(String fullArgument)
    {
        int index = fullArgument.indexOf("=");
        if (index == -1)
        {
            throw new IllegalArgumentException("Argument syntax is wrong." + fullArgument + ". The right is -arg=value");
        }

        return fullArgument.substring(index + 1, fullArgument.length());
    }
}
