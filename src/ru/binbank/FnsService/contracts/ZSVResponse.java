package ru.binbank.fnsservice.contracts;

import java.util.Date;

/**
 * Ответы на запросы выписки.
 */
public class ZSVResponse {
    private String msgId;
    private Date operdateBeg;
    private Date operdateEnd;
    private String code;
    private String amountDeb;
    private String amountCred;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Date getOperdateBeg() {
        return operdateBeg;
    }

    public void setOperdateBeg(Date operdateBeg) {
        this.operdateBeg = operdateBeg;
    }

    public Date getOperdateEnd() {
        return operdateEnd;
    }

    public void setOperdateEnd(Date operdateEnd) {
        this.operdateEnd = operdateEnd;
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
