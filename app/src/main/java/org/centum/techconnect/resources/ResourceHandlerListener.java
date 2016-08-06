package org.centum.techconnect.resources;

import org.centum.techconnect.model.Contact;
import org.centum.techconnect.model.Device;

public interface ResourceHandlerListener {

    void onDevicesChanged(Device[] oldDevices, Device[] newDevices);

    void onContactsChanged(Contact[] oldContacts, Contact[] newContacts);
}
