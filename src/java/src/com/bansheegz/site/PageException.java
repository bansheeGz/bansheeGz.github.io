package com.bansheegz.site;

public class PageException extends Exception
{
    private Page page;

    public PageException(Page page, String message)
    {
        super(message);
        this.page = page;
    }

    @Override
    public String getMessage()
    {
        return "Page " + page +"\r\n" + super.getMessage();
    }
}
