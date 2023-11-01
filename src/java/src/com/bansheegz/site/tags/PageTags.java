package com.bansheegz.site.tags;

import com.bansheegz.site.Page;
import com.bansheegz.site.PageException;
import com.bansheegz.site.PageProcessor;

import java.util.List;
import java.util.Properties;

public class PageTags
{

    public static StringBuilder fillTagSet(Page page, final StringBuilder content, final Properties result) throws PageException
    {
        iterate("set", content, (start, builder) -> {
            TagSet tag = new TagSet(page, content, start);
            result.setProperty(tag.getName(), tag.getValue());
            tag.replaceWith("");
        });
        return content;
    }

    public static StringBuilder fillTagIfDefined(Page page, final StringBuilder content, final Properties result) throws PageException
    {
        iterate("if_defined", content, (start, builder) -> {
            TagIfDefined tag = new TagIfDefined(page, content, start);
            if(result.getProperty(tag.getName())!=null)
            {
                tag.replaceWith(tag.getContent());
            }
            else tag.replaceWith("");
        });
        return content;
    }

    public static StringBuilder fillTagGet(Page page, final StringBuilder content, final Properties props) throws PageException
    {
        iterate("get", content, (start, builder) -> {
            TagGet tag = new TagGet(page, content, start);

            String value = props.getProperty(tag.getName());
            if (value == null)
            {
                throw new PageException(page, "Required prop " + tag.getName() + " is not set");

            }
            tag.replaceWith(value);
        });
        return content;
    }

    public static StringBuilder fillTagIterate(PageProcessor processor, final StringBuilder content, final Properties props) throws PageException
    {
        iterate("iterate", content, (start, builder) -> {
            TagIterate tag = new TagIterate(processor, content, start);

            final List<TagIterate.Section> sections = tag.getSections();

            StringBuilder fullContentBuilder = new StringBuilder();
            for (TagIterate.Section section : sections)
            {
                StringBuilder sectionBuilder = new StringBuilder();

                Properties sectionProperties = new Properties(props);
                if (tag.getHeader() != null)
                {
                    for (TagIterate.Item item : section.getItems())
                    {
                        for (String key : item.getProps().stringPropertyNames())
                        {
                            sectionProperties.setProperty(key, item.getProps().getProperty(key));
                        }
                    }

                    StringBuilder sectionHeaderBuilder = new StringBuilder(tag.getHeader());
                    fillTagIf(processor.getPage(), sectionHeaderBuilder, sectionProperties);
                    fillTagGet(processor.getPage(), sectionHeaderBuilder, sectionProperties);
                    fillTagPath(processor.getPage(), sectionHeaderBuilder, processor.getLevel());

                    sectionBuilder.append(sectionHeaderBuilder.toString());
                }

                int index=1;
                for (TagIterate.Item item : section.getItems())
                {
                    StringBuilder itemBuilder = new StringBuilder(tag.getTemplate());

                    //add section props
                    for (String key : sectionProperties.stringPropertyNames())
                    {
                        if(!item.getProps().containsKey(key))
                        {
                            item.getProps().setProperty(key, sectionProperties.getProperty(key));
                        }
                    }
                    item.getProps().setProperty(TagsConstants.INDEX, "" + index);

                    fillTagIf(processor.getPage(), itemBuilder, item.getProps());
                    fillTagGet(processor.getPage(), itemBuilder, item.getProps());
                    fillTagPath(processor.getPage(), itemBuilder, processor.getLevel());

                    sectionBuilder.append(itemBuilder.toString());

                    index++;
                }

                if (tag.getFooter() != null)
                {
                    sectionBuilder.append(tag.getFooter());
                }


                fullContentBuilder.append(sectionBuilder.toString());
            }

            tag.replaceWith(fullContentBuilder.toString());
        });
        return content;
    }


    public static StringBuilder fillTagIf(Page page, final StringBuilder content, final Properties props) throws PageException
    {
        iterate("if", content, (start, builder) -> {
            TagIf tag = new TagIf(page, content, start);

            String value = props.getProperty(tag.getName());
            if (value == null)
            {
                throw new PageException(page, "Required prop " + tag.getName() + " is not set");
            }


            if (tag.evaluate(value, props))
            {
                tag.replaceWith(tag.getContent());
            }
            else if (tag.getContentElse() != null)
            {
                tag.replaceWith(tag.getContentElse());
            }
            else
            {
                tag.replaceWith("");
            }
        });
        return content;
    }

    private static void iterate(String tag, StringBuilder content, TagIteratorAction action) throws PageException
    {
        final String tagStart = "#" + tag + " ";

        int start = content.indexOf(tagStart);

        while (start != -1)
        {
            action.forEach(start, content);

            start = content.indexOf(tagStart);
        }

    }

    public static StringBuilder fillTagPath(Page page, final StringBuilder content, final int level) throws PageException
    {
        iterate("path", content, (start, builder) -> {
            TagPath tag = new TagPath(page, content, start);

            String path = tag.getName();
            for (int i = 0; i < level; i++)
            {
                path = "../" + path;
            }

            tag.replaceWith(path);
        });
        return content;
    }

    @FunctionalInterface
    public interface TagIteratorAction
    {

        void forEach(int start, StringBuilder builder) throws PageException;
    }


}
