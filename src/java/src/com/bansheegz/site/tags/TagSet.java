package com.bansheegz.site.tags;

import com.bansheegz.site.Page;
import com.bansheegz.site.PageException;

class TagSet extends Tag
{
    protected String value;
    protected final boolean hasQuotes;

    TagSet(Page page, StringBuilder content, int start) throws PageException
    {
        this(page, "set", content, start, true);
    }

    TagSet(Page page, String tag, StringBuilder content, int start, boolean skipSpaces) throws PageException
    {
        super(page, tag, content, start, "=");

        hasQuotes = content.charAt(nameEnd + 1) == '\"' || content.charAt(nameEnd + 1) == '\'';
        if (hasQuotes)
        {
            end = getIndex(nameEnd + 2, "Invalid content: #set without matching \" symbol.", "\"", "'");
            value = content.substring(nameEnd + 2, end).trim();
        }
        else
        {
            String[] symbols = skipSpaces?new String[]{"\r\n"}:(new String[]{"\r\n"," "});
            end = getIndex(start + 5, "Invalid content: #set without matching end.", symbols);
            value = content.substring(nameEnd + 1, end).trim();
        }
        end = end + 1;

/*
        value = value.replaceAll("<", "&lt;");
        value = value.replaceAll(">", "&gt;");
*/
    }

    String getValue()
    {
        return value;
    }
}
