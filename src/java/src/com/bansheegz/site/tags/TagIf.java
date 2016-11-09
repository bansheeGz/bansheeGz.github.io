package com.bansheegz.site.tags;

import com.bansheegz.site.Page;
import com.bansheegz.site.PageException;

import java.util.Properties;

class TagIf extends TagSet
{
    private final String content;
    private final String contentElse;
    private final Function function;

    TagIf(Page page, StringBuilder content, int start) throws PageException
    {
        super(page, "if", content, start, false);

        int contentStart = end;
        end = getIndex(contentStart, "if tag not closed. ", "#endif") + "#endif".length();

        String wholeTagContent = content.substring(contentStart, end - "#endif".length());

        int elseIndex = wholeTagContent.indexOf("#else");
        if (elseIndex != -1)
        {
            this.content = wholeTagContent.substring(0, elseIndex);
            this.contentElse = wholeTagContent.substring(elseIndex + 5, wholeTagContent.length());
        }
        else
        {
            this.content = wholeTagContent;
            this.contentElse = null;
        }

        final boolean startsWith = getValue().startsWith("_startsWith");
        final boolean contains = getValue().startsWith("_contains");
//        final boolean has = getValue().startsWith("_has");
        if (startsWith || contains)
        {
            function = startsWith ? new FunctionStartsWith() : new FunctionContains();

            int pStart = indexOf(0, getValue(), "(", "Can not find ( symbol for function FunctionStartsWith");
            int pEnd = indexOf(pStart, value, ")", "Can not find ) symbol for function FunctionStartsWith");

            value = getValue().substring(pStart + 1, pEnd);
        }
        else
        {
            function = new FunctionEquals();
        }
    }

    String getContent()
    {
        return content;
    }

    String getContentElse()
    {
        return contentElse;
    }

    public boolean evaluate(String value, Properties props)
    {
        return function.evaluate(getValue(), value, props);
    }


    private interface Function
    {
        boolean evaluate(String value, String toCompare, Properties props);

    }

    private class FunctionEquals implements Function
    {
        @Override
        public boolean evaluate(String value, String toCompare, Properties props)
        {
            return value.equals(toCompare);
        }
    }

    private class FunctionStartsWith implements Function
    {
        @Override
        public boolean evaluate(String value, String toCompare, Properties props)
        {
            return toCompare.startsWith(value);
        }
    }

    private class FunctionContains implements Function
    {
        @Override
        public boolean evaluate(String value, String toCompare, Properties props)
        {
            return toCompare.contains(value);
        }
    }

/*
    private class FunctionHas implements Function
    {
        @Override
        public boolean evaluate(String value, String toCompare, Properties props)
        {
            return props.getProperty(value) != null;
        }
    }
*/

}
