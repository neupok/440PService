package ru.binbank.fnsservice.contracts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Адрес в Российской Федерации по КЛАДР
 *
 * <p>Java class for AddrRfType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AddrRfType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="Index" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;length value="6"/>
 *             &lt;pattern value="[0-9]{6}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="KodRegion" use="required" type="{}ССРФТип" />
 *       &lt;attribute name="Raion">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="50"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="Gorod">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="50"/>
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="NaselPunkt">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="50"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="Ulica">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="50"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="Dom">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="20"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="Korpus">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="20"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="Kvart">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="20"/>
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
@XmlType(name = "\u0410\u0434\u0440\u0420\u0424\u0422\u0438\u043f")
public class AddrRfType {

    @XmlAttribute(name = "\u0418\u043d\u0434\u0435\u043a\u0441", required = true)
    protected String index;
    @XmlAttribute(name = "\u041a\u043e\u0434\u0420\u0435\u0433\u0438\u043e\u043d", required = true)
    protected String kodRegion;
    @XmlAttribute(name = "\u0420\u0430\u0439\u043e\u043d")
    protected String raion;
    @XmlAttribute(name = "\u0413\u043e\u0440\u043e\u0434")
    protected String gorod;
    @XmlAttribute(name = "\u041d\u0430\u0441\u0435\u043b\u041f\u0443\u043d\u043a\u0442")
    protected String naselPunkt;
    @XmlAttribute(name = "\u0423\u043b\u0438\u0446\u0430")
    protected String ulica;
    @XmlAttribute(name = "\u0414\u043e\u043c")
    protected String dom;
    @XmlAttribute(name = "\u041a\u043e\u0440\u043f\u0443\u0441")
    protected String korpus;
    @XmlAttribute(name = "\u041a\u0432\u0430\u0440\u0442")
    protected String kvart;

    /**
     * Gets the value of the index property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndex(String value) {
        this.index = value;
    }

    /**
     * Gets the value of the kodRegion property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKodRegion() {
        return kodRegion;
    }

    /**
     * Sets the value of the kodRegion property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKodRegion(String value) {
        this.kodRegion = value;
    }

    /**
     * Gets the value of the raion property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRaion() {
        return raion;
    }

    /**
     * Sets the value of the raion property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRaion(String value) {
        this.raion = value;
    }

    /**
     * Gets the value of the gorod property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGorod() {
        return gorod;
    }

    /**
     * Sets the value of the gorod property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGorod(String value) {
        this.gorod = value;
    }

    /**
     * Gets the value of the naselPunkt property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNaselPunkt() {
        return naselPunkt;
    }

    /**
     * Sets the value of the naselPunkt property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNaselPunkt(String value) {
        this.naselPunkt = value;
    }

    /**
     * Gets the value of the ulica property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUlica() {
        return ulica;
    }

    /**
     * Sets the value of the ulica property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUlica(String value) {
        this.ulica = value;
    }

    /**
     * Gets the value of the dom property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDom() {
        return dom;
    }

    /**
     * Sets the value of the dom property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDom(String value) {
        this.dom = value;
    }

    /**
     * Gets the value of the korpus property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKorpus() {
        return korpus;
    }

    /**
     * Sets the value of the korpus property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKorpus(String value) {
        this.korpus = value;
    }

    /**
     * Gets the value of the kvart property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKvart() {
        return kvart;
    }

    /**
     * Sets the value of the kvart property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKvart(String value) {
        this.kvart = value;
    }

}

