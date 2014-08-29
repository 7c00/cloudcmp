package org.cloudcmp.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;

import org.cloudcmp.Adaptor;
import org.cloudcmp.Service;

public class Iperf3Service extends Service {

    public Iperf3Service(Adaptor adaptor) {
        super(adaptor);
    }

    public String getServiceName() {
        return "Iperf3Service";
    }

    public boolean requiresNetwork() {
        return true;
    }

    public void run(){
        if (!adaptor.hasNetwork()) return;
        // int listenPort = Integer.parseInt(adaptor.configs.get("bw_port"));
        try {
            Runtime.getRuntime().exec("iperf3 -s -D");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
