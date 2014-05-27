package com.avaje.ebean.fetch;

import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Query;
import fetch.FetchDescriptorManagerImpl;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public class Test1 {

    @Test
    public void test() throws IOException {
        final String path = getClass().getResource("Main.groovy").getPath();
//        final String path = "D:\\work\\ebean-fetch-dsl\\ebean-fetch-manager\\src\\test\\groovy\\com\\avaje\\ebean\\fetch\\Main.groovy";
        final FetchDescriptorManagerImpl ma = new FetchDescriptorManagerImpl();
        ma.loadFile(path);

        final EbeanFetchDescriptorManager manager = new EbeanFetchDescriptorManagerImpl(ma, 1000);

        final Query<Test1> query = mock(Query.class);
        when(query.fetch(any(String.class), any(String.class), any(FetchConfig.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                System.out.println("fetch: path=" + args[0] + ", properties=" + args[1]);
                return null;
            }
        });

        manager.apply(query, "advProject");
    }
}
