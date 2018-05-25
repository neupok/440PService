package ru.binbank.fnsservice.contracts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Плательщик – организация
 *
 * <p>Java class for PlUlType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PlUlType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="INNUL" use="required" type="{}INNULТип" />
 *       &lt;attribute name="KPP" use="required" type="{}KPPТип" />
 *       &lt;attribute name="NaimUl" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="160"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "\u041f\u043b\u042e\u041b\u0422\u0438\u043f")
public class PlUlType {

    @XmlAttribute(name = "\u0418\u041d\u041d\u042e\u041b", required = true)
    protected String innul;
    @XmlAttribute(name = "\u041a\u041f\u041f", required = true)
    protected String kpp;
    @XmlAttribute(name = "\u041d\u0430\u0438\u043c\u042e\u041b", required = true)
    protected String naimUl;

    /**
     * Gets the value of the innul property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getINNUL() {
        return innul;
    }

    /**
     * Sets the value of the innul property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setINNUL(String value) {
        this.innul = value;
    }

    /**
     * Gets the value of the kpp property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKPP() {
        return kpp;
    }

    /**
     * Sets the value of the kpp property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKPP(String value) {
        this.kpp = value;
    }

    /**
     * Gets the value of the naimUl property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNaimUl() {
        return naimUl;
    }

    /**
     * Sets the value of the naimUl property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNaimUl(String value) {
        this.naimUl = value;
    }

}

