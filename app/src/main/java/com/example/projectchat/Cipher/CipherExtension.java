package com.example.projectchat.Cipher;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.XmlEnvironment;

public class CipherExtension implements ExtensionElement {
    String idpubsub;

    String pub;

    public String getDescription() {
        return this.idpubsub;
    }

    public String getElementName() {
        return "id";
    }

    public String getNamespace() {
        return "idpubsub";
    }

    public String getUrl() {
        return this.pub;
    }

    public void setDescription(String paramString) {
        this.idpubsub = paramString;
    }

    public void setUrl(String paramString) {
        this.pub = paramString;
    }

    public CharSequence toXML(String paramString) {
        StringBuilder stringBuilder1 = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("<");
        stringBuilder2.append(getElementName());
        stringBuilder2.append(" xmlns=\"");
        stringBuilder2.append(getNamespace());
        stringBuilder2.append("\">");
        stringBuilder1.append(stringBuilder2.toString());
        if (this.pub != null) {
            stringBuilder1.append("<receiver>");
            stringBuilder1.append(this.pub);
            stringBuilder1.append("</receiver>");
        }
        if (this.idpubsub != null) {
            stringBuilder1.append("<sender>");
            stringBuilder1.append(this.idpubsub);
            stringBuilder1.append("</sender>");
        }
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("</");
        stringBuilder2.append(getElementName());
        stringBuilder2.append(">");
        stringBuilder1.append(stringBuilder2.toString());
        return stringBuilder1.toString();
    }

    public CharSequence toXML(XmlEnvironment paramXmlEnvironment) {
        StringBuilder stringBuilder1 = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("<");
        stringBuilder2.append(getElementName());
        stringBuilder2.append(" xmlns=\"");
        stringBuilder2.append(getNamespace());
        stringBuilder2.append("\">");
        stringBuilder1.append(stringBuilder2.toString());
        if (this.pub != null) {
            stringBuilder1.append("<receiver>");
            stringBuilder1.append(this.pub);
            stringBuilder1.append("</receiver>");
        }
        if (this.idpubsub != null) {
            stringBuilder1.append("<sender>");
            stringBuilder1.append(this.idpubsub);
            stringBuilder1.append("</sender>");
        }
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("</");
        stringBuilder2.append(getElementName());
        stringBuilder2.append(">");
        stringBuilder1.append(stringBuilder2.toString());
        return stringBuilder1.toString();
    }
}


/* Location:              C:\new folder\dex2jar-2.0\classes-dex2jar.jar!\com\example\projectchat\Cipher\CipherExtension.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */