package com.bansheegz.partial;

import com.bansheegz.copyright.CopyRighter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class PartialChecker
{
    private File dir;
    public PartialChecker(File dir)
    {
        this.dir=dir;
    }

    public static void main(String[] args) throws Exception
    {
        if (args == null || args.length != 1)
        {
            throw new Exception("Provide the root directory");
        }
        File dir = new File(args[0]);

        if (!dir.exists())
        {
            throw new Exception("Can not find file " + dir.getAbsolutePath());
        }

        if (!dir.isDirectory())
        {
            throw new Exception("File is not folder" + dir.getAbsolutePath());
        }
        new PartialChecker(dir).process();
    }

    private void process() throws IOException
    {
        process(dir);
    }

    private void process(File dir) throws IOException
    {
        File[] sharpSource = dir.listFiles(pathname -> pathname.getName().endsWith(".cs"));
        if (sharpSource != null)
        {
            for (File file : sharpSource)
            {
                processSource(file);
            }
        }
        File[] subfolders = dir.listFiles(File::isDirectory);
        if (subfolders != null)
        {
            for (File subfolder : subfolders) process(subfolder);
        }
    }

    private void processSource(File file) throws IOException
    {
        String content = new String(Files.readAllBytes(file.toPath()));
        String s = " partial ";
        int index = content.indexOf(s);
        if (index==-1)
        {
            System.out.println("Found non partial class : " + file.getAbsolutePath());

        }
    }
}
