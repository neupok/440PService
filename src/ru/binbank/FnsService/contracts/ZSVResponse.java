package ru.binbank.fnsservice.contracts;

import java.util.Date;

/**
 * Ответы на запросы выписки.
 */
public class ZSVResponse {
    //private Date operdate;
    //private String code;
    //private String amountDeb;
    //private String amountCred;

    public Date operdate;
    public String code;
    public String amountDeb;
    public String amountCred;

    public Date getOperdate() {
        return operdate;
    }

    public void setOperdate(Date operdate) {
        this.operdate = operdate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAmountDeb() {
        return amountDeb;
    }

    public void setAmountDeb(String amountDeb) {
        this.amountDeb = amountDeb;
    }

    public String getAmountCred() {
        return amountCred;
    }

    public void setAmountCred(String amountCred) {
        this.amountCred = amountCred;
    }

}
