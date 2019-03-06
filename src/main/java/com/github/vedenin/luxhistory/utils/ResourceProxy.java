package com.github.vedenin.luxhistory.utils;

import java.io.File;
import java.net.URL;

public class ResourceProxy
{
    public File getFileFromResource(final String name)
    {
        final URL fileName = ResourceProxy.class.getResource(name);
        return new File(fileName.getFile());
    }
}
