package ru.binbank.fnsservice.contracts;

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.05.23 at 10:02:59 PM MSK 
//

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Сведения о банке (филиале банка)
 *
 * <p>Java class for БанкТип complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="БанкТип">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="RegNom" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *             &lt;totalDigits value="4"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="NomFil" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *             &lt;totalDigits value="4"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="BIK" use="required" type="{}BIKТип" />
 *       &lt;attribute name="NaimBank" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="160"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="INNBank" use="required" type="{}ИННЮЛТип" />
 *       &lt;attribute name="KPPBank" use="required" type="{}КППТип" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "\u0411\u0430\u043d\u043a\u0422\u0438\u043f")
public class BankType {

    @XmlAttribute(name = "\u0420\u0435\u0433\u041d\u043e\u043c", required = true)
    protected BigInteger regNom;
    @XmlAttribute(name = "\u041d\u043e\u043c\u0424\u0438\u043b", required = true)
    protected BigInteger nomFil;
    @XmlAttribute(name = "\u0411\u0418\u041a", required = true)
    protected String bik;
    @XmlAttribute(name = "\u041d\u0430\u0438\u043c\u0411\u0430\u043d\u043a", required = true)
    protected String naimBank;
    @XmlAttribute(name = "\u0418\u041d\u041d\u0411\u0430\u043d\u043a", required = true)
    protected String innBank;
    @XmlAttribute(name = "\u041a\u041f\u041f\u0411\u0430\u043d\u043a", required = true)
    protected String kppBank;

    /**
     * Gets the value of the regNom property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getRegNom() {
        return regNom;
    }

    /**
     * Sets the value of the regNom property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setRegNom(BigInteger value) {
        this.regNom = value;
    }

    /**
     * Gets the value of the nomFil property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getNomFil() {
        return nomFil;
    }

    /**
     * Sets the value of the nomFil property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setNomFil(BigInteger value) {
        this.nomFil = value;
    }

    /**
     * Gets the value of the bik property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBIK() {
        return bik;
    }

    /**
     * Sets the value of the bik property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBIK(String value) {
        this.bik = value;
    }

    /**
     * Gets the value of the naimBank property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNaim() {
        return naimBank;
    }

    /**
     * Sets the value of the naimBank property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNaimBank(String value) {
        this.naimBank = value;
    }

    /**
     * Gets the value of the innBank property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getINNBank() {
        return innBank;
    }

    /**
     * Sets the value of the innBank property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setINNBank(String value) {
        this.innBank = value;
    }

    /**
     * Gets the value of the kppBank property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKPPBank() {
        return kppBank;
    }

    /**
     * Sets the value of the kppBank property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKPPBank(String value) {
        this.kppBank = value;
    }

}
