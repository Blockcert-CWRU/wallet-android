package com.learningmachine.android.app.util;

import android.content.Context;

import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import okio.Buffer;
import timber.log.Timber;

public class FileUtils {

    private static final String CERT_DIR = "certs";
    private static final String LOGS_DIR = "logs";
    private static final String JSON_EXT = ".json";
    private static final String LOGS_FILE = "logs.txt";

    public static void saveCertificate(Context context, Buffer buffer, String uuid) {
        File file = getCertificateFile(context, uuid, true);
        writeResponseBodyToDisk(file, buffer);
    }

    public static void saveLogs(Context context, StringBuilder buffer) {
        File file = getLogsFile(context, true);
        writeLogsToDisk(file, buffer);
    }

    public static void copyCertificateStream(Context context, InputStream inputStream, String uuid) {
        File outputFile = getCertificateFile(context, uuid, true);
        copyInputStream(inputStream, outputFile);
    }

    public static boolean deleteCertificate(Context context, String uuid) {
        return getCertificateFile(context, uuid).delete();
    }

    public static void deleteLogs(Context context) {
        getLogsFile(context, false).delete();
    }

    public static void renameCertificateFile(Context context, String oldName, String newName) {
        File oldFile = getCertificateFile(context, oldName);
        File newFile = getCertificateFile(context, newName);
        try {
            Files.move(oldFile, newFile);
        } catch (IOException e) {
            Timber.e(e, "Failed to rename the certificate file");
        }
    }

    public static File getCertificateFile(Context context, String uuid) {
        return getCertificateFile(context, uuid, false);
    }

    public static String getCertificateFileJSON(Context context, String uuid) throws Exception {
        File fl = getCertificateFile(context, uuid);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    private static File getCertificateFile(Context context, String uuid, boolean createDir) {
        File certDir = getCertificateDirectory(context, createDir);
        String filename = uuid + JSON_EXT;
        return new File(certDir, filename);
    }

    private static File getCertificateDirectory(Context context, boolean createDir) {
        File certDir = new File(context.getFilesDir(), CERT_DIR);
        if (createDir) {
            certDir.mkdirs();
        }
        return certDir;
    }

    private static void writeResponseBodyToDisk(File file, Buffer buffer) {
        try (InputStream inputStream = buffer.inputStream();
             OutputStream outputStream = new FileOutputStream(file)) {
            copyStreams(inputStream, outputStream);
        } catch (IOException e) {
            Timber.e(e, "Unable to write ResponseBody to file");
        }
    }

    public static File getLogsFile(Context context, boolean createDir) {
        File logsDir = getLogsDirectory(context, createDir);
        return new File(logsDir, LOGS_FILE);
    }

    private static File getLogsDirectory(Context context, boolean createDir) {
        File certDir = new File(context.getFilesDir(), LOGS_DIR);
        if (createDir) {
            certDir.mkdirs();
        }
        return certDir;
    }

    private static void writeLogsToDisk(File file, StringBuilder buffer) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(buffer.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyInputStream(InputStream inputStream, File outputFile) {
        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            copyStreams(inputStream, outputStream);
        } catch (IOException e) {
            Timber.e(e, "Unable to copy certificate file");
        }
    }

    public static void copyStreams(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096];
        while (true) {
            int read = inputStream.read(buffer);
            if (read == -1) {
                break;
            }
            outputStream.write(buffer, 0, read);
        }
        outputStream.flush();
    }

    public static void appendCharactersToFile(InputStream inputStream, File outputFile) throws IOException {
        try (FileWriter writer = new FileWriter(outputFile, true);
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            int characterCount = 4096;
            char[] buffer = new char[characterCount];
            while (true) {
                int read = reader.read(buffer, 0, characterCount);
                if (read == -1) {
                    break;
                }
                writer.write(buffer, 0, read);
            }
            writer.flush();
        } catch (IOException ioe) {
            Timber.e("Failed to append to file " + outputFile.getName());
            throw ioe;
        }
    }

    public static void appendStringToFile(String string, File outputFile) throws IOException {
        Timber.i("Last 5 characters are" + string.substring(string.length() - 5));
        FileWriter writer = new FileWriter(outputFile, true);
        writer.write(string);
        writer.flush();
    }


    public static void writeStringToFile(String string, String outputPath) throws IOException {
        File outputFile = new File(outputPath);
        FileWriter writer = new FileWriter(outputFile, false);
        writer.write(string);
        writer.flush();
    }


    public static void copyAssetFile(Context appContext, String assetFilePath, String destinationFilePath) throws IOException {
        InputStream in = appContext.getAssets().open(assetFilePath);
        OutputStream out = new FileOutputStream(appContext.getFilesDir() + "/" + destinationFilePath);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);
        in.close();
        out.close();
    }


    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

}
