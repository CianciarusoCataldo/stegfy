package com.stegfy.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.stegfy.Constants.CAMERA;
import static com.stegfy.Constants.FILE_TYPE_IMAGE;
import static com.stegfy.Constants.GALLERY;
import static com.stegfy.Constants.IMAGE_DIRECTORY;
import static com.stegfy.Constants.PNG;

/**
 * Define properties for custom Fragment.
 *
 * @author Cataldo Cianciaruso
 */
public abstract class AdvancedFragment extends Fragment {

    /**
     * Run the default gallery app (or other file manager, if set), to get an image.
     */
    protected void galleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, FILE_TYPE_IMAGE);
        startActivityForResult(galleryIntent, GALLERY);
    }

    /**
     * Run the default camera app on device, to get image.
     */
    protected void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA);
    }

    /**
     * Return an available Uri to store image. The file name is generated randomly.
     *
     * @return : valid URI to store image
     */
    protected Uri getOutputMediaFileUri() {
        return null;
    }

    /**
     * Return an available File Object, to store image.
     *
     * @return Available File Object to store image
     * @throws IOException : if it's not possible to create or open file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected File getOutputMediaFile() throws IOException {
        File encodeImageDirectory =
                new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);

        if (!encodeImageDirectory.exists()) {
            encodeImageDirectory.mkdirs();
        }
        String uniqueId = UUID.randomUUID().toString();
        File mediaFile = new File(encodeImageDirectory, uniqueId + PNG);
        mediaFile.createNewFile();
        return mediaFile;
    }

    /**
     * Set the Visibility of the User Interface. Only valid ints are the visibility value
     * in {@link android.view.View} class: {@link android.view.View#VISIBLE}, {@link android.view.View#INVISIBLE}
     * and {@link android.view.View#GONE}. Other values will throw unexpected Exception.
     *
     * @param vis : the visibility of the User Interface.
     */
    protected abstract void setUiVisibility(int vis);

    /**
     * Simpler alternative to {@link AdvancedFragment#setUiVisibility(int)}.
     * Set the User Interface visibility(true for visible, false for invisible).
     *
     * @param vis : User Interface visibility
     */
    protected void setUiVisible(boolean vis) {
        if (vis) {
            setUiVisibility(View.VISIBLE);
        } else {
            setUiVisibility(View.INVISIBLE);
        }
    }


    /**
     * Start animation on User Interface.
     */
    public abstract void animate();


    /**
     * Load image from a source and show it to User Interface
     *
     * @param source : image source
     */
    public abstract void showImage(Object source);


    /**
     * Set the visibility of the dedicated progress view (like ProgressBar)
     *
     * @param vis : visibility of user progress view
     */
    protected abstract void setProgressVisible(boolean vis);

}

