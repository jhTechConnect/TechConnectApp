package org.techconnect.resources;

import org.techconnect.networkhelper.model.Contact;

public interface ResourceHandlerListener {

    void onDevicesChanged();

    void onContactsChanged(Contact[] oldContacts, Contact[] newContacts);
}
