package com.iot.nero.middleware.dfs.index.core;

import com.iot.nero.middleware.dfs.common.cluster.discovery.NodeDiscovery;
import com.iot.nero.middleware.dfs.common.entity.DataPosition;
import com.iot.nero.middleware.dfs.common.cluster.node.entity.DataNode;
import com.iot.nero.middleware.dfs.common.exceptions.SaveFileTransactionPrepareFailedException;
import com.iot.nero.middleware.dfs.common.exceptions.SaveFileTransactionStartFailedException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.iot.nero.middleware.dfs.index.constant.CONSTANT.SAVE_FILE_TRANSACTION_PREPARE_FAILED;
import static com.iot.nero.middleware.dfs.index.constant.CONSTANT.SAVE_FILE_TRANSACTION_START_FAILED;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/21
 * Time   8:33 AM
 */
public class FileManager {

    AtomicInteger prepareTransactionSuccessCount;
    AtomicInteger startTransactionSuccessCount;
    AtomicInteger commitTransactionSuccessCount;
    AtomicInteger rollbackTransactionSuccessCount;


    public synchronized DataPosition delFile(String fileHash,DataNode dataNode,final DataPosition dataPosition) throws InterruptedException, SaveFileTransactionStartFailedException, SaveFileTransactionPrepareFailedException, IOException {
        return null;
    }


    public synchronized DataPosition insertFile(DataNode masterNode, final DataPosition dataPosition, final byte[] file) throws InterruptedException, SaveFileTransactionStartFailedException, SaveFileTransactionPrepareFailedException, IOException, NoSuchMethodException, IllegalAccessException, InstantiationException {

        AtomicInteger successCounter = new AtomicInteger(0);
        NodeDiscovery nodeDiscovery = new NodeDiscovery();
        List<DataNode> dataNodeList  = nodeDiscovery.getSlaveNode(masterNode); // 数据存储Master节点Slave节点列表(>=3)
        dataNodeList.add(masterNode);
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // 预请求, 数据量比正式请求少一点, 失败的成本低
        final CountDownLatch prepareTransactionCountDownLatch = new CountDownLatch(dataNodeList.size());
        prepareTransactionSuccessCount = new AtomicInteger(0);
        for (final DataNode dataNode : dataNodeList) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    saveFileTransactionPrepare(dataNode, prepareTransactionCountDownLatch);
                }
            });
        }
        prepareTransactionCountDownLatch.await();
        // 预请求失败
        if (prepareTransactionSuccessCount.get() < dataNodeList.size()) {
            throw new SaveFileTransactionPrepareFailedException(SAVE_FILE_TRANSACTION_PREPARE_FAILED);
        }

        // 1pc 文件写入事务请求
        startTransactionSuccessCount = new AtomicInteger(0);
        final CountDownLatch transactionCountDownLatch = new CountDownLatch(dataNodeList.size());
        for (final DataNode dataNode : dataNodeList) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    saveFileTransactionStart(dataNode, dataPosition, file, transactionCountDownLatch);
                }
            });
        }
        transactionCountDownLatch.await();

        // 写入事务失败,回滚
        final CountDownLatch transactionRollbackCountDownLatch = new CountDownLatch(dataNodeList.size());
        if (startTransactionSuccessCount.get() < dataNodeList.size()) {
            for (final DataNode dataNode : dataNodeList) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        saveFileTransactionRollback(dataNode, dataPosition, transactionRollbackCountDownLatch);
                    }
                });
            }
            transactionRollbackCountDownLatch.await();
            throw new SaveFileTransactionStartFailedException(SAVE_FILE_TRANSACTION_START_FAILED);
        } // 要是有一个节点回滚失败了怎么办？？？？

        // 2pc 文件写入事务提交请求
        final CountDownLatch transactionCommitCountDownLatch = new CountDownLatch(dataNodeList.size());
        for (final DataNode dataNode : dataNodeList) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    saveFileTransactionCommit(dataNode, dataPosition, transactionCommitCountDownLatch);
                }
            });
        } // 要是有一个节点提交失败了怎么办？？？
        transactionCommitCountDownLatch.await();

        // 缓存索引
        // fileIndex.getRoot().insertOrUpdate(fileHash,dataPosition,fileIndex);

        // 向其他索引节点同步索引事务请求

        // 向其他索引节点发送索引生效,提交事务

        // 收到储存响应

        // 给客户端返回
        return dataPosition;
    }

    private boolean saveFileTransactionPrepare(DataNode dataNode, CountDownLatch countDownLatch) {


        // 若收到响应
        countDownLatch.countDown();
        prepareTransactionSuccessCount.incrementAndGet();

        return true;
    }

    private boolean saveFileTransactionStart(DataNode dataNode, DataPosition dataPosition, byte[] file, CountDownLatch countDownLatch) {

        // 若收到响应
        countDownLatch.countDown();
        startTransactionSuccessCount.incrementAndGet();


        // 超时断链
        return true;
    }

    private boolean saveFileTransactionRollback(DataNode dataNode, DataPosition dataPosition, CountDownLatch countDownLatch) {

        // 若收到响应
        countDownLatch.countDown();
        rollbackTransactionSuccessCount.incrementAndGet();

        return true;
    }

    private boolean saveFileTransactionCommit(DataNode dataNode, DataPosition dataPosition, CountDownLatch countDownLatch) {

        // 若收到响应
        countDownLatch.countDown();
        commitTransactionSuccessCount.incrementAndGet();

        return true;
    }
}
