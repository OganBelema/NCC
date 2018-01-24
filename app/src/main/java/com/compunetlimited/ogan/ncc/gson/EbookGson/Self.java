
package com.compunetlimited.ogan.ncc.gson.EbookGson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Self {

    @SerializedName("href")
    @Expose
    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}
