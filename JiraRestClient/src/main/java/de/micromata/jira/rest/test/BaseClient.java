package de.micromata.jira.rest.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import com.google.gson.Gson;
import de.micromata.jira.rest.JiraRestClient;
import de.micromata.jira.rest.core.jql.EField;
import de.micromata.jira.rest.core.jql.EOperator;
import de.micromata.jira.rest.core.jql.JqlBuilder;
import de.micromata.jira.rest.core.jql.JqlConstants;
import de.micromata.jira.rest.core.misc.RestPathConstants;

public class BaseClient implements JqlConstants, RestPathConstants {
    static AtomicReference<JiraRestClient> clientRef = new AtomicReference();
    static final Gson gson = new Gson();
    public static JiraRestClient restClient;
    static String CONFIGFILENAME = "config.properties";
    static ExecutorService executorService;
    static String URL_PARAM = "url";
    static String LOGIN_PARAM = "login";
    static String PASSWORD_PARAM = "password";

    public static JiraRestClient connect()
            throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        JiraRestClient client = clientRef.get();
        if (client != null) {
            return client;
        }
        executorService = Executors.newFixedThreadPool(100);
        Properties config = loadConfig();
        // ProxyHost proxy = new ProxyHost("proxy", 3128);
        URI uri = new URI(config.getProperty(URL_PARAM));
        JiraRestClient jiraRestClient = new JiraRestClient(executorService);
        jiraRestClient.connect(uri, config.getProperty(LOGIN_PARAM), config.getProperty(PASSWORD_PARAM));
        clientRef.set(jiraRestClient);
        restClient = clientRef.get();
        return clientRef.get();
    }

    public static void disConnect() {
        if (clientRef.get() != null) {
            clientRef.set(null);
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    static Properties loadConfig() throws IOException {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        Properties config = new Properties();
        config.load(new FileInputStream(path + CONFIGFILENAME));
        return config;
    }

    static JqlBuilder.JqlKeyword addListCondition(JqlBuilder builder, EField field, Collection<String> ranges) throws IOException {
        if (ranges == null || ranges.isEmpty()) {
            return null;
        }
        int len = ranges.size();
        if (len == 1) {
            return builder.addCondition(field, EOperator.EQUALS, ranges.iterator().next());
        }
       return builder.addCondition(field, EOperator.IN, ranges.toArray(new String[len]));
    }

    static <K> JqlBuilder.JqlKeyword addSingleCondition(JqlBuilder builder, EField field, EOperator opr, K value,
            Function<K, String> convert) {
        if (value == null) {
            return null;
        }
        return builder.addCondition(field, opr, convert.apply(value));
    }
}
