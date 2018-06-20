package ru.binbank.fnsservice.contracts;

import javax.xml.bind.annotation.*;


/**
 * Индивидуальный предприниматель, нотариус, занимающийся частной практикой, адвокат, учредивший адвокатский кабинет
 *
 * <p>Java class for PlIpType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PlIpType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FIO" type="{}FioType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="INNIP" use="required" type="{}ИННФЛТип" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "\u041f\u043b\u0418\u041f\u0422\u0438\u043f", propOrder = {
        "fio"
})
public class PlIpType {

    @XmlElement(name = "\u0424\u0418\u041e", required = true)
    protected FioType fio;
    @XmlAttribute(name = "\u0418\u041d\u041d\u0418\u041f", required = true)
    protected String innip;

    /**
     * Gets the value of the fio property.
     *
     * @return
     *     possible object is
     *     {@link FioType }
     *
     */
    public FioType getFIO() {
        return fio;
    }

    /**
     * Sets the value of the fio property.
     *
     * @param value
     *     allowed object is
     *     {@link FioType }
     *
     */
    public void setFIO(FioType value) {
        this.fio = value;
    }

    /**
     * Gets the value of the innip property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getINNIP() {
        return innip;
    }

    /**
     * Sets the value of the innip property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setINNIP(String value) {
        this.innip = value;
    }

}
