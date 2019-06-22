package com.stegfy.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.stegfy.BuildConfig;
import com.stegfy.R;
import com.stegfy.activity.ChoiceActivity;
import com.stegfy.activity.SettingsActivity;
import com.stegfy.activity.TextDialogActivity;
import com.stegfy.interfaces.TextEncoder;
import com.stegfy.utils.GlideApp;
import com.stegfy.utils.compress.Coders;
import com.stegfy.utils.steganography.Steganographer;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static com.stegfy.Constants.CAMERA;
import static com.stegfy.Constants.FILE_TYPE_IMAGE;
import static com.stegfy.Constants.GALLERY;
import static com.stegfy.Constants.PASSWORD;
import static com.stegfy.Constants.PICTURE_DIALOG_ITEM1;
import static com.stegfy.Constants.PICTURE_DIALOG_ITEM2;
import static com.stegfy.Constants.PICTURE_DIALOG_ITEM3;
import static com.stegfy.Constants.PICTURE_DIALOG_TITLE;
import static com.stegfy.Constants.SECRET_DATA_KEY;
import static com.stegfy.Constants.TEXTFILE;

public class FragmentEncode extends AdvancedFragment implements TextEncoder {

    private static int requestType;
    private static boolean firstLaunch = true;
    public static Coders.Algoritms choiceComp = Coders.Algoritms.NONE;

    private ProgressBar progress;
    private File imageFile;
    private boolean encoded = false;
    private Bitmap bmpImage;

    private String secretText;
    private String hashingAlgo;
    private String encryptionAlgo;

    private AlertDialog.Builder pictureDialog;
    private TextView imageTextMessage;
    private ImageView loadImage;
    private View encPage;
    private Button textInputButton;
    private Button sendButton;
    private View imageContainer;

    private Activity context;

    public FragmentEncode() {
    }

