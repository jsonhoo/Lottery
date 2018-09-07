package com.wyzk.lottery.utils;

import android.annotation.SuppressLint;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017-07-24.
 *
 */

public final class ByteUtils {
    private static byte HEAD = -14;
    private static int currentSizeNew = 0;
    private static byte[] cashBufferNew = new byte[4096];

    private ByteUtils() {
    }

    @SuppressLint("WrongConstant")
    public static short calcFrameShort() {
        Calendar c = Calendar.getInstance();
        byte hour = (byte)c.get(11);
        byte minute = (byte)c.get(12);
        byte second = (byte)c.get(13);
        int minsec = c.get(14);
        byte mm = (byte)(minsec >>> 6 & 15);
        short no = (short)(minute << 10 | second << 4 | mm);
        return no;
    }

    @SuppressLint("WrongConstant")
    public static int calcFrameNumber() {
        Calendar c = Calendar.getInstance();
         byte day = (byte)c.get(5);
        byte hour = (byte)c.get(11);
        byte minute = (byte)c.get(12);
        byte second = (byte)c.get(13);
        int minsec = c.get(14);
        byte mm = (byte)(minsec >> 8 & 3);
        int no = day << 19 | hour << 14 | minute << 8 | second << 2 | mm;
        System.out.println("############ " + day + ":" + hour + ":" + minute + ":" + second + "  " + no);
        return no;
    }
    @SuppressLint("WrongConstant")
    public static short calcFrameNumber1() {
        Calendar c = Calendar.getInstance();
         byte hour = (byte)c.get(11);
        byte minute = (byte)c.get(12);
        byte second = (byte)c.get(13);
        int minsec = c.get(14);
        int a3 = minsec >> 8 & 3;
        int a2 = minsec >> 6 & 3;
        byte millsec = (byte)(a3 << 2 | a2);
        short no = (short)(hour << 12 | minute << 8 | second << 4 | millsec);
        return no;
    }

