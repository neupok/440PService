package ru.binbank.fnsservice.contracts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Физическое лицо, не являющееся индивидуальным предпринимателем (полные данные)
 *
 * <p>Java class for PflType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PflType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FIO" type="{}FioType"/>
 *         &lt;element name="AdrPlat" type="{}AddrRfType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="InnFl" type="{}InnFlТип" />
 *       &lt;attribute name="DateRozhd" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="MestoRozhd">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="254"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="KodDul">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;length value="2"/>
 *             &lt;pattern value="\d{2}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="SerNomDoc">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="25"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="DataDoc" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "\u041f\u0424\u041b\u0422\u0438\u043f", propOrder = {
        "fio",
        "adrPlat"
})
public class PflType {

    @XmlElement(name = "\u0424\u0418\u041e", required = true)
    protected FioType fio;
    @XmlElement(name = "\u0410\u0434\u0440\u041f\u043b\u0430\u0442")
    protected AddrRfType adrPlat;
    @XmlAttribute(name = "\u0418\u041d\u041d\u0424\u041b")
    protected String innFl;
    @XmlAttribute(name = "\u0414\u0430\u0442\u0430\u0420\u043e\u0436\u0434")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateRozhd;
    @XmlAttribute(name = "\u041c\u0435\u0441\u0442\u043e\u0420\u043e\u0436\u0434")
    protected String mestoRozhd;
    @XmlAttribute(name = "\u041a\u043e\u0434\u0414\u0423\u041b")
    protected String kodDul;
    @XmlAttribute(name = "\u0421\u0435\u0440\u041d\u043e\u043c\u0414\u043e\u043a")
    protected String serNomDoc;
    @XmlAttribute(name = "\u0414\u0430\u0442\u0430\u0414\u043e\u043a")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataDoc;

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
     * Gets the value of the adrPlat property.
     *
     * @return
     *     possible object is
     *     {@link AddrRfType }
     *
     */
    public AddrRfType getAdrPlat() {
        return adrPlat;
    }

    /**
     * Sets the value of the adrPlat property.
     *
     * @param value
     *     allowed object is
     *     {@link AddrRfType }
     *
     */
    public void setAdrPlat(AddrRfType value) {
        this.adrPlat = value;
    }

    /**
     * Gets the value of the innFl property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInnFl() {
        return innFl;
    }

    /**
     * Sets the value of the innFl property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInnFl(String value) {
        this.innFl = value;
    }

    /**
     * Gets the value of the dateRozhd property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getDateRozhd() {
        return dateRozhd;
    }

    /**
     * Sets the value of the dateRozhd property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setDateRozhd(XMLGregorianCalendar value) {
        this.dateRozhd = value;
    }

    /**
     * Gets the value of the mestoRozhd property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMestoRozhd() {
        return mestoRozhd;
    }

    /**
     * Sets the value of the mestoRozhd property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMestoRozhd(String value) {
        this.mestoRozhd = value;
    }

    /**
     * Gets the value of the kodDul property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKodDul() {
        return kodDul;
    }

    /**
     * Sets the value of the kodDul property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKodDul(String value) {
        this.kodDul = value;
    }

    /**
     * Gets the value of the serNomDoc property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSerNomDoc() {
        return serNomDoc;
    }

    /**
     * Sets the value of the serNomDoc property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSerNomDoc(String value) {
        this.serNomDoc = value;
    }

    /**
     * Gets the value of the dataDoc property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getDataDoc() {
        return dataDoc;
    }

    /**
     * Sets the value of the dataDoc property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setDataDoc(XMLGregorianCalendar value) {
        this.dataDoc = value;
    }

}
