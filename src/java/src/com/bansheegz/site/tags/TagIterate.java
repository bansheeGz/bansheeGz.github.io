package com.bansheegz.site.tags;

import com.bansheegz.site.PageException;
import com.bansheegz.site.PageProcessor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TagIterate extends Tag
{
    private List<Section> sections = new ArrayList<>();

    private String template;
    private String header;
    private String footer;

    public TagIterate(PageProcessor processor, StringBuilder content, int start) throws PageException
    {
        super(processor.getPage(), "iterate", content, start, "\r\n");

        end = getIndex(nameEnd, "Can not find end tag", "#enditerate") + "#enditerate".length();

        template = content.substring(nameEnd + "\r\n".length(), end - "#enditerate".length());


        int footerStart = template.indexOf("#footer\r\n");
        if (footerStart != -1)
        {
            footer = template.substring(footerStart + "#footer\r\n".length(), template.length());
            template = template.substring(0, footerStart);
        }

        int headerStart = template.indexOf("#header\r\n");
        if (headerStart != -1)
        {
            int contentStart = template.indexOf("#content\r\n");
            if (contentStart == -1)
            {
                throw new PageException(page, "Can not fint content tag for #iterate ");
            }
            header = template.substring(headerStart + "#header\r\n".length(), contentStart);
            template = template.substring(contentStart + "#content\r\n".length(), template.length());
        }


        String listContent;
        try
        {
            listContent = processor.getFileContent(getName());
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            throw new PageException(page, "Can not read file for #include file=" + getName());
        }

        BufferedReader reader = new BufferedReader(new StringReader(listContent));
        try
        {
            Section currentSection = new Section();
            Item currentItem = new Item();
            String value = reader.readLine();
            int spaceCount = 0;
            while (value != null)
            {
                if (value.trim().equals(""))
                {
                    if (currentItem.count() > 0)
                    {
                        currentSection.add(currentItem);
                        currentItem = new Item();
                    }
                    spaceCount++;
                }
                else
                {
                    if (spaceCount > 1)
                    {
                        sections.add(currentSection);
                        currentSection = new Section();
                    }

                    spaceCount = 0;
                    int equalSignIndex = value.indexOf('=');
                    if (equalSignIndex < 0)
                    {
                        throw new PageException(page, "Wrong format in file " + getName() + ": No equal sign " + value);
                    }
                    final String key = value.substring(0, equalSignIndex).trim();
                    String finalValue = value.substring(equalSignIndex + 1, value.length());
                    boolean insidePrettyPrint = false;
                    if (finalValue.startsWith("@"))
                    {
                        String nextLine;
                        finalValue = "";
                        while (true)
                        {
                            nextLine = reader.readLine();
                            if (nextLine == null)
                            {
                                break;
                            }
                            if(insidePrettyPrint && nextLine.contains("</pre>"))
                            {
                                insidePrettyPrint=false;
                            }

                            if(insidePrettyPrint)
                            {
                                nextLine = nextLine.replaceAll("<", "&lt;");
                                nextLine = nextLine.replaceAll(">", "&gt;");
                            }

                            finalValue += nextLine + "\r\n";
                            if (nextLine.endsWith("@"))
                            {
                                finalValue = finalValue.substring(0, finalValue.lastIndexOf('@'));
                                break;
                            }
                            if(nextLine.contains("prettyprint"))
                            {
                                insidePrettyPrint=true;
                            }
                        }
                    }
                    else
                    {
                        char lastChar = finalValue.charAt(finalValue.length() - 1);
                        if (lastChar == '#' || lastChar == '~')
                        {
                            //multiline
                            String nextLine = finalValue;
                            finalValue = "";
                            while (nextLine != null && nextLine.length() > 0 && (nextLine.charAt(nextLine.length() - 1) == '#' || nextLine.charAt(nextLine.length() - 1) == '~'))
                            {
                                boolean returnRequired = nextLine.charAt(nextLine.length() - 1) == '~';
                                finalValue += nextLine.substring(0, nextLine.length() - 1);
                                if (returnRequired)
                                {
                                    finalValue += "\r\n";
                                }
                                nextLine = reader.readLine();
                            }

                            if (nextLine != null)
                            {
                                finalValue += nextLine;
                            }
                        }
                    }

                    currentItem.add(key, finalValue);
                }

                value = reader.readLine();
            }
            if (currentItem.count() > 0)
            {
                currentSection.add(currentItem);
            }
            if (currentSection.count() > 0)
            {
                sections.add(currentSection);
            }
        }
        catch (IOException e)
        {
            throw new PageException(page, "Something wrong with file=" + getName());
        }
        if (sections.size() == 0)
        {
            throw new PageException(page, "Can not find any item in #iterate list file " + getName());
        }

    }

    public String getTemplate()
    {
        return template;
    }

    public List<Section> getSections()
    {
        return sections;
    }

    public String getHeader()
    {
        return header;
    }

    public String getFooter()
    {
        return footer;
    }

    public static class Section
    {
        private List<Item> items = new ArrayList<>();

        public void add(Item item)
        {
            items.add(item);
        }

        public int count()
        {
            return items.size();
        }

        public List<Item> getItems()
        {
            return items;
        }
    }

    public static class Item
    {
        private Properties props = new Properties();

        public Item()
        {
        }

        public Properties getProps()
        {
            return props;
        }

        public void add(String key, String value)
        {
            props.setProperty(key, value);
        }

        public int count()
        {
            return props.size();
        }
    }
}
