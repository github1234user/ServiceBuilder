package com.microsoft.azure.provisioningengine;

import okhttp3.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

// Include the following imports to use blob APIs.
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvisioningEngine {

    static String body="";
    static String url;
    static com.microsoft.azure.provisioningengine.RestClient restClient;
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    static DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy K:mm:ss a");


    static String storageConnectionString =
            "DefaultEndpointsProtocol=http;" +
                    "AccountName=testdssrc;" +
                    "AccountKey="+ System.getenv("accountkey");

    public static void DetachPublicIP(String vmname) throws IOException, InterruptedException {

        // Detach PublicIP from a NIC
        body="{  \n" +
                "   \"location\":\"West Europe\",\n" +
                "   \"properties\":{  \n" +
                "      \"ipConfigurations\":[  \n" +
                "         {  \n" +
                "            \"name\":\"myip1\",\n" +
                "            \"properties\":{  \n" +
                "               \"subnet\":{  \n" +
                "                  \"id\":\"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/virtualNetworks/testvnet/subnets/default\"\n" +
                "               },\n" +
                "               \"privateIPAllocationMethod\":\"Dynamic\",\n" +
                "               \"privateIPAddressVersion\":\"IPv4\"               \n" +
                "            }\n" +
                "         }\n" +
                "      ],\n" +
                "      \"enableIPForwarding\": false\n" +
                "   }\n" +
                "}";

        url="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/networkInterfaces/nic" + vmname + "?api-version=2016-03-30";;
        restClient.ExecCall("PUT", url, body);
    }

    public static void AttachPublicIP(String vmname, String ipname) throws IOException, InterruptedException {

        // Update the NIC
        body="{  \n" +
                "   \"location\":\"West Europe\",\n" +
                "   \"properties\":{  \n" +
                "      \"ipConfigurations\":[  \n" +
                "         {  \n" +
                "            \"name\":\"myip1\",\n" +
                "            \"properties\":{  \n" +
                "               \"subnet\":{  \n" +
                "                  \"id\":\"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/virtualNetworks/testvnet/subnets/default\"\n" +
                "               },\n" +
                "               \"privateIPAllocationMethod\":\"Dynamic\",\n" +
                "               \"privateIPAddressVersion\":\"IPv4\",               \n" +
                "               \"publicIPAddress\":{  \n" +
                "                  \"id\":\"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/publicIPAddresses/" +ipname+ "\"\n" +
                "               }\n" +
                "            }\n" +
                "         }\n" +
                "      ],\n" +
                "      \"enableIPForwarding\": false\n" +
                "   }\n" +
                "}";

        url="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/networkInterfaces/nic" + vmname + "?api-version=2016-03-30";;
        restClient.ExecCall("PUT", url, body);

    }

    public static void CreateVolume(String diskname) throws IOException, InterruptedException {
        // Create intial Managed Disk from VHD file
        body = "{\n" +
                "    \"location\": \"West Europe\", \n" +
                "        \"tags\": { \n" +
                "        \"organization\": \"TestDS\", \n" +
                "        \"description\": \"Tests creating a full deployment workflow\", \n" +
                "        },\n" +
                "    \"properties\": {\n" +
                "        \"creationData\": { \n" +
                "             \"createOption\": \"Import\", \n" +
                "             \"sourceUri\": \"https://testdssrc.blob.core.windows.net/sourcevhd/img-mci7centos-v003.vhd\",\n" +
                "            }, \n" +
                "        \"osType\": \"Linux\",\n" +
                "        \"accountType\": \"Premium_LRS\",\n" +
                "        \"diskSizeGB\": \"128\"\n" +
                "        } \n" +
                "}";

        url = "https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourcegroups/testds/providers/Microsoft.Compute/disks/" + diskname + "?api-version=2016-04-30-preview";
        restClient.ExecCall("PUT", url, body);
    }

    public static void AttachIpAndDiskToNewVM (String vmname, String pipname, String diskname)  throws IOException, InterruptedException
    {
        // Create a NIC
        body="{  \n" +
                "   \"location\":\"West Europe\",\n" +
                "   \"properties\":{  \n" +
                "      \"ipConfigurations\":[  \n" +
                "         {  \n" +
                "            \"name\":\"myip1\",\n" +
                "            \"properties\":{  \n" +
                "               \"subnet\":{  \n" +
                "                  \"id\":\"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/virtualNetworks/testvnet/subnets/default\"\n" +
                "               },\n" +
                "               \"privateIPAllocationMethod\":\"Dynamic\",\n" +
                "               \"privateIPAddressVersion\":\"IPv4\",               \n" +
                "               \"publicIPAddress\":{  \n" +
                "                  \"id\":\"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/publicIPAddresses/" +pipname+ "\"\n" +
                "               }\n" +
                "            }\n" +
                "         }\n" +
                "      ],\n" +
                "      \"enableIPForwarding\": false\n" +
                "   }\n" +
                "}";

        url="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/networkInterfaces/nic" + vmname + "?api-version=2016-03-30";;
        restClient.ExecCall("PUT", url, body);

        // Create a VM *Instance* based on new Managed Disk
        body = "{\n" +
                "\t\"location\": \"West Europe\",\n" +
                "\t\"properties\": {\n" +
                "\t\t  \"hardwareProfile\": {\n" +
                "    \t  \t\t\"vmSize\": \"Standard_DS2\"\n" +
                "    \t\t},\n" +
                "\t\t  \"osProfile\": {\n" +
                "    \t  \t\t\"computerName\": \""+vmname+"\",\n" +
                "    \t  \t\t\"adminUsername\": \"scoriani\",\n" +
                "    \t  \t\t\"linuxConfiguration\": {\n" +
                "    	  		\"disablePasswordAuthentication\": \"true\",\n"+
                "    	  		    \"ssh\": { \n"+
                "    	  		    \"publicKeys\": [ \n"+
                "                    { \n"+
                "                        \"path\": \"/home/scoriani/.ssh/authorized_keys\", \n"+
                "                        \"keyData\": \"publickey\" \n"+
                "                    } \n"+
                "                   ] \n"+
                "                } \n"+
                "            } \n" +
                "    \t\t},\n" +
                "\t\t\"storageProfile\": {\n" +
                "       \"imageReference\": {\n" +
                "       \"publisher\": \"OpenLogic\",\n" +
                "       \"offer\": \"CentOS\",\n" +
                "       \"sku\": \"6.5\",\n" +
                "       \"version\": \"latest\"\n" +
                "       }, \n" +
                "      \"dataDisks\": [\n" +
                "         {\n" +
                "           \"lun\": 0,\n" +
                "           \"name\": \""+diskname+"\",\n" +
                "           \"createOption\": \"attach\",\n" +
                "           \"managedDisk\":\n" +
                "              {\n" +
                "                \"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/disks/"+diskname+"\"\n" +
                "              }\n" + "\n" +
                "         }]" +
                "   },\n "+
                "\t  \"networkProfile\": {\n" +
                "\t      \"networkInterfaces\": [\n" +
                "\t        {\n" +
                "\t          \"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/networkInterfaces/nic" + vmname + "\",\n" +
                "\t          \"properties\": {\n" +
                "\t            \"primary\": true\n" +
                "\t          }\n" +
                "\t        }\n" +
                "\t      ]\n" +
                "\t    }\n" +
                "\t}\n" +
                "}";

        url = "https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/virtualMachines/" + vmname + "?api-version=2016-04-30-preview";
        restClient.ExecCall("PUT", url, body);

    }

    public  static void DetachIpAndDiskFromBrokenVM(String vmname) throws IOException, InterruptedException
    {
        DetachPublicIP(vmname);

        // Strip Data Disk from an existing VM
        body = "{\n" +
                "\t\"location\": \"West Europe\",\n" +
                "\t\"properties\": {\n" +
                "\t\t  \"hardwareProfile\": {\n" +
                "    \t  \t\t\"vmSize\": \"Standard_DS2\"\n" +
                "    \t\t},\n" +
                "\t\t  \"osProfile\": {\n" +
                "    \t  \t\t\"computerName\": \""+vmname+"\",\n" +
                "    \t  \t\t\"adminUsername\": \"scoriani\",\n" +
                "    \t  \t\t\"linuxConfiguration\": {\n" +
                "    	  		\"disablePasswordAuthentication\": \"true\",\n"+
                "    	  		    \"ssh\": { \n"+
                "    	  		    \"publicKeys\": [ \n"+
                "                    { \n"+
                "                        \"path\": \"/home/scoriani/.ssh/authorized_keys\", \n"+
                "                        \"keyData\": \"publickey\" \n"+
                "                    } \n"+
                "                   ] \n"+
                "                } \n"+
                "            } \n" +
                "    \t\t},\n" +
                "\t\t\"storageProfile\": {\n" +
                "       \"imageReference\": {\n" +
                "       \"publisher\": \"OpenLogic\",\n" +
                "       \"offer\": \"CentOS\",\n" +
                "       \"sku\": \"6.5\",\n" +
                "       \"version\": \"latest\"\n" +
                "       }, \n" +
                "      \"dataDisks\": [\n" +
                "         ]" +
                "   },\n "+
                "\t  \"networkProfile\": {\n" +
                "\t      \"networkInterfaces\": [\n" +
                "\t        {\n" +
                "\t          \"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/networkInterfaces/nic"+vmname+"\",\n" +
                "\t          \"properties\": {\n" +
                "\t            \"primary\": true\n" +
                "\t          }\n" +
                "\t        }\n" +
                "\t      ]\n" +
                "\t    }\n" +
                "\t}\n" +
                "}";

        url = "https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/virtualMachines/" + vmname + "?api-version=2016-04-30-preview";
        restClient.ExecCall("PUT", url, body);

    }
        public  static void CreateBaseVMInstance(String vmname) throws IOException, InterruptedException {

        // Create a Public IP
        body="\n" +
                "{\n" +
                "   \"location\": \"West Europe\",\n" +
                "   \"properties\": {\n" +
                "      \"publicIPAllocationMethod\": \"Static\",\n" +
                "      \"publicIPAddressVersion\": \"IPv4\",\n" +
                "      \"idleTimeoutInMinutes\": 4\n" +
                "   }\n" +
                "}";

        url="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/publicIPAddresses/pip" + vmname + "?api-version=2016-03-30";
        restClient.ExecCall("PUT", url, body);

        // Create a NIC
        body="{  \n" +
                "   \"location\":\"West Europe\",\n" +
                "   \"properties\":{  \n" +
                "      \"ipConfigurations\":[  \n" +
                "         {  \n" +
                "            \"name\":\"myip1\",\n" +
                "            \"properties\":{  \n" +
                "               \"subnet\":{  \n" +
                "                  \"id\":\"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/virtualNetworks/testvnet/subnets/default\"\n" +
                "               },\n" +
                "               \"privateIPAllocationMethod\":\"Dynamic\",\n" +
                "               \"privateIPAddressVersion\":\"IPv4\",               \n" +
                "               \"publicIPAddress\":{  \n" +
                "                  \"id\":\"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/publicIPAddresses/pip" +vmname+ "\"\n" +
                "               }\n" +
                "            }\n" +
                "         }\n" +
                "      ],\n" +
                "      \"enableIPForwarding\": false\n" +
                "   }\n" +
                "}";

        url="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/networkInterfaces/nic" + vmname + "?api-version=2016-03-30";;
        restClient.ExecCall("PUT", url, body);

        // Create a VM *Instance* based on new Managed Disk
        body = "{\n" +
                "\t\"location\": \"West Europe\",\n" +
                "\t\"properties\": {\n" +
                "\t\t  \"hardwareProfile\": {\n" +
                "    \t  \t\t\"vmSize\": \"Standard_DS2\"\n" +
                "    \t\t},\n" +
                "\t\t  \"osProfile\": {\n" +
                "    \t  \t\t\"computerName\": \""+vmname+"\",\n" +
                "    \t  \t\t\"adminUsername\": \"scoriani\",\n" +
                "    \t  \t\t\"linuxConfiguration\": {\n" +
                "    	  		\"disablePasswordAuthentication\": \"true\",\n"+
                "    	  		    \"ssh\": { \n"+
                "    	  		    \"publicKeys\": [ \n"+
                "                    { \n"+
                "                        \"path\": \"/home/scoriani/.ssh/authorized_keys\", \n"+
                "                        \"keyData\": \"publickey\" \n"+
                "                    } \n"+
                "                   ] \n"+
                "                } \n"+
                "            } \n" +
                "    \t\t},\n" +
                "\t\t\"storageProfile\": {\n" +
                "       \"imageReference\": {\n" +
                "       \"publisher\": \"OpenLogic\",\n" +
                "       \"offer\": \"CentOS\",\n" +
                "       \"sku\": \"6.5\",\n" +
                "       \"version\": \"latest\"\n" +
                "       }, \n" +
                "      \"dataDisks\": [\n" +
                "         {\n" +
                "           \"lun\": 0,\n" +
                "           \"name\": \"basedatadisk\",\n" +
                "           \"createOption\": \"attach\",\n" +
                "           \"managedDisk\":\n" +
                "              {\n" +
                "                \"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/disks/basedatadisk\"\n" +
                "              }\n" + "\n" +
                "         }]" +
                "   },\n "+
//                "\t\t\t\"osDisk\": \t{\n" +
//                "\t\t\t\t\"createOption\": \"Empty\",\n" +
//                "\t\t\t\t\"osType\": \"Linux\",\n" +
//                "\t\t\t\t\"managedDisk\": {\n" +
//                "\t\t\t\t\"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/disks/osdisk"+vmname+"\"\n" +
//                "\t\t\t\t}\n" +
//                "\t\t\t}\n" +
//                "\t\t},\n" +
                "\t  \"networkProfile\": {\n" +
                "\t      \"networkInterfaces\": [\n" +
                "\t        {\n" +
                "\t          \"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/networkInterfaces/nic" + vmname + "\",\n" +
                "\t          \"properties\": {\n" +
                "\t            \"primary\": true\n" +
                "\t          }\n" +
                "\t        }\n" +
                "\t      ]\n" +
                "\t    }\n" +
                "\t}\n" +
                "}";

        url = "https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/virtualMachines/" + vmname + "?api-version=2016-04-30-preview";
        restClient.ExecCall("PUT", url, body);
    }

    public static void ProvisionAndAttachIP(String vmname) throws IOException, InterruptedException, URISyntaxException, InvalidKeyException, StorageException
    {
        // Create a new Public IP
        body="\n" +
                "{\n" +
                "   \"location\": \"North Europe\",\n" +
                "   \"properties\": {\n" +
                "      \"publicIPAllocationMethod\": \"Static\",\n" +
                "      \"publicIPAddressVersion\": \"IPv4\",\n" +
                "      \"idleTimeoutInMinutes\": 4\n" +
                "   }\n" +
                "}";

        url="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/publicIPAddresses/pip" + vmname + "?api-version=2016-03-30";
        restClient.ExecCall("PUT", url, body);

        // Update a NIC attached to a VM
        body="{  \n" +
                "   \"location\":\"northeurope\",\n" +
                "   \"properties\":{  \n" +
                "        \"virtualMachine\": { \n" +
                "       \"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/virtualMachines/finalvm7678\" \n" +
                "  }, \n" +
                "      \"ipConfigurations\":[  \n" +
                "         {  \n" +
                "            \"name\":\"ipconfig1\",\n" +
                "            \"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/networkInterfaces/finalvm7678/ipConfigurations/ipconfig1\", \n " +
                "            \"properties\":{  \n" +
                "               \"subnet\":{  \n" +
                "                  \"id\":\"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/virtualNetworks/testds-vnet/subnets/default\"\n" +
                "               },\n" +
                "               \"privateIPAllocationMethod\":\"Dynamic\",\n" +
                "               \"privateIPAddressVersion\":\"IPv4\",               \n" +
                "               \"primary\": true,               \n" +
                "               \"publicIPAddress\":{  \n" +
                "                  \"id\":\"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/publicIPAddresses/pip" +vmname+ "\", \n " +
                "                  \"location\": \"northeurope\"\n" +
                "               }\n" +
                "            }\n" +
                "         }\n" +
                "      ],\n" +
                "      \"enableIPForwarding\": false\n" +
                "   }\n" +
                "}";

        url="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/networkInterfaces/" + vmname + "?api-version=2016-03-30";;
        restClient.ExecCall("PUT", url, body);

    }

    public static void CaptureVMImageNew(String vmname, String diskname, String imagename) throws IOException, InterruptedException, URISyntaxException, InvalidKeyException, StorageException
    {
        String body = "{\n" +
                "  \"location\": \"West Europe\",\n" +
                "  \"properties\": {\n" +
                "    \"sourceVirtualMachine\": {\n" +
                "      \"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/virtualMachines/"+vmname+"\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        url = "https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/images/"+imagename+"?api-version=2016-04-30-preview";

        restClient.ExecCall("PUT", url, body);
    }

    public static void CaptureVMImage(String vmname, String diskname, String imagename) throws IOException, InterruptedException, URISyntaxException, InvalidKeyException, StorageException
    {

        String sourceuri="";
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();


        // Temporary workaround, will be replaced when CreateImage API will be available

        // First deallocate Base VM Instance
        url = "https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/virtualMachines/"+vmname+"/deallocate?api-version=2016-04-30-preview";
        restClient.ExecCall("POST", url, "");

        // Then get access to the underlying VHD
        body = "{ \n" +
                "\t\"access\": \"read\",\n" +
                "\t\"durationInSeconds\": \"3600\" \n" +
                "}";

        url = "https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/disks/"+diskname+"/BeginGetAccess?api-version=2016-04-30-preview";
        String bo = restClient.ExecCall("POST", url, body);

        JSONObject jsonObj = new JSONObject(bo);
        // for whatever reason a simple .getString("accessSAS") doesn't work :(
        sourceuri = (String) jsonObj.getJSONObject("properties").getJSONObject("output").toMap().values().toArray()[0];

        // Copy the VHD to a target storage account (maybe in the same or different subscription)
        CloudBlobContainer container = blobClient.getContainerReference("sourcevhd");
        CloudPageBlob dest = container.getPageBlobReference("img"+diskname+".vhd");

        dest.startCopy(new URI(sourceuri));
        MonitorCopy(dest);

        // Release access to the underlying VHD
        body = "{ \n" +
                "\t\"access\": \"read\",\n" +
                "\t\"durationInSeconds\": \"3600\" \n" +
                "}";

        url = "https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/disks/"+diskname+"/EndGetAccess?api-version=2016-04-30-preview";
        restClient.ExecCall("POST", url, body);

        // Create the image from that VHD file
        body = "{ \n" +
                "    \"location\": \"West Europe\", \n" +
                "    \"tags\": { \n" +
                "        \"organization\": \"Contoso Informations Systems\" \n" +
                "    }, \n" +
                "    \"properties\": { \n" +
                "        \"storageProfile\": { \n" +
                "            \"osDisk\": { \n" +
                "                \"osType\": \"Linux\", \n" +
                "                \"osState\": \"specialized\",  \"blobUri\": \"https://testdssrc.blob.core.windows.net/sourcevhd/img"+diskname+".vhd\",\n" +
                "                \"caching\": \"readonly\",  \"storageAccountType\": \"Premium_LRS\" \n" +
                "            }\n" +
                "        } \n" +
                "    } \n" +
                "}";

        url ="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/images/"+imagename+"?api-version=2016-04-30-preview";
        restClient.ExecCall("PUT", url, body);

    }

    public static void DeployTargetVMInstance(String vmname) throws IOException, InterruptedException {

        // Create a Public IP
        body="\n" +
                "{\n" +
                "   \"location\": \"West Europe\",\n" +
                "   \"properties\": {\n" +
                "      \"publicIPAllocationMethod\": \"Static\",\n" +
                "      \"publicIPAddressVersion\": \"IPv4\",\n" +
                "      \"idleTimeoutInMinutes\": 4\n" +
                "   }\n" +
                "}";

        url="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/publicIPAddresses/pip" + vmname + "?api-version=2016-03-30";
        restClient.ExecCall("PUT", url, body);

        // Create a NIC
        body="{  \n" +
                "   \"location\":\"West Europe\",\n" +
                "   \"properties\":{  \n" +
                "      \"ipConfigurations\":[  \n" +
                "         {  \n" +
                "            \"name\":\"myip1\",\n" +
                "            \"properties\":{  \n" +
                "               \"subnet\":{  \n" +
                "                  \"id\":\"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/virtualNetworks/vnet3043f9f6d1/subnets/subnet1\"\n" +
                "               },\n" +
                "               \"privateIPAllocationMethod\":\"Dynamic\",\n" +
                "               \"privateIPAddressVersion\":\"IPv4\",               \n" +
                "               \"publicIPAddress\":{  \n" +
                "                  \"id\":\"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/publicIPAddresses/pip" +vmname+ "\"\n" +
                "               }\n" +
                "            }\n" +
                "         }\n" +
                "      ],\n" +
                "      \"enableIPForwarding\": false\n" +
                "   }\n" +
                "}";

        url="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/networkInterfaces/nic" + vmname + "?api-version=2016-03-30";;
        restClient.ExecCall("PUT", url, body);

        // Deploy a customized VM Instance based on the Image
        body="{\n" +
                "\t\"location\": \"West Europe\",\n" +
                "\t\"properties\": {\n" +
                "\t\t  \"hardwareProfile\": {\n" +
                "    \t  \t\t\"vmSize\": \"Standard_DS1\"\n" +
                "    \t\t},\n" +
                "\t\t\t\t\"storageProfile\": {\n" +
                "\t\t\t\t\t\"imageReference\": {\n" +
                "\t\t\t\t\t\t\"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/images/imgfinalvm5\"\n" +
                "\t\t\t\t}\n" +
                "\t\t},\n" +
                "\t  \"networkProfile\": {\n" +
                "\t      \"networkInterfaces\": [\n" +
                "\t        {\n" +
                "\t          \"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Network/networkInterfaces/nic" + vmname + "\",\n" +
                "\t          \"properties\": {\n" +
                "\t            \"primary\": true\n" +
                "\t          }\n" +
                "\t        }\n" +
                "\t      ]\n" +
                "\t    }\n" +
                "\t}\n" +
                "}";

        url ="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/virtualMachines/"+vmname+"?api-version=2016-04-30-preview";
        restClient.ExecCall("PUT", url, body);
    }

    public static void MonitorCopy(CloudPageBlob dest) throws StorageException, InterruptedException
    {
        CopyStatus status;
        long bytesTotal=0,bytesCopied=0;
        boolean pendingCopy = true;

        while (pendingCopy) {

            pendingCopy = false;

            Thread.sleep(10000);

            status = dest.getCopyState().getStatus();

            try {
                String lease = dest.acquireLease();
                pendingCopy = false;
                dest.breakLease(null);
            } catch (StorageException e)
            {
                pendingCopy = true;
                log.info(dateFormat.format(new Date()) + " Copy State: " + status.name());
            }
        }
    }
    public static void CreateDataVolume (String diskname) throws IOException, InterruptedException {
        // Create intial Managed Disk from VHD file
        body = "{\n" +
                "    \"location\": \"westeurope\", \n" +
                "        \"tags\": { \n" +
                "        \"organization\": \"TestDS\", \n" +
                "        \"description\": \"Tests creating a full deployment workflow\", \n" +
                "        },\n" +
                "    \"properties\": {\n" +
                "        \"creationData\": { \n" +
                "             \"createOption\": \"Empty\", \n" +
                "            }, \n" +
                "        \"osType\": \"Linux\",\n" +
                "        \"accountType\": \"Premium_LRS\",\n" +
                "        \"diskSizeGB\": \"128\"\n" +
                "        } \n" +
                "}";

        url = "https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourcegroups/testds/providers/Microsoft.Compute/disks/" + diskname + "?api-version=2016-04-30-preview";
        restClient.ExecCall("PUT", url, body);
    }

    public static void AttachDataVolume (String vmname, String diskname, String lun) throws IOException, InterruptedException {

        body="{\n" +
                "    \"location\": \"westeurope\",\n" +
                "    \"properties\": {\n" +
                "        \"storageProfile\": {\n" +
                "            \"dataDisks\": [ \n" +
                "                { \n" +
                "                    \"lun\": \""+lun+"\",  \n" +
                "                    \"name\": \""+diskname+"\", \n" +
                "                    \"managedDisk\": { \n" +
                "                    \"id\": \"/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/disks/"+diskname+"\",\n" +
                "                    \"storageAccountType\": \"Premium_LRS\" \n" +
                "                }, \n" +
                "                \"createOption\": \"attach\" \n" +
                "                },\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}";

        url="https://management.azure.com/subscriptions/e243327e-b18c-4766-8f44-d9a945082e57/resourceGroups/testds/providers/Microsoft.Compute/virtualMachines/" + vmname + "?api-version=2016-04-30-preview";;
        restClient.ExecCall("PUT", url, body);
    }
}
