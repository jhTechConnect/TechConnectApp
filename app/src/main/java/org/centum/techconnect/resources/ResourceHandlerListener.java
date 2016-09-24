package org.centum.techconnect.resources;

import com.java.model.FlowChart;

import com.java.model.Contact;

public interface ResourceHandlerListener {

    void onDevicesChanged(FlowChart[] oldDevices, FlowChart[] newDevices);

    void onContactsChanged(Contact[] oldContacts, Contact[] newContacts);
}
