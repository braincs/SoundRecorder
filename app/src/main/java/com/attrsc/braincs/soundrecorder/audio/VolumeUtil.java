package com.attrsc.braincs.soundrecorder.audio;

/**
 * Created by Shuai
 * 08/02/2019.
 */

/**
 * 8位pcm用一个字节表示语音的一个点，16位pcm用两个字节，也就是一个short来表示语音的一个点。
 * 需要特别注意的是，如果你用的16位pcm编码，而取录音数据用的是byte的话，
 * 需要自己将两个bye转换成一个short。
 * 将两个byte转换成一个short，有小端和大端两种，
 * 一般默认情况都是小端，但是有的开源库，
 * 比如lamemp3需要的就是大端，这个要根据不同的情况进行不同的处理。
 */
public class VolumeUtil {

    /**
     * 录音的编码主要有两种：8位pcm和16位pcm
     * @param buffer
     * @return
     */
    public static double calculateVolume(short[] buffer){
        double sumVolume = 0.0;
        double avgVolume = 0.0;
        double volume = 0.0;

        for(short b : buffer){

            sumVolume += Math.abs(b);

        }

        avgVolume = sumVolume / buffer.length;

        volume = Math.log10(1 + avgVolume) * 10;
        return volume;
    }

    public static double calculateVolume(byte[] buffer){

        double sumVolume = 0.0;
        double avgVolume = 0.0;
        double volume = 0.0;

        for(int i = 0; i < buffer.length; i+=2){
            int v1 = buffer[i] & 0xFF;
            int v2 = buffer[i + 1] & 0xFF;
            int temp = v1 + (v2 << 8);// 小端

            if (temp >= 0x8000) {
                temp = 0xffff - temp;
            }

            sumVolume += Math.abs(temp);
        }

        avgVolume = sumVolume / buffer.length / 2;
        volume = Math.log10(1 + avgVolume) * 10;
        return volume;
    }


    /**
     * calculate volume rate (volume -10)/40 [0, 40]
     * @param buffer
     * @return
     */
    public static double calculateVolumeRate(byte[] buffer){
        double volume = calculateVolume(buffer);
        volume = Math.min(volume - 10, 40);
        volume = Math.max(0, volume)/ 40;
        return volume;
    }


}
