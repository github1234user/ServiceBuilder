package com.microsoft.azure.provisioningengine;

import com.microsoft.azure.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import okhttp3.logging.HttpLoggingInterceptor;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RestClient {

    private static com.microsoft.azure.RestClient restClient;
    static DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy K:mm:ss a");
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String key = System.getenv("secret");
    private static final String id = System.getenv("client-id");
    private static final String tenant = System.getenv("domain");
    private static ApplicationTokenCredentials credentials;

    public static String ExecCall(String method, String url, String strbody) throws IOException, InterruptedException {
        Response res = null;
        Request req = null;
        String reqid = "";
        String retryAfter = "";
        String asyncOp = "";
        MediaType JSON;
        RequestBody body;

        Authenticate();

        // prepare the request
        switch (method) {
            case "GET":
                req = new Request
                        .Builder()
                        .get()
                        .url(url)
                        .build();
                break;
            case "PUT":
                JSON = MediaType.parse("application/json; charset=utf-8");
                body = RequestBody.create(JSON, strbody);
                req = new Request
                        .Builder()
                        .put(body)
                        .url(url)
                        .build();
                break;
            case "POST":
                JSON = MediaType.parse("application/json; charset=utf-8");
                body = RequestBody.create(JSON, strbody);
                req = new Request
                        .Builder()
                        .post(body)
                        .url(url)
                        .build();
                break;
            case "DELETE":
                req = new Request
                        .Builder()
                        .delete()
                        .url(url)
                        .build();
                break;
        }
        // execute the request
        res = restClient.httpClient().newCall(req).execute();

        if (res.code() == 200 || res.code() == 201 || res.code() == 202) {

            // It's a long running operation
            if (!res.headers().values("Azure-AsyncOperation").isEmpty()) {

                // check the URL to poll for completion
                asyncOp = res.headers().values("Azure-AsyncOperation").get(0);
                reqid = res.headers().values("x-ms-request-id").get(0);

                // check if a retry-after interval is specified
                if (!res.headers().values("Retry-After").isEmpty()) {
                    retryAfter = res.headers().values("Retry-After").get(0);
                } else {
                    retryAfter = "5";
                }

                boolean cont = true;

                // Wait for "Retry-After" interval and re-check
                while (cont) {

                    Thread.sleep(Integer.parseInt(retryAfter) * 1000);

                    req = new Request
                            .Builder()
                            .get()
                            .url(asyncOp)
                            .build();

                    res = restClient.httpClient().newCall(req).execute();

                    String out = res.body().string();

                    // if the operation is still ongoing
                    if (!res.headers().values("Azure-AsyncOperation").isEmpty() || out.contains("InProgress")) {
                        cont = true;
                        log.info(dateFormat.format(new Date()) + " LongRunningOperationCheck RequestId: " + reqid + " AsyncOperation: " + asyncOp + " Retry-After: " + retryAfter);
                    } else
                    {
                        log.info(dateFormat.format(new Date()) + " LongRunningOperationEnded RequestId: " + reqid + " AsyncOperation: " + asyncOp + " Retry-After: " + retryAfter);
                        return out;
                    }
                }
            }
        }
        return null;
    }

    public static void Authenticate() {
        credentials = new ApplicationTokenCredentials(
                id,
                tenant,
                key,
                AzureEnvironment.AZURE);

        restClient = new com.microsoft.azure.RestClient.Builder()
                .withBaseUrl(AzureEnvironment.AZURE.getBaseUrl())
                .withCredentials(credentials)
                .withLogLevel(HttpLoggingInterceptor.Level.BODY)
                .build();
    }
}
