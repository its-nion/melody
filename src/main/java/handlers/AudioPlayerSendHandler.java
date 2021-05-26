package handlers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler
{
    private final AudioPlayer audioPlayer;
    private final ByteBuffer buffer;
    private MutableAudioFrame lastFrame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.buffer = ByteBuffer.allocate(1024);
        this.lastFrame = new MutableAudioFrame();
        this.lastFrame.setBuffer(buffer);
    }

    @Override
    public boolean canProvide()
    {
        return this.audioPlayer.provide(this.lastFrame);
    }

    @Override
    public ByteBuffer provide20MsAudio()
    {
        return buffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
