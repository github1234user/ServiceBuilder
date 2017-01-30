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

    public static void main(String[] args) throws IOException, InterruptedException, InvalidKeyException, StorageException, URISyntaxException, JSchException {
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

    public static void CompleteWorkflow() throws IOException, InterruptedException, InvalidKeyException, StorageException, URISyntaxException, JSchException {
        // Detach Ip and Disk from broken VM
        DetachIpAndDiskFromBrokenVM("basevm");

        // Attach IP and DataDisk to a new VM
        AttachIpAndDiskToNewVM("basevm2","pipbasevm","basedatadisk");

        // SSH connect and disk check
        // TBD
        ConnectSSH();
    }

    public static void ConnectSSH() throws JSchException, IOException {

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        JSch jsch=new JSch();

        jsch.addIdentity( "~/key.pub" , "passphrase");

        Session session=jsch.getSession("scoriani", "52.232.114.148", 22);

        session.setConfig(config);

        System.out.println(dateFormat.format(new Date()) + " INFO: Connect via SSH to new VM ");

        session.connect();

        Channel channel=session.openChannel("exec");

        System.out.println(dateFormat.format(new Date()) + " INFO: Exec 'ls /dev/sdc' command to check data disk is there ");

        ((ChannelExec)channel).setCommand("ls /dev/sdc");

        channel.setInputStream(null);

        ((ChannelExec)channel).setErrStream(System.err);

        InputStream in=channel.getInputStream();

        channel.connect();

        System.out.println(dateFormat.format(new Date()) + " INFO: return: ");

        byte[] tmp=new byte[1024];

        while(true){

            while(in.available()>0){
                int i=in.read(tmp, 0, 1024);

                if(i<0)break;

                System.out.print(new String(tmp, 0, i));
            }

            if(channel.isClosed()){
                if(in.available()>0) continue;

                System.out.println("exit-status: "+channel.getExitStatus());

                break;
            }

            try{Thread.sleep(1000);}catch(Exception ee){}
        }
        channel.disconnect();
        session.disconnect();
    }
}