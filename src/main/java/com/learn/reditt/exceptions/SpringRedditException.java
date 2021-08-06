package com.learn.reditt.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SpringRedditException extends RuntimeException  {
    public SpringRedditException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }

    public SpringRedditException(String exMessage) {
        super(exMessage);
    }
}
