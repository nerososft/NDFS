package com.iot.nero.middleware.dfs.data.core;

import com.iot.nero.middleware.dfs.common.entity.BlockData;
import com.iot.nero.middleware.dfs.common.exceptions.FileAlreadyExistsInThisChunkException;
import com.iot.nero.middleware.dfs.common.exceptions.ThisChunkIndexIsAlreadyFullException;
import org.junit.Test;
import org.xerial.snappy.Snappy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;


/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/27
 * Time   12:29 PM
 */
public class ChunkManagerTest {

    @Test
    public void createChunk() throws Exception {
        ChunkManager fileService = new ChunkManager();
        try {
            byte[] others = new byte[60];
            for (int i = 0; i < others.length; i++) {
                others[i] = (byte) 0xBB;
            }
            fileService.createChunk("21863941-09e2-427b-8071-0fb561485184", others);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getChunkList() throws Exception {
        ChunkManager fileService = new ChunkManager();
        System.out.println(fileService.getChunkList());
    }

    @Test
    public void writeFileToChunk() throws Exception {
        ChunkManager fileService = new ChunkManager();

        String fileHash1 = "hash1";
        int file1Size = 0;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/neroyang/project/iotcloud/middleware/ndfs/test_data/steps-o.jpg", "r")) {
            FileChannel fileChannel = randomAccessFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(byteBuffer);
            byte[] dataCompress = Snappy.compress(byteBuffer.array());

            file1Size = dataCompress.length;
            BlockData blockData = new BlockData(
                    "21863941-09e2-427b-8071-0fb561485184", "steps", "jpg", 0, fileHash1, (int) fileChannel.size(), "snappy", dataCompress.length,"",false, System.currentTimeMillis(), System.currentTimeMillis()
            );
            //fileService.writeFileToChunk("21863941-09e2-427b-8071-0fb561485184", dataCompress, blockData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileHash2 = "hash2";
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/neroyang/project/iotcloud/middleware/ndfs/test_data/steps_read.jpg", "r")) {
            FileChannel fileChannel = randomAccessFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(byteBuffer);
            byte[] dataCompress = Snappy.compress(byteBuffer.array());
            BlockData blockData = new BlockData(
                    "21863941-09e2-427b-8071-0fb561485184", "steps-o", "jpg", file1Size, fileHash2, (int) fileChannel.size(), "snappy", dataCompress.length,"",false, System.currentTimeMillis(), System.currentTimeMillis()
            );
            fileService.writeFileToChunk("21863941-09e2-427b-8071-0fb561485184", dataCompress, blockData);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FileAlreadyExistsInThisChunkException e) {
            e.printStackTrace();
        } catch (ThisChunkIndexIsAlreadyFullException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void readFileFromChunk() throws Exception {
        ChunkManager fileService = new ChunkManager();

        String fileHash1 = "hash2";

        byte[] fileBytes = new byte[0];
        try {
            fileBytes = fileService.readFileFromChunk("21863941-09e2-427b-8071-0fb561485184", fileHash1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (RandomAccessFile readImg = new RandomAccessFile("/Users/neroyang/project/iotcloud/middleware/ndfs/test_data/steps_read_o.jpg", "rw")) {
            readImg.write(fileBytes);
            readImg.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBlockDataListFromChunk() throws Exception {
        ChunkManager fileService = new ChunkManager();
        try {
            List<BlockData> blockDataList = fileService.getBlockDataListFromChunk("21863941-09e2-427b-8071-0fb561485184");
            System.out.println("文件列表" + blockDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}