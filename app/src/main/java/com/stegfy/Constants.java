package com.stegfy;

public interface Constants {
    String IMAGE_DIRECTORY = "/Pictures/Stegfy";
    int GALLERY = 0;
    int CAMERA = 1;
    int TEXTFILE = 2;

    String FILE_TYPE_TEXT = "text/*";
    String FILE_TYPE_IMAGE = "image/*";

    String DEFAULT_TEXT_MESSAGE = "Nothing to show";
    String FILE_CHOOSER_TITLE = "Select a File to Upload";
    String PNG = ".png";

    String TAG_HOME = "home";
    String TAB_ENCODE_TITLE = "Encode";
    String TAB_DECODE_TITLE = "Decode";

    String SECRET_DATA_KEY = "secret_data";
    String PASSWORD="encode_password";

    String ERROR_FILE_CANT_OPEN = "Unable to open file ";

    String PICTURE_DIALOG_TITLE = "Select Action";
    String PICTURE_DIALOG_ITEM1 = "Select photo from gallery";
    String PICTURE_DIALOG_ITEM2 = "Capture photo from camera";
    String PICTURE_DIALOG_ITEM3 = "Cancel";

    String midWithPwd = "!0π";
    String midWithOutPwd = "!0Ω";
    String end = "eƒ<";

    String MESSAGE_MISSING_PASSWORD = "Please enter password, this is password protected file";
    int MIN_PASSWORD_LENGTH = 8;
    String ERROR_SHORT_PASSWORD = "Password must have minimum 8 characters";

}
