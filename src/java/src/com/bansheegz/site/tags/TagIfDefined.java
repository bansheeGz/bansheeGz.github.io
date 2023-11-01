package com.bansheegz.site.tags;

import com.bansheegz.site.Page;
import com.bansheegz.site.PageException;

public class TagIfDefined extends Tag
{
    private static final String endTag="#end_if_defined";
    private final String content;

    public TagIfDefined(Page page, StringBuilder content, int start) throws PageException {
        super(page, "if_defined", content, start, " ", "\r\n");

        int contentStart = nameEnd + 1;
        end = getIndex(contentStart, "if_defined tag not closed. ", endTag) + endTag.length();
        this.content = content.substring(contentStart, end - endTag.length());

        String wholeTagContent = content.substring(start, end);
    }

    public String getContent() {
        return content;
    }
}
