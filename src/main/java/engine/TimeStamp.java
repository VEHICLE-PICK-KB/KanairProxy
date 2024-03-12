package engine;

import java.time.LocalDateTime;

public class TimeStamp {

    public LocalDateTime timeStamp;

    public TimeStamp() {

    }

    public TimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LocalDateTime getTime() {
        return this.timeStamp;
    }

    public void setTime(LocalDateTime date) {
        this.timeStamp = date;
    }

}