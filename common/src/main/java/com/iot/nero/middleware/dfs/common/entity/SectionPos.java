package com.iot.nero.middleware.dfs.common.entity;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/29
 * Time   8:07 AM
 */
public class SectionPos implements Comparable<SectionPos>{
    private Integer sectionIndex;
    private Integer sectionSize;
    private String chunkName;

    public SectionPos() {
    }

    public SectionPos(Integer sectionIndex, Integer sectionSize, String chunkName) {
        this.sectionIndex = sectionIndex;
        this.sectionSize = sectionSize;
        this.chunkName = chunkName;
    }

    public Integer getSectionIndex() {
        return sectionIndex;
    }

    public void setSectionIndex(Integer sectionIndex) {
        this.sectionIndex = sectionIndex;
    }

    public Integer getSectionSize() {
        return sectionSize;
    }

    public void setSectionSize(Integer sectionSize) {
        this.sectionSize = sectionSize;
    }

    public String getChunkName() {
        return chunkName;
    }

    public void setChunkName(String chunkName) {
        this.chunkName = chunkName;
    }

    @Override
    public int compareTo(SectionPos o) {
        int i = this.getSectionSize() - o.getSectionSize();//先按照段大小排序
        if(i == 0){
            return this.sectionIndex - o.getSectionIndex();//如果空闲段大小相同了就按照空闲段起始位置排序
        }
        return i;
    }



    @Override
    public String toString() {
        return "SectionPos{" +
                "sectionIndex=" + sectionIndex +
                ", sectionSize=" + sectionSize +
                ", chunkName='" + chunkName + '\'' +
                '}';
    }

}
