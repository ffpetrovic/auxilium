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
private var frequencyIndex = 0

class MainActivity : AppCompatActivity() {

    var FREQUENCIES = doubleArrayOf(65.41, 69.30, 73.42, 77.78, 82.41, 87.31, 92.50, 98.00, 103.83, 110.00, 116.54, 123.47, 130.81, 138.59, 146.83, 155.56, 164.81, 174.61, 185.00, 196.00, 207.65, 220.00, 233.08, 246.94, 261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 392.00, 415.30, 440.00, 466.16, 493.88, 523.25, 554.37, 587.33, 622.25, 659.25, 698.46, 739.99, 783.99, 830.61, 880.00, 932.33, 987.77)

    var  NOTE_NAMES = arrayOf("C","C#","D","D#","E","F","F#","G","G#","A","A#","B")

//    var NOTE_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")


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


    private fun findFrequency(freq: Double): String {
        var index = -1
        var dist = java.lang.Double.MAX_VALUE
        for (i in 0 until FREQUENCIES.size) {
            val d = Math.abs(freq - FREQUENCIES[i])
            if (d < dist) {
                index = i
                dist = d
            }
        }
        frequencyIndex = index;
        return NOTE_NAMES[index % 12] + "" + (index / 12 + 2)
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

                var freq = currentNote!!.getActualFrequency();
                freq = Math.round(freq * 100.0) / 100.0
                tvNote.text = findFrequency(freq);
                tvFrequency.text = freq.toString()

                if(frequencyIndex != 0 && frequencyIndex < FREQUENCIES.size - 1 && freq != FREQUENCIES[frequencyIndex]) {
                    var nextNote: Double = 0.0
                    when(freq > FREQUENCIES[frequencyIndex]) {
                        true -> nextNote = FREQUENCIES[frequencyIndex + 1]
                        false -> nextNote = FREQUENCIES[frequencyIndex - 2]
                    }

                    var percentage: Double = ((freq - FREQUENCIES[frequencyIndex]) / (nextNote - FREQUENCIES[frequencyIndex])) * 100
                    percentage = Math.round(percentage * 100.0) / 100.0
//                    Log.d("Percentage Aux", freq.toString() + " " + currentNote!!.frequency + " " + nextNote);
//                    percentage = Math.round(percentage);
                    textViewpercentage.text = "${percentage} away from  ${tvNote.text}";
                }

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
