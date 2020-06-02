package com.ekran.player;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.ekran.player.MainActivity.user;

public class FtpLoader {
    FTPClient con = null;

    private void upLoad(String panelName, String contentType, String fileName, int idFile) {
        con = new FTPClient();
        con.setControlEncoding("UTF-8");
        int port = 4545;
        try {
            con.connect("193.124.58.144", port);
            if (con.login(user.getUsername(), user.getPassword())) {
                con.enterLocalPassiveMode();
                con.setFileType(FTP.BINARY_FILE_TYPE);
                String data = "/data/data/com.ekran.player/files/" + fileName + "_ftp";

                InputStream is = con.retrieveFileStream("/" + panelName + "/" + contentType + "/" + fileName);

                OutputStream out = new FileOutputStream(new File(data));
                byte[] bytesIn = new byte[4096];
                int read = 0;
                //con.storeFile( panelName + "/" + contentType + "/" + fileName, is);
                while( ( ( read = is.read( bytesIn ) ) != - 1 )) {
                    out.write( bytesIn, 0, read );
                }
                is.close();
                is = null;
                out.flush();
                out.close();
                out = null;
                File file = new File(data);
                File newFile = new File("/data/data/com.ekran.player/files/"+ fileName);
                file.renameTo(newFile);

                Api api = new Api();
                api.setUploadedFile(idFile);
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(final String panelName, final String contentType, final String fileName, final int idFile) {
        Thread t = new Thread(){
            public void run(){
                FtpLoader ftpLoader = new FtpLoader();
                ftpLoader.upLoad(panelName, contentType, fileName, idFile);
            }
        };
        t.start();
    }
}
