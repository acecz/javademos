package de.micromata.jira.rest.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.Gson;
import de.micromata.jira.rest.JiraRestClient;
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
}
