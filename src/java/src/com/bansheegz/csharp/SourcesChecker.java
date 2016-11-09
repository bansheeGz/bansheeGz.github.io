package com.bansheegz.csharp;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Scanner;

public class SourcesChecker
{
    private static CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
    CharsetEncoder isoEncoder = Charset.forName("ISO-8859-1").newEncoder();

    private int curveFilesCount;
    private int componentsFilesCount;
    private int editorsFilesCount;

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

        final SourcesChecker checker = new SourcesChecker();
        checker.process(dir);

        System.out.println("Checked curve=" + checker.curveFilesCount + " cc=" + checker.componentsFilesCount + " editor=" + checker.editorsFilesCount);
    }

    private void process(File dir) throws Exception
    {
        File[] files = dir.listFiles(pathname -> {
            return pathname.getName().endsWith(".cs");
        });
        if (files.length > 0)
        {
            for (File file : files)
            {
                processCs(file);
            }
        }

        File[] folders = dir.listFiles(File::isDirectory);
        if (folders.length > 0)
        {
            for (File folder : folders)
            {
                process(folder);
            }
        }
    }

    private void processCs(File csSourceFile) throws Exception
    {
        String source = new Scanner(csSourceFile).useDelimiter("\\Z").next();

        final int namespaceIndex = source.indexOf("namespace ");
        if (namespaceIndex == -1)
        {
            throw new Exception("No namespace in " + csSourceFile.getAbsolutePath());
        }

        final int beginIndex = namespaceIndex + "namespace ".length();
        String nameSpace = null;
        try
        {
            nameSpace = source.substring(beginIndex, Math.min(source.indexOf("\n", beginIndex), source.indexOf(" ", beginIndex)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }

        boolean isEditor = nameSpace.contains("Editor");
        boolean isComponent = nameSpace.contains("Components");
        boolean isCurve = nameSpace.contains("Curve");

        if (!isEditor)
        {
            if (isComponent)
            {
                componentsFilesCount++;
            }
            else if (isCurve)
            {
                curveFilesCount++;
            }
        }
        else
        {
            editorsFilesCount++;
        }

        if (isCurve && !isEditor)
        {
            if (source.contains("BGSpline.Components"))
            {
                throw new Exception("Illegal reference to Cc package from Curve package " + csSourceFile.getName());
            }
        }

        checkNonLatin(source, csSourceFile.getName());
    }

    private static final String IS_ENGLISH_REGEX = "^[ \\w \\{ \\} \\* \\~ \\uFEFF \\d \\d \\s \\. \\& \\+ \\- \\, \\! \\@ \\# \\$ \\% \\^ \\* \\( \\) \\; \\\\ \\/ \\| \\< \\> \\\" \\' \\? \\= \\: \\[ \\] ]*$";

    private void checkNonLatin(String text, String fileName) throws Exception
    {
//        return asciiEncoder.canEncode(source) || isoEncoder.canEncode(source);

//        return  source.matches(IS_ENGLISH_REGEX);

        String[] parts = text.split("\r");
        int line = 1;
        for (String part : parts)
        {

            if (part.trim().equals("") || part.contains("é"))
            {
                line++;
                continue;
            }
            if (!part.matches(IS_ENGLISH_REGEX))
            {
                String wrongSymbol = null;
                int j;
                for (j = 0; j < part.length(); j++)
                {
                    final String symbol = Character.toString(text.charAt(j));
                    if (!symbol.matches(IS_ENGLISH_REGEX))
                    {
                        wrongSymbol = symbol;
                        break;
                    }
                }
                throw new Exception("Illegal characters discovered in " + fileName + ", line " + line + " column=" + j + " symbol=" + wrongSymbol + " text=" + part);
            }
            line++;
        }
    }
}
