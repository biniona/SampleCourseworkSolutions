package edu.virginia.engine.display;

import java.util.HashMap;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import sun.audio.*;

public class SoundManager {

    private static HashMap soundEffectHM = new HashMap();
    private static HashMap musicHM = new HashMap();
    private static AudioPlayer soundEffectPLayer;
    private static AudioPlayer musicPlayer;


    public void LoadSoundEffect(String id, String filename){
        String file = ("resources" + File.separator + filename);
        AudioStream stream = loadSound(file);
        soundEffectHM.put(id, stream);
    }
    //FILE MUST BE UNDER 1MGB
    public void LoadMusic(String id, String filename){
        String file = ("resources" + File.separator + filename);
        AudioStream stream = loadSound(file);
        AudioData data;
        try {
            data = stream.getData();
        }
        catch (IOException e){
            e.printStackTrace();
            throw new java.lang.Error("ERROR GETTING AUDIO DATA");

        }
        ContinuousAudioDataStream contStream = new ContinuousAudioDataStream(data);
        musicHM.put(id, contStream);
    }

        //todo: BUG ONLY PLAYS SOUND ONCE!!!!
    public static void PlaySoundEffect(String id){
        soundEffectPLayer.player.start((AudioStream) soundEffectHM.get(id));

    }


    public static void PlayMusic(String id) {
        // play the audio clip with the audioplayer class
        musicPlayer.player.start((ContinuousAudioDataStream) musicHM.get(id));


    }

    /*
    The loadSound function is based on the code from this site:
    https://alvinalexander.com/java/java-audio-example-java-au-play-sound
    */
    private AudioStream loadSound(String Filename) {

        InputStream in;
        AudioStream audioStream;
        try
        {
            in = new FileInputStream(Filename);
        }
        catch (FileNotFoundException e)
        {
            throw new java.lang.Error("Music File Could not be found");
        }

        try
        {
            audioStream = new AudioStream(in);
            return audioStream;
        }
        catch (IOException e)
        {
            throw new java.lang.Error("IOException in audiostream");
        }
    }

}
