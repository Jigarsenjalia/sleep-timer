/*
Copyright (c) 2013 Joel Andrews
Distributed under the MIT License: http://opensource.org/licenses/MIT
 */

package com.oldsneerjaw.sleeptimer;

import android.media.AudioManager;

import com.oldsneerjaw.sleeptimer.test.AndroidMockingTestCase;

import org.mockito.Mockito;

/**
 * Test cases for {@link PauseMusicService}.
 */
public class PauseMusicServiceTest extends AndroidMockingTestCase {

    private AudioManager mockAudioManager;
    private AudioManager.OnAudioFocusChangeListener mockListener;
    private PauseMusicNotifier mockNotifier;
    private PauseMusicService service;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mockAudioManager = Mockito.mock(AudioManager.class);
        mockListener = Mockito.mock(AudioManager.OnAudioFocusChangeListener.class);
        mockNotifier = Mockito.mock(PauseMusicNotifier.class);

        service = new PauseMusicService();
        service.onCreate(mockAudioManager, mockListener, mockNotifier);
    }

    public void testPauseAndNotify_Success() {
        Mockito.when(mockAudioManager.requestAudioFocus(mockListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN))
                .thenReturn(AudioManager.AUDIOFOCUS_REQUEST_GRANTED);

        assertTrue(service.pauseAndNotify());

        Mockito.verify(mockNotifier).postNotification();
    }

    public void testPauseAndNotify_Failed() {
        Mockito.when(mockAudioManager.requestAudioFocus(mockListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN))
                .thenReturn(AudioManager.AUDIOFOCUS_REQUEST_FAILED);

        assertFalse(service.pauseAndNotify());

        Mockito.verifyNoMoreInteractions(mockNotifier);
    }

    public void testOnDestroy() {
        service.onDestroy();

        Mockito.verify(mockAudioManager).abandonAudioFocus(mockListener);
    }

    public void testListenerOnAudioFocusChange_FocusLost() {
        PauseMusicService mockService = Mockito.mock(PauseMusicService.class);
        PauseMusicService.AudioFocusListener listener = mockService.new AudioFocusListener(mockAudioManager);

        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS);

        Mockito.verify(mockAudioManager).abandonAudioFocus(listener);
    }
}
