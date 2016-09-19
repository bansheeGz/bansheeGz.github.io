package com.bansheegz.site.tags;

import com.bansheegz.site.Page;
import com.bansheegz.site.PageException;

class TagGet extends Tag
{
    TagGet(Page page, StringBuilder content, int start) throws PageException
    {
        this(page, "get", content, start);
    }
    TagGet(Page page, String tag, StringBuilder content, int start) throws PageException
    {
        super(page, tag, content, start, "'", "\"", " ", "\r\n", "<");

        char endingChar = content.charAt(nameEnd);
        end = endingChar == '\'' || endingChar == '\"' || endingChar == '<' ? nameEnd : nameEnd + 1;
    }
}
