package com.bansheegz.site.tags;

import com.bansheegz.site.Page;
import com.bansheegz.site.PageException;

import java.io.File;
import java.io.PrintWriter;

class Tag
{
    private final int start;
    private String name;
    private String tag;
    private StringBuilder content;

    int nameEnd;
    int end;
    Page page;



    Tag(Page page, String tag, StringBuilder content, int start, String... nameEndSymbols) throws PageException
    {
        this.page = page;
        this.start = start;
        this.content = content;
        this.tag = tag;

        int nameStart = start + tag.length() + 2;
        nameEnd = getIndex(nameStart, "Can not find an end for tag " + tag, nameEndSymbols);
        name = content.substring(nameStart, nameEnd);
    }

    int getIndex(int start, String error, String... symbols) throws PageException
    {
        int[] results = new int[symbols.length];

        for (int i = 0; i < symbols.length; i++)
        {
            String symbol = symbols[i];
            results[i] = content.indexOf(symbol, start);
        }

        int min = -1;
        for (int result : results)
        {
            if (result == -1)
            {
                continue;
            }
            if (min == -1 || result < min)
            {
                min = result;
            }
        }

        if (min == -1 && error != null)
        {
            String message = error;
            final File file = new File("lastError.log");
            try
            {
                if(!file.exists())
                {
                    file.createNewFile();
                }
                try (PrintWriter out = new PrintWriter(file))
                {
                    out.println(content.toString());
                }

                message="Log file lastError.log created. Error at line " + getLine(content, start) + "\r\n" +message;
            }
            catch (Exception e)
            {
            }

            throw new PageException(page, message);
        }

        return min;
    }

    String getName()
    {
        return name;
    }

    void replaceWith(String value)
    {
        content.replace(start, end, value);
    }
    void getTagContent(String value)
    {
        content.replace(start, end, value);
    }

    private static int getLine(StringBuilder builder, int index)
    {
        return builder.toString().substring(0, index).split("\r\n|\r|\n").length;
    }

    protected int indexOf(int pStart, String value, String toSeek, String error) throws PageException
    {
        int pEnd = value.indexOf(toSeek, pStart);
        if (pEnd == -1)
        {
            throw new PageException(page, error);
        }
        return pEnd;
    }

}
