package ru.binbank.fnsservice.contracts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Familia, name, otchestvo физического лица
 *
 * <p>Java class for FioType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FioType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="Familia" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="60"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="Name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="60"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="Otchestvo">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="60"/>
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
@XmlType(name = "\u0424\u0418\u041e\u0422\u0438\u043f")
public class FioType {

    @XmlAttribute(name = "\u0424\u0430\u043c\u0438\u043b\u0438\u044f", required = true)
    protected String familia;
    @XmlAttribute(name = "\u0418\u043c\u044f", required = true)
    protected String name;
    @XmlAttribute(name = "\u041e\u0442\u0447\u0435\u0441\u0442\u0432\u043e")
    protected String otchestvo;

    /**
     * Gets the value of the familia property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFamilia() {
        return familia;
    }

    /**
     * Sets the value of the familia property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFamilia(String value) {
        this.familia = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the otchestvo property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOtchestvo() {
        return otchestvo;
    }

    /**
     * Sets the value of the otchestvo property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOtchestvo(String value) {
        this.otchestvo = value;
    }

}
