package com.deemaso.grotto.utils;

import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Helpers {
    public static float getAttributeAsFloat(Element element, String attribute, float defaultValue) {
        String value = element.getAttribute(attribute);
        if (value.isEmpty()) {
            return defaultValue;
        } else {
            return Float.parseFloat(value);
        }
    }

    public static int getAttributeAsInt(Element element, String attribute, int defaultValue) {
        String value = element.getAttribute(attribute);
        if (value.isEmpty()) {
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static boolean getAttributeAsBoolean(Element element, String attribute, boolean defaultValue) {
        String value = element.getAttribute(attribute);
        if (value.isEmpty()) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(value);
        }
    }

    public static String getAttributeAsString(Element element, String attribute, String defaultValue) {
        String value = element.getAttribute(attribute);
        if (value.isEmpty()) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public static List<String> getChildElementsAsStringList(Element element, String childName) {
        List<String> list = new ArrayList<>();
        NodeList nodeList = element.getElementsByTagName(childName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element childElement = (Element) nodeList.item(i);
            list.add(childElement.getTextContent().trim());
        }
        return list;
    }

}
