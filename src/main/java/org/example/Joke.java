package org.example;

public record Joke(int id, String text, String type, boolean nsfw, boolean religious, boolean political, boolean racist,
                   boolean sexist, boolean explicit) {
}
