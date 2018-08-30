package com.assimilation.ellie.assibungee.server;

import com.assimilation.ellie.assibungee.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Ellie on 12/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ServerPing {

    public ServerPingResponse getPing(String hostname) throws IOException {
        return this.getPing(new Options().setHostname(hostname));
    }

    public ServerPingResponse getPing(final Options options) throws IOException {
        PingUtil.validate(options.getHostname(), "Hostname cannot be null.");
        PingUtil.validate(options.getPort(), "Port cannot be null.");

        final Socket socket = new Socket();
        socket.connect(new InetSocketAddress(options.getHostname(), options.getPort()), options.getTimeout());

        final DataInputStream in = new DataInputStream(socket.getInputStream());
        final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        //> Handshake

        ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(handshake_bytes);

        handshake.writeByte(PingUtil.PACKET_HANDSHAKE);
        PingUtil.writeVarInt(handshake, PingUtil.PROTOCOL_VERSION);
        PingUtil.writeVarInt(handshake, options.getHostname().length());
        handshake.writeBytes(options.getHostname());
        handshake.writeShort(options.getPort());
        PingUtil.writeVarInt(handshake, PingUtil.STATUS_HANDSHAKE);

        PingUtil.writeVarInt(out, handshake_bytes.size());
        out.write(handshake_bytes.toByteArray());

        //> Status request

        out.writeByte(0x01); // Size of packet
        out.writeByte(PingUtil.PACKET_STATUSREQUEST);

        //< Status response

        PingUtil.readVarInt(in); // Size
        int id = PingUtil.readVarInt(in);

        PingUtil.io(id == -1, "Server prematurely ended stream.");
        PingUtil.io(id != PingUtil.PACKET_STATUSREQUEST, "Server returned invalid packet.");

        int length = PingUtil.readVarInt(in);
        PingUtil.io(length == -1, "Server prematurely ended stream.");
        PingUtil.io(length == 0, "Server returned unexpected value.");

        byte[] data = new byte[length];
        in.readFully(data);
        String json = new String(data, options.getCharset());

        //> Ping

        out.writeByte(0x09); // Size of packet
        out.writeByte(PingUtil.PACKET_PING);
        out.writeLong(System.currentTimeMillis());

        //< Ping

        PingUtil.readVarInt(in); // Size
        id = PingUtil.readVarInt(in);
        PingUtil.io(id == -1, "Server prematurely ended stream.");

        handshake.close();
        handshake_bytes.close();
        out.close();
        in.close();
        socket.close();

        return Util.getGson().fromJson(json, ServerPingResponse.class);
    }


    public static class Options {

        private String hostname;
        private int port = 25565;
        private int timeout = 2000;
        private String charset = "UTF-8";

        public Options setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Options setPort(int port) {
            this.port = port;
            return this;
        }

        public Options setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Options setCharset(String charset) {
            this.charset = charset;
            return this;
        }

        public String getHostname() {
            return this.hostname;
        }

        public int getPort() {
            return this.port;
        }

        public int getTimeout() {
            return this.timeout;
        }

        public String getCharset() {
            return this.charset;
        }

    }

    private static class PingUtil {

        public static byte PACKET_HANDSHAKE = 0x00, PACKET_STATUSREQUEST = 0x00, PACKET_PING = 0x01;
        public static int PROTOCOL_VERSION = 4;
        public static int STATUS_HANDSHAKE = 1;

        public static void validate(final Object o, final String m){
            if (o == null){
                throw new RuntimeException(m);
            }
        }

        public static void io(final boolean b, final String m) throws IOException {
            if (b){
                throw new IOException(m);
            }
        }

        public static int readVarInt(DataInputStream in) throws IOException {
            int i = 0;
            int j = 0;
            while (true) {
                int k = in.readByte();

                i |= (k & 0x7F) << j++ * 7;

                if (j > 5)
                    throw new RuntimeException("VarInt too big");

                if ((k & 0x80) != 128)
                    break;
            }

            return i;
        }

        public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
            while (true){
                if ((paramInt & 0xFFFFFF80) == 0){
                    out.writeByte(paramInt);
                    return;
                }

                out.writeByte(paramInt & 0x7F | 0x80);
                paramInt >>>= 7;
            }
        }

    }

}
