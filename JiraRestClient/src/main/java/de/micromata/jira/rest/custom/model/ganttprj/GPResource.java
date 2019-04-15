package de.micromata.jira.rest.custom.model.ganttprj;

public class GPResource {
    String id;
    String memberId;
    String function = "Default:0";
    String contacts = "";
    String phone = "";
    String rate = "name=\"standard\" value=\"1\"";

    public GPResource(String id, String memberId) {
        this.id = id;
        this.memberId = memberId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
