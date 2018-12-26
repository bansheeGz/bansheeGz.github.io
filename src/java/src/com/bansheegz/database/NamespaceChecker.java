package com.bansheegz.database;

import com.bansheegz.csharp.SourcesChecker;

import java.io.File;
import java.util.Scanner;

public class NamespaceChecker
{
    public static void main(String[] args) throws Exception
    {
        if (args == null || args.length == 0)
        {
            throw new Exception("Provide the root directories");
        }

        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];

            File dir = new File(arg);

            if (!dir.exists())
            {
                throw new Exception("Can not find file " + dir.getAbsolutePath());
            }

            if (!dir.isDirectory())
            {
                throw new Exception("File is not folder" + dir.getAbsolutePath());
            }

            process(dir);

        }

        System.out.println("Checked "+args.length+" dirs.");
    }

    private static void process(File dir) throws Exception
    {
        File[] files = dir.listFiles(pathname -> pathname.getName().endsWith(".cs"));
        if (files.length > 0)
        {
            for (File file : files)
            {
                processCs(file);
            }
        }

        File[] folders = dir.listFiles(File::isDirectory);
        if (folders!=null && folders.length > 0)
        {
            for (File folder : folders)
            {
                process(folder);
            }
        }
    }
    private static void processCs(File csSourceFile) throws Exception
    {
        String source = new Scanner(csSourceFile).useDelimiter("\\Z").next();

        final int namespaceIndex = source.indexOf("namespace ");
        if (namespaceIndex == -1)
        {
            throw new Exception("No namespace in " + csSourceFile.getAbsolutePath());
        }
    }
}
