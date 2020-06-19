package com.ekran.player;

import android.content.Context;
import android.util.Log;

import com.ekran.player.model.Content;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import static com.ekran.player.MainActivity.statusFtp;
import static com.ekran.player.MainActivity.user;

public class FtpLoader {
    FTPClient con = null;

    public void checkNewVersion(String panelName)  {

    }

    private void upLoad(String panelName, String contentType, String fileName, int idFile) {
        con = new FTPClient();
        con.setControlEncoding("UTF-8");
        int port = 4545;
        try {
            con.connect("193.124.58.144", port);
            if (con.login(user.getUsername(), user.getPassword())) {
                con.enterLocalPassiveMode();
                con.setFileType(FTP.BINARY_FILE_TYPE);
                con.setDataTimeout(0);
                String data = "/data/data/com.ekran.player/files/" + fileName + "_ftp";

                InputStream is = con.retrieveFileStream("/" + panelName + "/" + contentType + "/" + fileName);

                OutputStream out = new FileOutputStream(new File(data));
                byte[] bytesIn = new byte[1024];
                int read = 0;
                //con.storeFile( panelName + "/" + contentType + "/" + fileName, is);
                while ((read = is.read(bytesIn)) > 0 ) {
                    out.write( bytesIn, 0, read );
                }
                is.close();
                is = null;
                out.flush();
                if (out!=null) out.close();

                File file = new File(data);
                File newFile = new File("/data/data/com.ekran.player/files/"+ fileName);
                file.renameTo(newFile);
                if (idFile != 0) {
                    Api api = new Api();
                    api.setUploadedFile(idFile);
                }
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadNewVersion(final String panelName) {
        Thread t = new Thread(){
            public void run(){
                FtpLoader ftpLoader = new FtpLoader();
                ftpLoader.upLoad(panelName, "update", "apprelease.apk", 0);
            }
        };
        t.start();
    }

    public void uploadFileV2(final String panelName, final String type, final List<Content> list) {
        if (statusFtp == 0) {
            statusFtp = 1;
            Thread t = new Thread(){
                public void run(){
                    FtpLoader ftpLoader = new FtpLoader();
                    for (Content content: list) {
                        ftpLoader.upLoad(panelName, type, content.getFile_name(), content.getId());
                    }
                    statusFtp = 0;
                }
            };

            t.start();
        }

    }
}
