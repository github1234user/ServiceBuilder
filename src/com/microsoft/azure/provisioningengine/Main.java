package com.microsoft.azure.provisioningengine;

import com.microsoft.azure.storage.StorageException;
import okhttp3.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    static DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy K:mm:ss a");
    static ProvisioningEngine provEngine;

    public static void main(String[] args) throws IOException, InterruptedException, InvalidKeyException, StorageException, URISyntaxException {
        Date start = new Date();

        // Create Managed Disk volume
//        provEngine.CreateVolume("osdiskds");

        // Create Base VM Instance based on MD
//        provEngine.CreateBaseVMInstance("basevm");

        // Customize VM Instance (e.g. add new data disk)
        // Generalize VM Instance

        // Capture VM Image
        provEngine.CaptureVMImage("basevm","osdiskds","imgfinalvm");

//        provEngine.DeployTargetVMInstance("finalvm");

        Date end = new Date();
        System.out.println(dateFormat.format(new Date()) + " INFO: Test execution (took " + ((end.getTime() - start.getTime()) / 1000.00) + " seconds) \n");
    }
}