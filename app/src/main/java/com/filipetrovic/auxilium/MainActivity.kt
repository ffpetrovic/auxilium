package com.filipetrovic.auxilium

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

private var sampleRate: Int = 0
private var bufferSize: Int = 0
private var readSize: Int = 0
private var amountRead: Int = 0
private var buffer: FloatArray? = null
private var intermediaryBuffer: ShortArray? = null
private var audioRecord: AudioRecord? = null
private var yin: Yin? = null
private var currentNote: Note? = null
private var result: PitchDetectionResult? = null
private var isRecording: Boolean = false
private var handler: Handler? = null
private var thread: Thread? = null
private var comp: Float? = 0f
private var counter: Int? = 0

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    11)
        }
        init()
        start()
    }


    fun init() {
        sampleRate = AudioUtils.getSampleRate()
        bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT) * 4
        readSize = bufferSize / 4
        buffer = FloatArray(readSize)
        intermediaryBuffer = ShortArray(readSize)
        isRecording = false
        audioRecord = AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize)
        yin = Yin(sampleRate + 0f, readSize)
        currentNote = Note(Note.DEFAULT_FREQUENCY)
        handler = Handler(Looper.getMainLooper())
    }

    fun start() {
        if (audioRecord != null) {
            isRecording = true
            audioRecord?.startRecording()
            val thread = Thread(Runnable {
                //Runs off the UI thread
                findNote()
            }, "Tuner Thread")
            thread.start()
        }
    }

    private fun findNote() {
        while (isRecording) {
            amountRead = audioRecord!!.read(intermediaryBuffer, 0, readSize)
            buffer = shortArrayToFloatArray(intermediaryBuffer!!)
            result = yin?.getPitch(buffer)
            currentNote?.changeTo(result!!.getPitch())
            handler?.post({
                //Runs on the UI thread

//                if (currentNote.getActualFrequency().toInt() !== -1) {
//                    comp = comp + currentNote!!.getActualFrequency().toFloat()
//                    counter = counter + 1;
//                }
//                if (counter >= 5) {
//                    counter = 0
//                    mainTextview.setText(comp / 5 + "")
//                    comp = 0f
//                }
                textView.text = currentNote!!.getActualFrequency().toString()
            })
        }
    }

    private fun shortArrayToFloatArray(array: ShortArray): FloatArray {
        val fArray = FloatArray(array.size)
        for (i in array.indices) {
            fArray[i] = array[i].toFloat()
        }
        return fArray
    }

    fun stop() {
        isRecording = false
        if (audioRecord != null) {
            audioRecord?.stop()
        }
    }

    fun release() {
        isRecording = false
        if (audioRecord != null) {
            audioRecord?.release()
        }
    }

    fun isInitialized(): Boolean {
        return if (audioRecord != null) {
            true
        } else false
    }
}
