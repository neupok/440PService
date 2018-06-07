package ru.binbank.fnsservice.contracts;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateAdapter extends XmlAdapter<String, XMLGregorianCalendar> {
    private static ThreadLocal<SimpleDateFormat> format = new ThreadLocal<SimpleDateFormat>();

    private SimpleDateFormat getFormat() {
        if (format.get() == null) {
            format.set(new SimpleDateFormat("yyyy-MM-dd"));
        }
        return format.get();
    }

    @Override
    public XMLGregorianCalendar unmarshal(String value) throws Exception {
        if (value == null)
            return null;
        Date date = getFormat().parse(value);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }

    @Override
    public String marshal(XMLGregorianCalendar value) throws Exception {
        return value != null ? getFormat().format(value.toGregorianCalendar().getTime()) : null;
    }
}