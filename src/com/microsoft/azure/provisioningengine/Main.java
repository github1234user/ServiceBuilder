package com.microsoft.azure.provisioningengine;

import com.microsoft.azure.storage.StorageException;
import okhttp3.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jcraft.jsch.*;

import java.awt.*;

import javax.swing.*;

import java.io.*;

import static com.microsoft.azure.provisioningengine.ProvisioningEngine.*;

public class Main {

    static private DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy K:mm:ss a");
    static private ProvisioningEngine provEngine;

    public static void main(String[] args) throws IOException, InterruptedException, InvalidKeyException, StorageException, URISyntaxException {
        Date start = new Date();

        //Create Managed Disk volume
        //provEngine.CreateVolume("osdiskds5");
        // Create Base VM Instance based on MD
        //provEngine.CreateBaseVMInstance("basevm");

        // Customize VM Instance (e.g. add new data disk)
        // Generalize VM Instance
        // Capture VM Image
        //provEngine.CaptureVMImageNew("basevm","basevm_OsDisk_1_8f7a67c4cc9a48ae8377a6b614eec5c3","baseimage");

//        provEngine.DeployTargetVMInstance("finalvm9");
//        provEngine.CreateDataVolume("basedatadisk");
//        provEngine.AttachDataVolume("basevm","basedatadisk","0");
//        provEngine.ProvisionAndAttachIP("finalvm7678");

        CompleteWorkflow();

        Date end = new Date();
        System.out.println(dateFormat.format(new Date()) + " INFO: Test execution (took " + ((end.getTime() - start.getTime()) / 1000.00) + " seconds) \n");
    }

    public static void CompleteWorkflow() throws IOException, InterruptedException, InvalidKeyException, StorageException, URISyntaxException
    {
        // Detach Ip and Disk from broken VM
        DetachIpAndDiskFromBrokenVM("basevm");

        // Attach IP and DataDisk to a new VM
        AttachIpAndDiskToNewVM("basevm2","pipbasevm","basedatadisk");

        // SSH connect and disk check
        // TBD
    }
}