package de.micromata.jira.rest.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import de.micromata.jira.rest.core.domain.AttachmentBean;
import de.micromata.jira.rest.core.domain.IssueBean;
import de.micromata.jira.rest.core.domain.JqlSearchResult;
import de.micromata.jira.rest.core.jql.EField;
import de.micromata.jira.rest.core.jql.EOperator;
import de.micromata.jira.rest.core.jql.JqlBuilder;
import de.micromata.jira.rest.core.jql.JqlSearchBean;
import de.micromata.jira.rest.core.util.JsonUtil;

public class UpdateIssueClient extends BaseClient {

    static File file = new File("/Users/cz/Repos/github/javademos/JiraRestClient/user-worklog-summary-2018-04-14.csv");

    public static void main(String[] args) throws Exception {
        try {
            restClient = connect();
            // saveAttachment();
            // deleteAttachmentToIssue();
        } finally {
            disConnect();
        }
    }

    public static void saveAttachment() throws Exception {
        if (file.exists() == true) {
            Future<List<AttachmentBean>> listFuture = restClient.getIssueClient().saveAttachmentToIssue("CNTMAT-5992",
                    file);
            List<AttachmentBean> attachmentBeen = listFuture.get();
            attachmentBeen.forEach(f -> System.out.println(JsonUtil.obj2Str(f)));
        }
    }

    public static void updateWorkLogIssueAttachment(String issueKey, List<File> newAttachments,
            Set<String> removedAttachmentPrefixes) throws Exception {
        removedOldAttachments(issueKey, removedAttachmentPrefixes);
        newAttachments = newAttachments.stream().filter(File::exists).collect(Collectors.toList());
        if (!newAttachments.isEmpty()) {
            restClient.getIssueClient().saveAttachmentToIssue(issueKey,
                    newAttachments.toArray(new File[newAttachments.size()]));
        }
    }

    private static void removedOldAttachments(String issueKey, Set<String> removedAttachmentPrefixes) throws Exception {
        if (removedAttachmentPrefixes.isEmpty()) {
            return;
        }
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder.addCondition(EField.ISSUE_KEY, EOperator.EQUALS, issueKey).build();
        jsb.setJql(jql);
        jsb.setMaxResults(1);
        jsb.addField(EField.ATTACHMENT);
        Future<JqlSearchResult> future = restClient.getSearchClient().searchIssues(jsb);
        List<IssueBean> issues = future.get().getIssues();
        if (issues == null || issues.isEmpty()) {
            return;
        }
        List<AttachmentBean> attachments = issues.get(0).getFields().getAttachment();
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        List<String> deletedAttachments = new ArrayList<>();
        attachments.forEach(a -> {
            String fileName = a.getFilename().trim();
            for (String prefix : removedAttachmentPrefixes) {
                if (fileName.startsWith(prefix)) {
                    deletedAttachments.add(a.getId());
                }
            }
        });
        if (deletedAttachments.isEmpty()) {
            return;
        }
        deletedAttachments.forEach(id -> restClient.getIssueClient().deleteAttachment(id));
    }

}
