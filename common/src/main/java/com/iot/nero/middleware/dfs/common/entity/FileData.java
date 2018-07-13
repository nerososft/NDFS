package com.iot.nero.middleware.dfs.common.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/27
 * Time   8:13 PM
 */
public class FileData implements Serializable {

    private String fileName;
    private String fileType;
    private String fileHash;
    private String chainHash;
    private Integer fileSize;
    private Integer compressedFileSize;
    private String compressMethod;
    private List<Byte> data;


    public FileData() {
    }

    public FileData(String fileName, String fileType, String fileHash, String chainHash, Integer fileSize, Integer compressedFileSize, String compressMethod, List<Byte> data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileHash = fileHash;
        this.chainHash = chainHash;
        this.fileSize = fileSize;
        this.compressedFileSize = compressedFileSize;
        this.compressMethod = compressMethod;
        this.data = data;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public Integer getCompressedFileSize() {
        return compressedFileSize;
    }

    public void setCompressedFileSize(Integer compressedFileSize) {
        this.compressedFileSize = compressedFileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getCompressMethod() {
        return compressMethod;
    }

    public void setCompressMethod(String compressMethod) {
        this.compressMethod = compressMethod;
    }

    public List<Byte> getData() {
        return data;
    }

    public void setData(List<Byte> data) {
        this.data = data;
    }

    public String getChainHash() {
        return chainHash;
    }

    public void setChainHash(String chainHash) {
        this.chainHash = chainHash;
    }

    @Override
    public String toString() {
        return "FileData{" +
                "fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileHash='" + fileHash + '\'' +
                ", chainHash='" + chainHash + '\'' +
                ", fileSize=" + fileSize +
                ", compressedFileSize=" + compressedFileSize +
                ", compressMethod='" + compressMethod + '\'' +
                ", data=" + data +
                '}';
    }
}
