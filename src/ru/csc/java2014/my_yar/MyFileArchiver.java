package ru.csc.java2014.my_yar;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;

public class MyFileArchiver {
    private DataOutputStream archOutStream;
    private DataInputStream archInStream;

    private static final int BUFFER_SIZE = 8192;
    private File archive;

    public MyFileArchiver(File archive) {
        this.archive = archive;
    }

    public MyFileArchiver() {
    }

    public void pack(File dir) throws IOException {
        if (archive != null)
            archOutStream = new DataOutputStream(new FileOutputStream(archive));
        walkFileTreeRecursively(dir, dir);
        //writeEndOfStreamMarker();
        archOutStream.close();
    }

    public void unpack(File dir) throws IOException {
        if (archive != null)
            archInStream = new DataInputStream(new FileInputStream(archive));
        boolean EOS = false;

        try {
            while (!EOS) {
                String filePath = archInStream.readUTF();
                String dirPath = dir.getAbsolutePath();

                File newFile = createNewFile(filePath, dirPath);
                //assignFileAttributes(newFile);
                EOS = fillFileWithData2(newFile);
            }
        } catch (EOFException e) {
            System.out.println("EOF is reached");
        }

        archInStream.close();
    }

    private void walkFileTreeRecursively(File baseDir, File dir) throws IOException {

        if (dir.isFile()) {
            BasicFileAttributes fileAttrs = Files.readAttributes(dir.toPath(), BasicFileAttributes.class);
            addFile(dir, baseDir, fileAttrs);
            return;
        }

        for (int i = 0; i < dir.list().length; i++) {
            walkFileTreeRecursively(baseDir, dir.listFiles()[i]);
        }
    }

    private void addFile(File file, File baseDir, BasicFileAttributes fileAttrs) throws IOException {
        archOutStream.writeUTF((baseDir.toURI().relativize(file.toURI())).toString());
        //archOutStream.writeLong(fileAttrs.creationTime().toMillis());
        //archOutStream.writeLong(fileAttrs.lastModifiedTime().toMillis());
        //archOutStream.writeUTF("File name: " + (baseDir.toURI().relativize(file.toURI())).toString() + "  ");
        //archOutStream.writeUTF("File creation time: " + fileAttrs.creationTime().toMillis() + "  ");
        //archOutStream.writeUTF("File last mod time: " + fileAttrs.lastModifiedTime().toMillis() + "  ");

        long start = System.nanoTime();

        writeFileDataToStream2(file);

        long estimatedTime = System.nanoTime() - start;
        //System.out.println("length= " + file.length());
        //System.out.println("Wrote " + file.length() * 1_000_000L / estimatedTime + " KB/s");
    }

    private void writeFileDataToStream(File file) throws IOException {
        FileInputStream fileInStream = new FileInputStream(file);
        int fileData = 0;
        long fileLength = file.length();

        archOutStream.writeUTF("File length: ");
        archOutStream.writeLong(fileLength);
        archOutStream.writeUTF("  File data: ");

        while ((fileData = fileInStream.read()) != -1) {
            archOutStream.writeByte(fileData);
        }
        archOutStream.writeChar('\n');
    }

    private void writeFileDataToStream2(File file) throws IOException {
        BufferedInputStream fileInStream = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream buffArchOutStream = new BufferedOutputStream(archOutStream);
        byte[] buffer = new byte[BUFFER_SIZE];
        int n = 0;
        long nread = 0;
        long fileLength = file.length();

        //archOutStream.writeUTF("File length: ");
        //archOutStream.writeLong(fileLength);
        //archOutStream.writeUTF("  File data: ");

        while ((n = fileInStream.read(buffer)) > 0) {
            archOutStream.writeInt(n);
            buffArchOutStream.write(buffer, 0, n);
            nread += n;
        }

        buffArchOutStream.flush();
        writeEndOfStreamMarker();
        System.out.println("nread= " + nread);
        //archOutStream.writeChar('\n');
        //fileInStream.close();
        //buffArchOutStream.close();
    }

    private void writeEndOfStreamMarker() throws IOException {
        archOutStream.writeInt(0);
    }

    private File createNewFile(String filePath, String dirPath) throws IOException {
        String absoluteFilePath = dirPath.concat("/".concat(filePath));
        File newFile = new File(absoluteFilePath);
        File fileParentDir = newFile.getParentFile();

        fileParentDir.mkdirs();
        newFile.createNewFile();

        return newFile;
    }

    private void assignFileAttributes(File file) throws IOException {
        long lastModTime = archInStream.readLong();
        file.setLastModified(lastModTime);
    }

    private boolean fillFileWithData(File file) throws IOException {
        BufferedOutputStream fileOutStream = new BufferedOutputStream(new FileOutputStream(file));
        BufferedInputStream buffArchInStream = new BufferedInputStream(archInStream);
        byte[] buffer = new byte[BUFFER_SIZE];
        int n = 0;
        long nread = 0L;
        int fileLength = 0;
        boolean EOS = false;
        boolean EOF = false;

        while (!EOF && !EOS) {
            fileLength = archInStream.readInt();
            if ((n = buffArchInStream.read(buffer, 0, fileLength)) > 0) {
                fileOutStream.write(buffer, 0, n);
                nread += n;
            } else if (n == 0)
                EOF = true;
            else
                EOS = true;
        }

        fileOutStream.flush();

        //fileOutStream.close();
        //buffArchInStream.close();

        return EOS;
    }

    private boolean fillFileWithData2(File file) throws IOException {
        BufferedOutputStream fileOutStream = new BufferedOutputStream(new FileOutputStream(file));
        //BufferedInputStream buffArchInStream = new BufferedInputStream(archInStream);
        byte[] buffer = new byte[BUFFER_SIZE];
        int n = 0;
        long nread = 0L;
        int fileLength = 0;
        boolean EOS = false;
        boolean EOF = false;

        while (!EOF && !EOS) {
            fileLength = archInStream.readInt();
            if ((n = archInStream.read(buffer, 0, fileLength)) > 0) {
                fileOutStream.write(buffer, 0, n);
                nread += n;
            } else
                EOF = true;
        }

        fileOutStream.flush();

        fileOutStream.close();
        //buffArchInStream.close();

        return EOS;
    }

    public void setArchOutStream(OutputStream archOutStream) {
        this.archOutStream = new DataOutputStream(archOutStream);
    }

    public void setArchInStream(InputStream archInStream) {
        this.archInStream = new DataInputStream(archInStream);
    }
}