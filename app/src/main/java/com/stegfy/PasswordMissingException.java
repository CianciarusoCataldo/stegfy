package com.stegfy;

import static com.stegfy.Constants.MESSAGE_MISSING_PASSWORD;

public class PasswordMissingException extends Exception{
    public PasswordMissingException()
    {
        super(MESSAGE_MISSING_PASSWORD);
    }

}