    @Override
    public void onAttach(Context c) {
        super.onAttach(c);
        if (c instanceof Activity) {
            context = (Activity) c;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        AsyncTask.execute(() -> {
            progress = context.findViewById(R.id.progressBar);
            pictureDialog = new AlertDialog.Builder(context, R.style.AlertDialogStyle);

            pictureDialog.setTitle(PICTURE_DIALOG_TITLE);

            String[] pictureDialogItems = {
                    PICTURE_DIALOG_ITEM1,
                    PICTURE_DIALOG_ITEM2,
                    PICTURE_DIALOG_ITEM3
            };

            pictureDialog.setItems(pictureDialogItems,
                    (DialogInterface dialog, int which) -> {
                        switch (which) {
                            case 0: {
                                loadImage.setVisibility(View.INVISIBLE);
                                imageTextMessage.setVisibility(View.INVISIBLE);
                                SettingsActivity.pause = true;
                                galleryIntent();
                            }
                            break;

                            case 1: {
                                loadImage.setVisibility(View.INVISIBLE);
                                imageTextMessage.setVisibility(View.INVISIBLE);
                                SettingsActivity.pause = true;
                                cameraIntent();
                            }
                            break;

                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    });
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_encode, container, false);

        AsyncTask.execute(() -> {
            encPage = view.findViewById(R.id.encpage);
            imageContainer = encPage.findViewById(R.id.image_container);
            imageTextMessage = imageContainer.findViewById(R.id.imageTextMessage);
            loadImage = imageContainer.findViewById(R.id.loadImage);
            textInputButton = encPage.findViewById(R.id.textInputButton);
            sendButton = encPage.findViewById(R.id.sendButton);

            loadImage.setOnClickListener((View -> pictureDialog.show()));

            textInputButton.setOnClickListener((View -> {
                if (imageFile == null || !(imageFile.exists())) {
                    Toasty.warning(context, "Please, select an image first", Toast.LENGTH_SHORT, true).show();

                } else {
                    Intent intent = new Intent(getContext(), TextDialogActivity.class);
                    startActivityForResult(intent, TEXTFILE);
                }
            }));

            sendButton.setOnClickListener((View -> {
                if (imageFile != null && imageFile.exists() && encoded) {

                    Intent intent = new Intent(getContext(), ChoiceActivity.class);
                    intent.putExtra("img_file", imageFile);
                    intent.putExtra("file_url", imageFile.toURI().toString());
                    startActivity(intent);
                } else
                    Toasty.warning(context, "Please, encode a text message into an image before send", Toast.LENGTH_SHORT, true).show();
            }));

        });

        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if (!isVisibleToUser) {

                secretText = "";
                imageFile = null;
                bmpImage = null;
                requestType = -1;
                setProgressVisible(false);
                loadImage.setImageResource(android.R.color.transparent);
                imageTextMessage.setVisibility(View.VISIBLE);

                if (SettingsActivity.is_animations_enabled) {
                    setUiVisibility(View.INVISIBLE);
                } else {
                    setUiVisibility(View.VISIBLE);
                }


            } else {

                if (SettingsActivity.is_animations_enabled && !SettingsActivity.pause) {
                    animate();
                } else {
                    SettingsActivity.pause = false;
                    setUiVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            Log.i("ERROR", e.getLocalizedMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SettingsActivity.is_animations_enabled && firstLaunch) {
            firstLaunch = false;
            animate();
        }

        if (SettingsActivity.pause) {
            SettingsActivity.pause = false;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == Activity.RESULT_CANCELED) {
                loadImage.setVisibility(View.VISIBLE);
                if (bmpImage == null) {
                    imageTextMessage.setVisibility(View.VISIBLE);
                }

            } else if (requestCode == GALLERY && data != null) {
                requestType = GALLERY;
                showImage(data.getData());

            } else if (requestCode == CAMERA) {
                requestType = CAMERA;
                showImage(imageFile);

            } else if (requestCode == TEXTFILE && data != null) {
                if (data.getExtras() != null) {
                    String pass = data.getExtras().getString(PASSWORD);
                    secretText = data.getExtras().getString(SECRET_DATA_KEY);

                    if (secretText == null || secretText.isEmpty()) {
                        Toasty.warning(context, getString(R.string.error_no_text), Toast.LENGTH_SHORT, true).show();

                    } else if (imageFile == null || !imageFile.exists()) {
                        Toasty.warning(context, getString(R.string.error_no_image), Toast.LENGTH_SHORT, true).show();

                    } else {
                        switch (requestType) {
                            case GALLERY:
                                encodeImageFromGallery(pass);
                                break;
                            case CAMERA:
                                encodeImageFromCamera(pass);
                                break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Toasty.error(Objects.requireNonNull(getContext()), getString(R.string.error_fail_message), Toast.LENGTH_SHORT, true).show();
        }

    }

    @Override
    protected void setUiVisibility(int vis) {
        imageContainer.setVisibility(vis);
        textInputButton.setVisibility(vis);
        sendButton.setVisibility(vis);
    }


    @Override
    protected Uri getOutputMediaFileUri() {
        try {
            imageFile = getOutputMediaFile();
            return FileProvider.getUriForFile(Objects.requireNonNull(getActivity()),
                    BuildConfig.APPLICATION_ID + ".fileprovider", imageFile);
        } catch (IOException ex) {
            Toasty.error(Objects.requireNonNull(getContext()), getString(R.string.error_fail_message), Toast.LENGTH_SHORT, true).show();

        }
        return null;
    }

    @Override
    public void encode(String password) {
        try {
            getAlgoNamesFromSharedPreferences();
            if (password != null && !password.isEmpty()) {
                Steganographer.withInput(bmpImage)
                        .withPassword(password)
                        .encode(secretText, encryptionAlgo, hashingAlgo)
                        .intoFile(imageFile);
            } else {
                Steganographer.withInput(bmpImage)
                        .encode(secretText, encryptionAlgo, hashingAlgo)
                        .intoFile(imageFile);
            }
        } catch (Exception e) {
            if (imageFile != null && imageFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                imageFile.delete();
            }
        }
    }

    /**
     * Encode text to image picked from the device storage
     *
     * @param password : password for encoding
     */
    private void encodeImageFromGallery(final String password) {
        setProgressVisible(true);
        new Thread(() -> {
            encode(password);
            context.runOnUiThread(() -> {
                if (imageFile != null && imageFile.exists()) {
                    MediaScannerConnection.scanFile(getContext(),
                            new String[]{imageFile.getPath()},
                            new String[]{FILE_TYPE_IMAGE}, null);
                    Toasty.success(context, getString(R.string.message_encoding_success), Toast.LENGTH_SHORT, true).show();
                    encoded = true;
                }
                reset();
            });

        }).start();
    }

    /**
     * Encode text to image captured by device camera
     *
     * @param password : password for encoding
     */
    private void encodeImageFromCamera(final String password) {
        setProgressVisible(true);
        new Thread(() -> {
            encode(password);
            context.runOnUiThread(() -> {
                if (imageFile != null && imageFile.exists()) {
                    Intent mediaScanIntent =
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(imageFile);
                    mediaScanIntent.setData(contentUri);
                    Objects.requireNonNull(getContext()).sendBroadcast(mediaScanIntent);
                    Toasty.success(context, getString(R.string.message_encoding_success), Toast.LENGTH_SHORT, true).show();
                    encoded = true;

                } else {
                    Toasty.error(Objects.requireNonNull(getContext()), getString(R.string.error_encoding_failed), Toast.LENGTH_SHORT, true).show();

                }
                reset();
            });
        }).start();
    }



    /**
     * Resize the given bitmap to a fixed size of 1920x1080 pixel. The original aspect-ratio
     * is preserved. This operation prevents memory leak due to original image size.
     *
     * @param image : image to resize
     *
     * @return : image resized
     */
    private Bitmap resize(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) 1920 / (float) 1080;

        int finalWidth = 1920;
        int finalHeight = 1080;
        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) 1080 * ratioBitmap);
        } else {
            finalHeight = (int) ((float) 1920 / ratioBitmap);
        }
        image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        return image;
    }


    private void getAlgoNamesFromSharedPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultHashingAlgo = getString(R.string.list_prefs_default_hashing);
        String defaultEncryptionAlgo = getString(R.string.list_prefs_default_encryption);
        String hashingPref = getString(R.string.list_prefs_key_hashing);
        String encryptionPref = getString(R.string.list_prefs_key_encryption);
        hashingAlgo = sharedPref.getString(hashingPref, defaultHashingAlgo);
        encryptionAlgo = sharedPref.getString(encryptionPref, defaultEncryptionAlgo);
    }


    @Override
    public void setProgressVisible(boolean vis) {
        if (vis) {
            progress.setVisibility(View.VISIBLE);
            encPage.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            encPage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void animate() {
        setUiVisibility(View.INVISIBLE);

        YoYo.with(Techniques.SlideInLeft).withListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                imageContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                YoYo.with(Techniques.SlideInLeft).withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation1) {
                        textInputButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation1) {
                        YoYo.with(Techniques.SlideInLeft).withListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation11) {
                                sendButton.setVisibility(View.VISIBLE);
                            }

                        }).duration(200).delay(0).playOn(sendButton);
                    }
                }).duration(200).delay(0).playOn(textInputButton);
            }
        }).duration(200).delay(0).playOn(imageContainer);
    }

    @Override
    public void showImage(Object source) {
        GlideApp.with(context)
                .asBitmap().load(source)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        context.runOnUiThread(() -> Toasty.error(context, "Error loading image", Toast.LENGTH_SHORT).show());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            bmpImage = resize(resource);
                            imageFile = getOutputMediaFile();

                            if (SettingsActivity.is_animations_enabled) {

                                context.runOnUiThread(() -> {
                                    imageTextMessage.setVisibility(View.INVISIBLE);

                                    loadImage.setVisibility(View.INVISIBLE);
                                    YoYo.with(Techniques.SlideInLeft).onStart((Animator a) -> {

                                        loadImage.setVisibility(View.VISIBLE);
                                        GlideApp.with(context)
                                                .load(bmpImage)
                                                .into(loadImage);

                                    }).duration(400).delay(0)
                                            .playOn(imageContainer);
                                    encoded = false;
                                    Toasty.info(context, getString(R.string.message_image_selected), Toast.LENGTH_SHORT, true).show();
                                });

                            } else {
                                context.runOnUiThread(() -> {
                                    imageTextMessage.setVisibility(View.INVISIBLE);
                                    loadImage.setVisibility(View.VISIBLE);
                                    GlideApp.with(context).load(bmpImage).into(loadImage);
                                    Toasty.info(Objects.requireNonNull(getContext()), getString(R.string.message_image_selected), Toast.LENGTH_SHORT, true).show();
                                });
                            }

                        } catch (IOException ex) {
                            context.runOnUiThread(() -> Toasty.error(context, "Error loading image", Toast.LENGTH_SHORT).show());
                        }
                        return false;

                    }
                }).submit();
    }

    private void reset() {
        encoded = true;
        secretText = "";
        bmpImage = null;
        requestType = -1;
        setProgressVisible(false);
    }

}
