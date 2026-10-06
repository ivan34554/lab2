package com.example.lab2.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AudioFile extends BaseEntity {
    public enum Format { MP3, WAV, OGG, FLAC, AAC }

    private Long episodeId;
    private String path;
    private Format format;
    private long sizeBytes;
    private boolean main;
}