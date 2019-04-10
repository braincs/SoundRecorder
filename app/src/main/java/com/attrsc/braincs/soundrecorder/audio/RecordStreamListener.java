package com.attrsc.braincs.soundrecorder.audio;

/**
 * Created by Shuai
 * 07/02/2019.
 */

public interface RecordStreamListener {

    void onRecording(byte[] audiodata, int i, int length);

    void finishRecord();
}
