package ru.binbank.fnsservice.contracts;

import java.util.ArrayList;
import java.util.Date;

/**
 * Запрос выписки.
 */
public class ZSVRequest {
    /*
    public Date getOperdateBeg() {
        return null;
    }

    public Date getOperdateEnd() {
        return null;
    }
*/

        private String msgId;
        private String clientINN;
        private Date operdateBeg;
        private Date operdateEnd;
        private int allAccounts;  // по всем счетам?
        private ArrayList<String> selectedAccounts = new ArrayList<String>(); // избранные счета

        public ArrayList<String> getSelectedAccounts() {
            return selectedAccounts;
        }

        public void setSelectedAccounts(ArrayList<String> selectedAccounts) {
            this.selectedAccounts = selectedAccounts;
        }

        public int getAllAccounts() {
            return allAccounts;
        }

        public void setAllAccounts(int allAccounts) {
            this.allAccounts = allAccounts;
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public String getClientINN() {
            return clientINN;
        }

        public void setClientINN(String clientINN) {
            this.clientINN = clientINN;
        }

        public Date getOperdateBeg() {
            return operdateBeg;
        }

        public void setOperdateBeg(Date operdate) {
            this.operdateBeg = operdate;
        }

        public Date getOperdateEnd() {
            return operdateEnd;
        }

        public void setOperdateEnd(Date operdate) {
            this.operdateEnd = operdate;
        }

}
