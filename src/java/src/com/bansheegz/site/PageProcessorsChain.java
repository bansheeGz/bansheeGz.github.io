package com.bansheegz.site;

import com.bansheegz.site.tags.PageTags;
import com.bansheegz.site.tags.TagsConstants;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import static com.bansheegz.site.tags.TagsConstants.PATH;

public class PageProcessorsChain
{
    private final List<PageProcessor> parentProcessors;

    private final PageProcessor targetProcessor;
    private final File targetFile;

    public PageProcessorsChain(PageProcessor targetProcessor, List<PageProcessor> parentProcessors)
    {
        this.targetProcessor = targetProcessor;
        targetFile = targetProcessor.getTargetFile();
        this.parentProcessors = parentProcessors;
    }


    public void build() throws Exception
    {

        //gather props
        Properties props = targetProcessor.getProps();
        props.setProperty(PATH, targetProcessor.getPage().getPath());

        boolean skipTemplate = props.getProperty(TagsConstants.SKIP_TEMPLATES) != null;
        for (int i = parentProcessors.size() - 1; i >= 0; i--)
        {
            Utils.merge(props, parentProcessors.get(i).getProps());
        }

        //target index file
        StringBuilder pageBuilder = new StringBuilder(targetProcessor.getIndexContent());
        processPage(targetProcessor.getPage(), pageBuilder, props, targetProcessor.getLevel());
        String pageContent = pageBuilder.toString();

        //target template
        if (targetProcessor.getTemplateContent() != null && !skipTemplate)
        {
            pageContent = processTemplate(props, pageContent, targetProcessor, targetProcessor.getLevel());
        }

        //all parents template
        for (int i = parentProcessors.size() - 1; i >= 0; i--)
        {
            PageProcessor parentProcessor = parentProcessors.get(i);
            pageContent = processTemplate(props, pageContent, parentProcessor, targetProcessor.getLevel());
        }

        try (PrintWriter out = new PrintWriter(targetFile))
        {
            out.println(pageContent);
        }

    }

    private String processTemplate(Properties props, String page, PageProcessor templateProcessor, int level) throws Exception
    {
        if (templateProcessor.getTemplateContent() == null)
        {
            return page;
        }
        props.setProperty(TagsConstants.CONTENT_TAG, page);

        StringBuilder builder = new StringBuilder(templateProcessor.getTemplateContent());

        processPage(templateProcessor.getPage(), builder, props, level);

        return builder.toString();
    }

    private void processPage(Page page, StringBuilder content, Properties props, int targetLevel) throws Exception
    {
        //#iterate
        PageTags.fillTagIterate(targetProcessor, content, props);

        //#get
        PageTags.fillTagGet(page, content, props);

        //#if
        PageTags.fillTagIf(page, content, props);

        //#path
        PageTags.fillTagPath(page, content, targetLevel);

    }
}
