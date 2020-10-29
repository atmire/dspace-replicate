package org.dspace.authority;

import org.springframework.beans.factory.InitializingBean;

public class MockAuthoritySolrServiceImpl extends AuthoritySolrServiceImpl implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        //We don't use SOLR in the tests of this module
        solr = null;
    }

    public void reset() {
        // This method intentionally left blank.
    }
}
