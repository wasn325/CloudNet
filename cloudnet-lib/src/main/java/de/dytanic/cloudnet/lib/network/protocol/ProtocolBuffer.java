/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by Tareko on 09.09.2017.
 */
public final class ProtocolBuffer extends ByteBuf implements Cloneable {

    private final ByteBuf byteBuf;

    public ProtocolBuffer(final ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    @Override
    public ProtocolBuffer clone() {
        return new ProtocolBuffer(Unpooled.buffer(byteBuf.readableBytes()).writeBytes(byteBuf));
    }

    public int readVarInt() {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = readByte();
            final int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public ProtocolBuffer writeVarInt(int value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            writeByte(temp);
        } while (value != 0);
        return this;
    }

    public void writeString(final String write) {
        final byte[] values = write.getBytes(StandardCharsets.UTF_8);
        writeVarLong(values.length);
        writeBytes(values);
    }

    public ProtocolBuffer writeVarLong(long value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            writeByte(temp);
        } while (value != 0);
        return this;
    }

    public String readString() {
        final int integer = (int) readVarLong();

        final byte[] buffer = new byte[integer];
        byteBuf.readBytes(buffer, 0, integer);

        return new String(buffer, StandardCharsets.UTF_8);
    }

    public long readVarLong() {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = readByte();
            final int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    @Override
    public int capacity() {
        return byteBuf.capacity();
    }

    @Override
    public ByteBuf capacity(final int i) {
        return byteBuf.capacity(i);
    }

    @Override
    public int maxCapacity() {
        return byteBuf.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return byteBuf.alloc();
    }

    @Override
    public ByteOrder order() {
        return byteBuf.order();
    }

    @Override
    public ByteBuf order(final ByteOrder byteOrder) {
        return byteBuf.order(byteOrder);
    }

    @Override
    public ByteBuf unwrap() {
        return byteBuf.unwrap();
    }

    @Override
    public boolean isDirect() {
        return byteBuf.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return byteBuf.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return byteBuf.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return byteBuf.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(final int i) {
        return byteBuf.readerIndex(i);
    }

    @Override
    public int writerIndex() {
        return byteBuf.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(final int i) {
        return byteBuf.writerIndex(i);
    }

    @Override
    public ByteBuf setIndex(final int i, final int i1) {
        return byteBuf.setIndex(i, i1);
    }

    @Override
    public int readableBytes() {
        return byteBuf.readableBytes();
    }

    @Override
    public int writableBytes() {
        return byteBuf.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return byteBuf.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return byteBuf.isReadable();
    }

    @Override
    public boolean isReadable(final int i) {
        return byteBuf.isReadable(i);
    }

    @Override
    public boolean isWritable() {
        return byteBuf.isWritable();
    }

    @Override
    public boolean isWritable(final int i) {
        return byteBuf.isWritable(i);
    }

    @Override
    public ByteBuf clear() {
        return byteBuf.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return byteBuf.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return byteBuf.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return byteBuf.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return byteBuf.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return byteBuf.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return byteBuf.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(final int i) {
        return byteBuf.ensureWritable(i);
    }

    @Override
    public int ensureWritable(final int i, final boolean b) {
        return byteBuf.ensureWritable(i, b);
    }

    @Override
    public boolean getBoolean(final int i) {
        return byteBuf.getBoolean(i);
    }

    @Override
    public byte getByte(final int i) {
        return byteBuf.getByte(i);
    }

    @Override
    public short getUnsignedByte(final int i) {
        return byteBuf.getUnsignedByte(i);
    }

    @Override
    public short getShort(final int i) {
        return byteBuf.getShort(i);
    }

    @Override
    public short getShortLE(final int i) {
        return byteBuf.getShortLE(i);
    }

    @Override
    public int getUnsignedShort(final int i) {
        return byteBuf.getUnsignedShort(i);
    }

    @Override
    public int getUnsignedShortLE(final int i) {
        return byteBuf.getUnsignedShortLE(i);
    }

    @Override
    public int getMedium(final int i) {
        return byteBuf.getMedium(i);
    }

    @Override
    public int getMediumLE(final int i) {
        return byteBuf.getMediumLE(i);
    }

    @Override
    public int getUnsignedMedium(final int i) {
        return byteBuf.getUnsignedMedium(i);
    }

    @Override
    public int getUnsignedMediumLE(final int i) {
        return byteBuf.getUnsignedMediumLE(i);
    }

    @Override
    public int getInt(final int i) {
        return byteBuf.getInt(i);
    }

    @Override
    public int getIntLE(final int i) {
        return byteBuf.getIntLE(i);
    }

    @Override
    public long getUnsignedInt(final int i) {
        return byteBuf.getUnsignedInt(i);
    }

    @Override
    public long getUnsignedIntLE(final int i) {
        return byteBuf.getUnsignedIntLE(i);
    }

    @Override
    public long getLong(final int i) {
        return byteBuf.getLong(i);
    }

    @Override
    public long getLongLE(final int i) {
        return byteBuf.getLongLE(i);
    }

    @Override
    public char getChar(final int i) {
        return byteBuf.getChar(i);
    }

    @Override
    public float getFloat(final int i) {
        return byteBuf.getFloat(i);
    }

    @Override
    public double getDouble(final int i) {
        return byteBuf.getDouble(i);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuf byteBuf) {
        return this.byteBuf.getBytes(i, byteBuf);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuf byteBuf, final int i1) {
        return this.byteBuf.getBytes(i, byteBuf, i1);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuf byteBuf, final int i1, final int i2) {
        return this.byteBuf.getBytes(i, byteBuf, i1, i2);
    }

    @Override
    public ByteBuf getBytes(final int i, final byte[] bytes) {
        return byteBuf.getBytes(i, bytes);
    }

    @Override
    public ByteBuf getBytes(final int i, final byte[] bytes, final int i1, final int i2) {
        return byteBuf.getBytes(i, bytes, i1, i2);
    }

    @Override
    public ByteBuf getBytes(final int i, final ByteBuffer byteBuffer) {
        return byteBuf.getBytes(i, byteBuffer);
    }

    @Override
    public ByteBuf getBytes(final int i, final OutputStream outputStream, final int i1) throws IOException {
        return byteBuf.getBytes(i, outputStream, i1);
    }

    @Override
    public int getBytes(final int i, final GatheringByteChannel gatheringByteChannel, final int i1) throws IOException {
        return byteBuf.getBytes(i, gatheringByteChannel, i1);
    }

    @Override
    public int getBytes(final int i, final FileChannel fileChannel, final long l, final int i1) throws IOException {
        return byteBuf.getBytes(i, fileChannel, l, i1);
    }

    @Override
    public CharSequence getCharSequence(final int i, final int i1, final Charset charset) {
        return byteBuf.getCharSequence(i, i1, charset);
    }

    @Override
    public ByteBuf setBoolean(final int i, final boolean b) {
        return byteBuf.setBoolean(i, b);
    }

    @Override
    public ByteBuf setByte(final int i, final int i1) {
        return byteBuf.setByte(i, i1);
    }

    @Override
    public ByteBuf setShort(final int i, final int i1) {
        return byteBuf.setShort(i, i1);
    }

    @Override
    public ByteBuf setShortLE(final int i, final int i1) {
        return byteBuf.setShortLE(i, i1);
    }

    @Override
    public ByteBuf setMedium(final int i, final int i1) {
        return byteBuf.setMedium(i, i1);
    }

    @Override
    public ByteBuf setMediumLE(final int i, final int i1) {
        return byteBuf.setMediumLE(i, i1);
    }

    @Override
    public ByteBuf setInt(final int i, final int i1) {
        return byteBuf.setInt(i, i1);
    }

    @Override
    public ByteBuf setIntLE(final int i, final int i1) {
        return byteBuf.setIntLE(i, i1);
    }

    @Override
    public ByteBuf setLong(final int i, final long l) {
        return byteBuf.setLong(i, l);
    }

    @Override
    public ByteBuf setLongLE(final int i, final long l) {
        return byteBuf.setLongLE(i, l);
    }

    @Override
    public ByteBuf setChar(final int i, final int i1) {
        return byteBuf.setChar(i, i1);
    }

    @Override
    public ByteBuf setFloat(final int i, final float v) {
        return byteBuf.setFloat(i, v);
    }

    @Override
    public ByteBuf setDouble(final int i, final double v) {
        return byteBuf.setDouble(i, v);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuf byteBuf) {
        return this.byteBuf.setBytes(i, byteBuf);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuf byteBuf, final int i1) {
        return this.byteBuf.setBytes(i, byteBuf, i1);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuf byteBuf, final int i1, final int i2) {
        return this.byteBuf.setBytes(i, byteBuf, i1, i2);
    }

    @Override
    public ByteBuf setBytes(final int i, final byte[] bytes) {
        return byteBuf.setBytes(i, bytes);
    }

    @Override
    public ByteBuf setBytes(final int i, final byte[] bytes, final int i1, final int i2) {
        return byteBuf.setBytes(i, bytes, i1, i2);
    }

    @Override
    public ByteBuf setBytes(final int i, final ByteBuffer byteBuffer) {
        return byteBuf.setBytes(i, byteBuffer);
    }

    @Override
    public int setBytes(final int i, final InputStream inputStream, final int i1) throws IOException {
        return byteBuf.setBytes(i, inputStream, i1);
    }

    @Override
    public int setBytes(final int i, final ScatteringByteChannel scatteringByteChannel, final int i1) throws IOException {
        return byteBuf.setBytes(i, scatteringByteChannel, i1);
    }

    @Override
    public int setBytes(final int i, final FileChannel fileChannel, final long l, final int i1) throws IOException {
        return byteBuf.setBytes(i, fileChannel, l, i1);
    }

    @Override
    public ByteBuf setZero(final int i, final int i1) {
        return byteBuf.setZero(i, i1);
    }

    @Override
    public int setCharSequence(final int i, final CharSequence charSequence, final Charset charset) {
        return byteBuf.setCharSequence(i, charSequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return byteBuf.readBoolean();
    }

    @Override
    public byte readByte() {
        return byteBuf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return byteBuf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return byteBuf.readShort();
    }

    @Override
    public short readShortLE() {
        return byteBuf.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return byteBuf.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return byteBuf.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return byteBuf.readMedium();
    }

    @Override
    public int readMediumLE() {
        return byteBuf.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return byteBuf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return byteBuf.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return byteBuf.readInt();
    }

    @Override
    public int readIntLE() {
        return byteBuf.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return byteBuf.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return byteBuf.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return byteBuf.readLong();
    }

    @Override
    public long readLongLE() {
        return byteBuf.readLongLE();
    }

    @Override
    public char readChar() {
        return byteBuf.readChar();
    }

    @Override
    public float readFloat() {
        return byteBuf.readFloat();
    }

    @Override
    public double readDouble() {
        return byteBuf.readDouble();
    }

    @Override
    public ByteBuf readBytes(final int i) {
        return byteBuf.readBytes(i);
    }

    @Override
    public ByteBuf readSlice(final int i) {
        return byteBuf.readSlice(i);
    }

    @Override
    public ByteBuf readRetainedSlice(final int i) {
        return byteBuf.readRetainedSlice(i);
    }

    @Override
    public ByteBuf readBytes(final ByteBuf byteBuf) {
        return this.byteBuf.readBytes(byteBuf);
    }

    @Override
    public ByteBuf readBytes(final ByteBuf byteBuf, final int i) {
        return this.byteBuf.readBytes(byteBuf, i);
    }

    @Override
    public ByteBuf readBytes(final ByteBuf byteBuf, final int i, final int i1) {
        return this.byteBuf.readBytes(byteBuf, i, i1);
    }

    @Override
    public ByteBuf readBytes(final byte[] bytes) {
        return byteBuf.readBytes(bytes);
    }

    @Override
    public ByteBuf readBytes(final byte[] bytes, final int i, final int i1) {
        return byteBuf.readBytes(bytes, i, i1);
    }

    @Override
    public ByteBuf readBytes(final ByteBuffer byteBuffer) {
        return byteBuf.readBytes(byteBuffer);
    }

    @Override
    public ByteBuf readBytes(final OutputStream outputStream, final int i) throws IOException {
        return byteBuf.readBytes(outputStream, i);
    }

    @Override
    public int readBytes(final GatheringByteChannel gatheringByteChannel, final int i) throws IOException {
        return byteBuf.readBytes(gatheringByteChannel, i);
    }

    @Override
    public CharSequence readCharSequence(final int i, final Charset charset) {
        return byteBuf.readCharSequence(i, charset);
    }

    @Override
    public int readBytes(final FileChannel fileChannel, final long l, final int i) throws IOException {
        return byteBuf.readBytes(fileChannel, l, i);
    }

    @Override
    public ByteBuf skipBytes(final int i) {
        return byteBuf.skipBytes(i);
    }

    @Override
    public ByteBuf writeBoolean(final boolean b) {
        return byteBuf.writeBoolean(b);
    }

    @Override
    public ByteBuf writeByte(final int i) {
        return byteBuf.writeByte(i);
    }

    @Override
    public ByteBuf writeShort(final int i) {
        return byteBuf.writeShort(i);
    }

    @Override
    public ByteBuf writeShortLE(final int i) {
        return byteBuf.writeShortLE(i);
    }

    @Override
    public ByteBuf writeMedium(final int i) {
        return byteBuf.writeMedium(i);
    }

    @Override
    public ByteBuf writeMediumLE(final int i) {
        return byteBuf.writeMediumLE(i);
    }

    @Override
    public ByteBuf writeInt(final int i) {
        return byteBuf.writeInt(i);
    }

    @Override
    public ByteBuf writeIntLE(final int i) {
        return byteBuf.writeIntLE(i);
    }

    @Override
    public ByteBuf writeLong(final long l) {
        return byteBuf.writeLong(l);
    }

    @Override
    public ByteBuf writeLongLE(final long l) {
        return byteBuf.writeLongLE(l);
    }

    @Override
    public ByteBuf writeChar(final int i) {
        return byteBuf.writeChar(i);
    }

    @Override
    public ByteBuf writeFloat(final float v) {
        return byteBuf.writeFloat(v);
    }

    @Override
    public ByteBuf writeDouble(final double v) {
        return byteBuf.writeDouble(v);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuf byteBuf) {
        return this.byteBuf.writeBytes(byteBuf);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuf byteBuf, final int i) {
        return this.byteBuf.writeBytes(byteBuf, i);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuf byteBuf, final int i, final int i1) {
        return this.byteBuf.writeBytes(byteBuf, i, i1);
    }

    @Override
    public ByteBuf writeBytes(final byte[] bytes) {
        return byteBuf.writeBytes(bytes);
    }

    @Override
    public ByteBuf writeBytes(final byte[] bytes, final int i, final int i1) {
        return byteBuf.writeBytes(bytes, i, i1);
    }

    @Override
    public ByteBuf writeBytes(final ByteBuffer byteBuffer) {
        return byteBuf.writeBytes(byteBuffer);
    }

    @Override
    public int writeBytes(final InputStream inputStream, final int i) throws IOException {
        return byteBuf.writeBytes(inputStream, i);
    }

    @Override
    public int writeBytes(final ScatteringByteChannel scatteringByteChannel, final int i) throws IOException {
        return byteBuf.writeBytes(scatteringByteChannel, i);
    }

    @Override
    public int writeBytes(final FileChannel fileChannel, final long l, final int i) throws IOException {
        return byteBuf.writeBytes(fileChannel, l, i);
    }

    @Override
    public ByteBuf writeZero(final int i) {
        return byteBuf.writeZero(i);
    }

    @Override
    public int writeCharSequence(final CharSequence charSequence, final Charset charset) {
        return byteBuf.writeCharSequence(charSequence, charset);
    }

    @Override
    public int indexOf(final int i, final int i1, final byte b) {
        return byteBuf.indexOf(i, i1, b);
    }

    @Override
    public int bytesBefore(final byte b) {
        return byteBuf.bytesBefore(b);
    }

    @Override
    public int bytesBefore(final int i, final byte b) {
        return byteBuf.bytesBefore(i, b);
    }

    @Override
    public int bytesBefore(final int i, final int i1, final byte b) {
        return byteBuf.bytesBefore(i, i1, b);
    }

    @Override
    public int forEachByte(final ByteProcessor byteProcessor) {
        return byteBuf.forEachByte(byteProcessor);
    }

    @Override
    public int forEachByte(final int i, final int i1, final ByteProcessor byteProcessor) {
        return byteBuf.forEachByte(i, i1, byteProcessor);
    }

    @Override
    public int forEachByteDesc(final ByteProcessor byteProcessor) {
        return byteBuf.forEachByteDesc(byteProcessor);
    }

    @Override
    public int forEachByteDesc(final int i, final int i1, final ByteProcessor byteProcessor) {
        return byteBuf.forEachByteDesc(i, i1, byteProcessor);
    }

    @Override
    public ByteBuf copy() {
        return byteBuf.copy();
    }

    @Override
    public ByteBuf copy(final int i, final int i1) {
        return byteBuf.copy(i, i1);
    }

    @Override
    public ByteBuf slice() {
        return byteBuf.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return byteBuf.retainedSlice();
    }

    @Override
    public ByteBuf slice(final int i, final int i1) {
        return byteBuf.slice(i, i1);
    }

    @Override
    public ByteBuf retainedSlice(final int i, final int i1) {
        return byteBuf.retainedSlice(i, i1);
    }

    @Override
    public ByteBuf duplicate() {
        return byteBuf.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return byteBuf.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return byteBuf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return byteBuf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(final int i, final int i1) {
        return byteBuf.nioBuffer(i, i1);
    }

    @Override
    public ByteBuffer internalNioBuffer(final int i, final int i1) {
        return byteBuf.internalNioBuffer(i, i1);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return byteBuf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(final int i, final int i1) {
        return byteBuf.nioBuffers(i, i1);
    }

    @Override
    public boolean hasArray() {
        return byteBuf.hasArray();
    }

    @Override
    public byte[] array() {
        return byteBuf.array();
    }

    @Override
    public int arrayOffset() {
        return byteBuf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return byteBuf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return byteBuf.memoryAddress();
    }

    @Override
    public String toString(final Charset charset) {
        return byteBuf.toString(charset);
    }

    @Override
    public String toString(final int i, final int i1, final Charset charset) {
        return byteBuf.toString(i, i1, charset);
    }

    @Override
    public int hashCode() {
        return byteBuf.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return byteBuf.equals(o);
    }

    @Override
    public int compareTo(final ByteBuf byteBuf) {
        return byteBuf.compareTo(byteBuf);
    }

    @Override
    public String toString() {
        return byteBuf.toString();
    }

    @Override
    public ByteBuf retain(final int i) {
        return byteBuf.retain(i);
    }

    @Override
    public ByteBuf retain() {
        return byteBuf.retain();
    }

    @Override
    public ByteBuf touch() {
        return byteBuf.touch();
    }

    @Override
    public ByteBuf touch(final Object o) {
        return byteBuf.touch(o);
    }

    public int forEachByte(final ByteBufProcessor byteBufProcessor) {
        return byteBuf.forEachByte(byteBufProcessor);
    }

    public int forEachByte(final int i, final int i1, final ByteBufProcessor byteBufProcessor) {
        return byteBuf.forEachByte(i, i1, byteBufProcessor);
    }

    public int forEachByteDesc(final ByteBufProcessor byteBufProcessor) {
        return byteBuf.forEachByteDesc(byteBufProcessor);
    }

    public int forEachByteDesc(final int i, final int i1, final ByteBufProcessor byteBufProcessor) {
        return byteBuf.forEachByteDesc(i, i1, byteBufProcessor);
    }

    @Override
    public int refCnt() {
        return byteBuf.refCnt();
    }

    @Override
    public boolean release() {
        return byteBuf.release();
    }

    @Override
    public boolean release(final int i) {
        return byteBuf.release(i);
    }
}
