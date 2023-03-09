package com.example.projectchat.sentImage;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.XmlEnvironment;

public class ImageSentExtensions implements ExtensionElement {

    String description;
    String url;

    @Override
    public String getNamespace() {
        return "jabber:x:oob";
    }

    @Override
    public String getElementName() {
        return "x";
    }

    @Override
    public CharSequence toXML(XmlEnvironment xmlEnvironment) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<" + getElementName() + " xmlns=\"" + getNamespace() + "\">");
        if (url != null) {
            stringBuilder.append("<url>").append(url).append("</url>");
        }

        if (description != null) {
            stringBuilder.append("<desc>").append(description).append("</desc>");
        }
        stringBuilder.append("</" + getElementName() + ">");

        return stringBuilder.toString();
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<" + getElementName() + " xmlns=\"" + getNamespace() + "\">");
        if (url != null) {
            stringBuilder.append("<url>").append(url).append("</url>");
        }

        if (description != null) {
            stringBuilder.append("<desc>").append(description).append("</desc>");
        }
        stringBuilder.append("</" + getElementName() + ">");

        return stringBuilder.toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String _description) {
        this.description = _description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String _url) {
        this.url = _url;
    }
}
