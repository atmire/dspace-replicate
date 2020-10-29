package org.dspace.statistics.export.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class MockOpenUrlServiceImpl extends OpenUrlServiceImpl {

    @Autowired
    ArrayList testProcessedUrls;

    /**
     * Returns a response code to simulate contact to the external url
     * When the url contains "fail", a fail code 500 will be returned
     * Otherwise the success code 200 will be returned
     * @param urlStr
     * @return 200 or 500 depending on whether the "fail" keyword is present in the url
     * @throws IOException
     */
    protected int getResponseCodeFromUrl(final String urlStr) throws IOException {
        if (StringUtils.contains(urlStr, "fail")) {
            return HttpURLConnection.HTTP_INTERNAL_ERROR;
        } else {
            testProcessedUrls.add(urlStr);
            return HttpURLConnection.HTTP_OK;
        }
    }
}
