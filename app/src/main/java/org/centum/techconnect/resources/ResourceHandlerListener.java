package org.centum.techconnect.resources;

import com.java.model.Contact;

public interface ResourceHandlerListener {

    void onDevicesChanged();

    void onContactsChanged(Contact[] oldContacts, Contact[] newContacts);
}