    public static int bytesToInt(byte[] src, int offset) {
        boolean value = true;
        int value1 = (src[offset] & 255) << 24 | (src[offset + 1] & 255) << 16 | (src[offset + 2] & 255) << 8 | src[offset + 3] & 255;
        return value1;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if(hexString != null && !hexString.equals("")) {
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];

            for(int i = 0; i < length; ++i) {
                int pos = i * 2;
                d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }

            return d;
        } else {
            return null;
        }
    }

    public static int threebytesToInt(byte[] src) {
        boolean value = true;
        int value1 = (src[0] & 255) << 16 | (src[1] & 255) << 8 | src[2] & 255;
        return value1;
    }

    private static byte charToByte(char c) {
        return (byte)"0123456789ABCDEF".indexOf(c);
    }

    public static void putShort(short sht, byte[] sb, int index) {
        for(int i = 0; i < 2; ++i) {
            sb[1 - i + index] = (byte)(sht >> i * 8 & 255);
        }

    }

    private static byte[] shortToByteArray(short s) {
        byte[] shortBuf = new byte[2];

        for(int i = 0; i < 2; ++i) {
            int offset = (shortBuf.length - 1 - i) * 8;
            shortBuf[i] = (byte)(s >>> offset & 255);
        }

        return shortBuf;
    }


    public static byte[] verifyData(byte[] data, int size) {
        int i = 0;
        if(0 != currentSizeNew) {
            System.arraycopy(data, 0, cashBufferNew, currentSizeNew, size);
        } else {
            while(true) {
                if(i >= size || data[i] == HEAD) {
                    if(i == size) {
                        return null;
                    }

                    size -= i;
                    System.arraycopy(data, i, cashBufferNew, 0, size);
                    break;
                }

                ++i;
            }
        }

        currentSizeNew += size;

        while(currentSizeNew >= 18) {
            int dataLen = getDataLength(cashBufferNew[14], cashBufferNew[15]);
            int pktLen = dataLen + 18;
            if(pktLen > 4000) {
                currentSizeNew = 0;
                return null;
            }

            if(pktLen > currentSizeNew) {
                return null;
            }

            byte[] fullpacket = new byte[pktLen];
            System.arraycopy(cashBufferNew, 0, fullpacket, 0, pktLen);
            if(currentSizeNew > pktLen) {
                for(i = pktLen; i < currentSizeNew && cashBufferNew[i] != HEAD; ++i) {
                    ;
                }

                int j;
                for(j = 0; i < currentSizeNew; ++i) {
                    cashBufferNew[j++] = cashBufferNew[i];
                }

                currentSizeNew = j;
            } else {
                currentSizeNew = 0;
            }

            if(checkCRC16(fullpacket)) {
                return fullpacket;
            }

        }

        return null;
    }

    public static boolean checkCRC16(byte[] data) {
        boolean isRight = false;
        if(data != null && data.length > 0) {
            int newByteLen = data.length - 3;
            if(newByteLen > 0) {
                byte[] tmp = new byte[newByteLen];
                System.arraycopy(data, 1, tmp, 0, newByteLen);
                byte[] crcKey = CRC16Calc(tmp, newByteLen);
                if(crcKey.length == 2 && data[data.length - 2] == crcKey[0] && data[data.length - 1] == crcKey[1]) {
                    isRight = true;
                }
            }
        }

        return isRight;
    }

    public static boolean isNull(String strSource) {
        return strSource == null || "".equals(strSource.trim());
    }

    public static String getCurrentTime() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(sf.format(new Date()));
        return sf.format(new Date());
    }

    public static String byteToMac(byte[] resBytes) {
        StringBuffer buffer = new StringBuffer();

        for(int i = 0; i < resBytes.length; ++i) {
            String hex = Integer.toHexString(resBytes[i] & 255);
            if(hex.length() == 1) {
                hex = '0' + hex;
            }

            buffer.append(hex.toUpperCase());
        }

        return buffer.toString();
    }

    public static byte[] CRC16Calc(byte[] data, int length) {
        boolean j = false;
        int crc16 = '\uffff';

        int ret;
        for(ret = 0; ret < length; ++ret) {
            crc16 ^= data[ret] & 255;

            for(int var6 = 0; var6 < 8; ++var6) {
                int crc = crc16 & 1;
                if(crc != 0) {
                    crc16 = crc16 >> 1 ^ '萈';
                } else {
                    crc16 >>= 1;
                }
            }
        }

        ret = ~crc16 & '\uffff';
        byte[] var7 = new byte[]{(byte)(ret >> 8 & 255), (byte)(ret & 255)};
        return var7;
    }

    public static int CRC(byte[] data, int length) {
        boolean j = false;
        int crc16 = '\uffff';

        int ret;
        for(ret = 0; ret < length; ++ret) {
            crc16 ^= data[ret] & 255;

            for(int var6 = 0; var6 < 8; ++var6) {
                int flags = crc16 & 1;
                if(flags != 0) {
                    crc16 = crc16 >> 1 ^ '萈';
                } else {
                    crc16 >>= 1;
                }
            }
        }

        ret = ~crc16 & '\uffff';
        return ret;
    }

    public static byte[] getByteBit(byte b) {
        byte[] array = new byte[8];

        for(int i = 7; i >= 0; --i) {
            array[i] = (byte)(b & 1);
            b = (byte)(b >> 1);
        }

        return array;
    }

    public static String byteToBit(byte b) {
        return "" + (byte)(b >> 7 & 1) + (byte)(b >> 6 & 1) + (byte)(b >> 5 & 1) + (byte)(b >> 4 & 1) + (byte)(b >> 3 & 1) + (byte)(b >> 2 & 1) + (byte)(b >> 1 & 1) + (byte)(b >> 0 & 1);
    }

    public static int getCommandNew(byte[] data) {
        if(data != null && data.length > 5) {
            int dataLen = getDataLength(data[3], data[4]);
            return dataLen;
        } else {
            return -1;
        }
    }

    public static int getCommandNew(byte[] data, int index) {
        if(data != null && data.length > index) {
            int dataLen = getDataLength(data[index], data[index + 1]);
            return dataLen;
        } else {
            return -1;
        }
    }

    public static int getCommandForOpen(byte[] data) {
        byte index = 31;
        if(data != null && data.length > index) {
            int dataLen = getDataLength(data[index], data[index + 1]);
            return dataLen;
        } else {
            return -1;
        }
    }

    public static String getCmd(byte[] data) {
        if(data != null && data.length >= 5) {
            byte[] macBytes = new byte[]{data[3], data[4]};
            return byteToMac(macBytes);
        } else {
            return null;
        }
    }

    public static String getCmdForOPen(byte[] data) {
        byte index = 31;
        if(data != null && data.length >= index) {
            byte[] macBytes = new byte[]{data[index], data[index + 1]};
            return byteToMac(macBytes);
        } else {
            return null;
        }
    }

    public static String getCmd(byte[] data, int index) {
        if(data != null && data.length >= index) {
            byte[] macBytes = new byte[]{data[index], data[index + 1]};
            return byteToMac(macBytes);
        } else {
            return null;
        }
    }

    public static String getMacAddr(byte[] data, int index) {
        if(data != null && data.length > index) {
            byte[] macBytes = new byte[6];
            System.arraycopy(data, index, macBytes, 0, 6);
            return byteToMac(macBytes);
        } else {
            return null;
        }
    }

    public static String getMacAddr(byte[] data) {
        if(data != null && data.length > 11) {
            byte[] macBytes = new byte[6];
            System.arraycopy(data, 5, macBytes, 0, 6);
            return byteToMac(macBytes);
        } else {
            return null;
        }
    }

    public static int getTypeNew(ByteBuffer buf) {
        byte type = -1;
        if(buf != null && buf.capacity() > 11) {
            type = buf.get(11);
        }

        return type;
    }

    public static String toHexStrings(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        if(b != null) {
            for(int i = 0; i < b.length; ++i) {
                String s = Integer.toHexString(b[i] & 255);
                if(s.length() == 1) {
                    s = "0" + s;
                }

                buffer.append(s + " ");
            }
        }

        return buffer.toString();
    }

    public static String toHexString(byte[] b) {
        return b != null && b.length > 0?(b[0] == 90?toHexStringForOpen(b):toHexStringForHet(b)):toHexStringForHet(b);
    }

    public static String toHexStringForHet(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        if(b != null) {
            for(int i = 0; i < b.length; ++i) {
                String s = Integer.toHexString(b[i] & 255);
                if(s.length() == 1) {
                    s = "0" + s;
                }

                if(b[1] == 66) {
                    if(i >= 2 && (i <= 2 || i >= 4) && (i <= 4 || i >= 10) && (i <= 10 || i >= 18) && (i <= 18 || i >= 20) && (i <= 20 || i >= 24) && (i <= 24 || i >= 32) && (i <= 32 || i >= 34) && (i <= 34 || i >= b.length - 5) && (i <= b.length - 5 || i >= b.length - 1)) {
                        buffer.append(s + " ");
                    } else {
                        buffer.append(s);
                    }
                } else if(i >= 2 && (i <= 2 || i >= 4) && (i <= 4 || i >= 10) && (i <= 10 || i >= 13) && (i <= 13 || i >= 15) && (i <= 15 || i >= 48) && (i <= 48 || i >= b.length - 3) && (i <= b.length - 3 || i >= b.length - 1)) {
                    buffer.append(s + " ");
                } else {
                    buffer.append(s);
                }
            }
        }

        return buffer.toString();
    }

    public static String toHexStringForOpen(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        if(b != null) {
            for(int i = 0; i < b.length; ++i) {
                String s = Integer.toHexString(b[i] & 255);
                if(s.length() == 1) {
                    s = "0" + s;
                }

                if((i <= 0 || i >= 2) && (i <= 2 || i >= 4) && (i <= 4 || i >= 12) && (i <= 12 || i >= 18) && (i <= 18 || i >= 22) && (i <= 22 || i >= 30) && (i <= 30 || i >= 32) && (i <= 32 || i >= b.length - 3) && (i <= b.length - 3 || i >= b.length - 1)) {
                    buffer.append(s + " ");
                } else {
                    buffer.append(s);
                }
            }
        }

        return buffer.toString();
    }

    public static String toIp(String ip) {
        if(isNull(ip)) {
            return ip;
        } else {
            String[] lines = ip.split("\\.");
            if(lines != null && lines.length >= 3) {
                int len = lines[3].length();
                if(len == 1) {
                    ip = ip + "  ";
                } else if(len == 2) {
                    ip = ip + " ";
                }

                return ip;
            } else {
                return ip;
            }
        }
    }

    public static int getDataLength(byte byte1, byte byte2) {
        boolean len16 = false;
        String hex = Integer.toHexString(byte1 & 255);
        if(hex.length() == 1) {
            hex = '0' + hex;
        }

        int len161 = Integer.valueOf(hex, 16).intValue();
        boolean len17 = false;
        String hex1 = Integer.toHexString(byte2 & 255);
        if(hex1.length() == 1) {
            hex1 = '0' + hex1;
        }

        int len171 = Integer.valueOf(hex1, 16).intValue();
        int pktLen = len161 * 256 + len171;
        return pktLen;
    }

    public static byte getProtocolVersion(byte[] data) {
        return data != null && data[0] == -14?data[1]:-1;
    }

    public static String intToIp(int i) {
        return (i & 255) + "." + (i >> 8 & 255) + "." + (i >> 16 & 255) + "." + (i >> 24 & 255);
    }

    public static byte[] short2bytes(int intvalue) {
        intvalue &= '\uffff';
        byte byte1 = (byte)(intvalue & 255);
        byte byte2 = (byte)(intvalue >>> 8 & 255);
        byte[] port = new byte[]{byte2, byte1};
        return port;
    }

    public static byte[] getBodyBytes(String ip, String port, byte[] key, byte[] ips) throws NumberFormatException, IOException {
        String[] ipArr = ip.split("\\.");
        byte[] ipByte = new byte[]{(byte)(Integer.parseInt(ipArr[0]) & 255), (byte)(Integer.parseInt(ipArr[1]) & 255), (byte)(Integer.parseInt(ipArr[2]) & 255), (byte)(Integer.parseInt(ipArr[3]) & 255)};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write(ipByte);
        if(!isNum(port)) {
            throw new NumberFormatException("port is not number...");
        } else {
            byte[] portByte = short2bytes(Integer.parseInt(port));
            dos.write(portByte);
            dos.write(key);
            if(ips != null && ips.length > 0 && dos != null) {
                dos.write(ips);
            }

            byte[] bs = baos.toByteArray();
            baos.close();
            dos.close();
            return bs;
        }
    }

    public static byte[] getBodyBytes(String ip, short port, byte[] key, byte ipLastByte) throws NumberFormatException, IOException {
        String[] ipArr = ip.split("\\.");
        byte[] ipByte = new byte[]{(byte)(Integer.parseInt(ipArr[0]) & 255), (byte)(Integer.parseInt(ipArr[1]) & 255), (byte)(Integer.parseInt(ipArr[2]) & 255), (byte)(Integer.parseInt(ipArr[3]) & 255)};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write(ipByte);
        byte[] portByte = short2bytes(port);
        dos.write(portByte);
        dos.write(key);
        dos.writeByte(ipLastByte);
        byte[] bs = baos.toByteArray();
        baos.close();
        dos.close();
        return bs;
    }

    public static byte[] getBodyBytesForOpen(String ip, String port, byte[] key) throws NumberFormatException, IOException {
        String[] ipArr = ip.split("\\.");
        byte[] ipByte = new byte[]{(byte)(Integer.parseInt(ipArr[0]) & 255), (byte)(Integer.parseInt(ipArr[1]) & 255), (byte)(Integer.parseInt(ipArr[2]) & 255), (byte)(Integer.parseInt(ipArr[3]) & 255)};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write(key);
        dos.write(ipByte);
        if(!isNum(port)) {
            throw new NumberFormatException("port is not number...");
        } else {
            byte[] portByte = short2bytes(Integer.parseInt(port));
            dos.write(portByte);
            byte[] bs = baos.toByteArray();
            baos.close();
            dos.close();
            return bs;
        }
    }

    public static byte[] getBodyBytesForOpen(String ip, short port, byte[] key) throws NumberFormatException, IOException {
        String[] ipArr = ip.split("\\.");
        byte[] ipByte = new byte[]{(byte)(Integer.parseInt(ipArr[0]) & 255), (byte)(Integer.parseInt(ipArr[1]) & 255), (byte)(Integer.parseInt(ipArr[2]) & 255), (byte)(Integer.parseInt(ipArr[3]) & 255)};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write(key);
        dos.write(ipByte);
        byte[] portByte = short2bytes(port);
        dos.write(portByte);
        byte[] bs = baos.toByteArray();
        baos.close();
        dos.close();
        return bs;
    }

    public static boolean isNum(String strNum) {
        return strNum.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    public static boolean isMac(String str) {
        String patternMac = "[a-fA-F0-9]{2}+[a-fA-F0-9]{2}+[a-fA-F0-9]{2}";
        String arg1 = "het-";
        String arg2 = "-a-b";
        String pattern = String.format("^%s+%s+%s$", new Object[]{arg1, patternMac, arg2});
        System.out.println(pattern);
        return str.matches(pattern);
    }

    public static void main(String[] args) {
        short ss = 9001;
        byte[] bb = short2bytes(ss & '\uffff');
        System.out.println(toHexString(bb));
    }
}
