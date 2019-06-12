package com.fafu.polutionrepo.finished.Beans;

import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("topicId")
    public int topicId;
    @SerializedName("accountId")
    private String account_Id;
    @SerializedName("content")
    private String content;
    @SerializedName("hits")
    private int hits;

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getAccount_Id() {
        return account_Id;
    }

    public void setAccount_Id(String account_Id) {
        this.account_Id = account_Id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }
}
