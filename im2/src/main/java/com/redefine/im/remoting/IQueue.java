package com.redefine.im.remoting;

public interface IQueue extends Comparable<IQueue> {

    String getName();

    byte[] blpop(int timeout);

    Long rpush(byte[] value);

    void expired();

    boolean isExpired();

}
