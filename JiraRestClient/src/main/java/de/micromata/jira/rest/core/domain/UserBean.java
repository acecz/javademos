package de.micromata.jira.rest.core.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class UserBean extends BaseBean {

    @Expose
    private Boolean active;
    @Expose
    private AvatarUrlsBean avatarUrls;
    @Expose
    private String displayName;
    @Expose
    private String emailAddress;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public AvatarUrlsBean getAvatarUrls() {
        return avatarUrls;
    }

    public void setAvatarUrls(AvatarUrlsBean avatarUrls) {
        this.avatarUrls = avatarUrls;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }
}
