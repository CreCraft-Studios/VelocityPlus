package com.oneonlybob.docker;

import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.utils.ExceptionUtils;
import com.oneonlybob.docker.network.Headers;
import com.oneonlybob.docker.network.Method;
import com.oneonlybob.docker.network.Response;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.*;

public class UnixClient {
    private final String host;
    private String path;
    private Method method;
    public final Headers headers;
    private String body="";

    File file;

    public UnixClient(String host) throws FileNotFoundException {
        this(host, "");
    }

    /**
     * @param host The host or the path to the file of the socket you wish to connect to
     * @param path The path like in a url. e.g. /containers/create*/
    public UnixClient(String host, String path) throws FileNotFoundException {
        this.host =host;
        this.path=path;
        this.method=Method.GET;

        this.headers=new Headers();
        this.headers.add("Accept", "application/json");
        this.headers.add("User-Agent", "Best-Agent");

        this.file = new File(this.host);
        if (!this.file.exists())
            throw new FileNotFoundException("Can't find file to connect via Unix Socket");
    }

    public void setMethod(Method method) {
        this.method=method;
    }

    public void setPath(String path) {
        this.path=path;
    }

    public String getPath() {
        return this.path;
    }

    public void setBody(String body) {
        this.body=body;
        this.headers.replace("Content-Length", String.valueOf(this.body.length()));
    }

    public void clearBody() {
        this.body="";
        this.headers.remove("Content-Length");
    }

    public Response connect() {
        try (AFUNIXSocket socket = AFUNIXSocket.newInstance()) {

            socket.connect(AFUNIXSocketAddress.of(this.file));

            OutputStream out = socket.getOutputStream();


            StringBuilder request = new StringBuilder();
            request.append(this.method.toString()).append(" ").append(this.path).append(" HTTP/1.1\r\n");
            request.append(this.headers.toString());

            if (!this.body.isEmpty())
                request.append(this.body);

            out.write(request.toString().getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String responseLine = reader.readLine();
            String[] status = responseLine.split(" ");

            Response response = new Response();
            response.statusCode = Integer.parseInt(status[1]);
            response.reason = status[2];

            String line;
            while((line = reader.readLine()) != null && !line.isEmpty()) {//!line.isEmpty is needed to break this cycle at the end of the headers
                int colo = line.indexOf(":");
                if (colo>0) {
                    String name = line.substring(0, colo).trim();
                    String value = line.substring(colo + 1).trim();
                    response.headers.add(name, value);
                }
            }

            if (response.headers.contains("Content-Length")) {
                int length = Integer.parseInt(response.headers.get("Content-Length"));
                char[] body = new char[length];
                int read = reader.read(body, 0, length);
                response.setBody(new String(body, 0, read));
            } else if (response.headers.get("transfer-encoding").equalsIgnoreCase("chunked")) {
                StringBuilder body = new StringBuilder();
                while((line = reader.readLine())!=null) {
                    int chunkSize = Integer.parseInt(line.trim(), 16);
                    if (chunkSize==0)
                        break;

                    char[] bytes = new char[chunkSize];

                    int totalRead = 0;
                    while(totalRead<chunkSize) {
                        int read = reader.read(bytes, totalRead, chunkSize - totalRead);
                        if (read==-1)
                            throw new IOException("Unexpected end of stream");
                        totalRead+=read;
                    }

                    body.append(new String(bytes, 0, totalRead));
                    reader.readLine();
                }
                response.setBody(body.toString().trim());
            } else {
                StringBuilder body = new StringBuilder();
                while((line=reader.readLine())!=null) {
                    body.append(line).append("\n");
                }
                response.setBody(body.toString().trim());
            }

            out.close();
            reader.close();
            if (response.serverError())
                VelocityPlus.get().logger.error("There was an error response from server with code 500: {}", response.reason);

            return response;
        } catch (IOException err) {
            VelocityPlus.get().logger.error("Error with connection");
            ExceptionUtils.printException(err);
            return new Response();
        }
    }
}