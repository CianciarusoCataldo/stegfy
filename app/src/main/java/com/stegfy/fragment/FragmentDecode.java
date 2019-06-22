package com.stegfy.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
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
import com.stegfy.PasswordMissingException;
import com.stegfy.R;
import com.stegfy.activity.SettingsActivity;
import com.stegfy.interfaces.TextDecoder;
import com.stegfy.utils.GlideApp;
import com.stegfy.utils.compress.Coders;
import com.stegfy.utils.p2p.WifiActivity;
import com.stegfy.utils.steganography.Steganographer;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static com.stegfy.Constants.DEFAULT_TEXT_MESSAGE;
import static com.stegfy.Constants.GALLERY;
import static com.stegfy.Constants.MESSAGE_MISSING_PASSWORD;
import static com.stegfy.Constants.SECRET_DATA_KEY;

public class FragmentDecode extends AdvancedFragment implements TextDecoder {

    private ImageView decodeImage;
    private Bitmap bmpImage;
    private View decPage;
    private TextView imageTextMessage;
    private TextView passwordToDecode;
    private Button decodeButton;
    private Button receiveButton;
    private String decodedMessage;
    private boolean neededPassword;
    private View passwordContainer;
    private View decompressions;
    ProgressBar progress;
    private View imageContainer;
    private Activity context;
    public static Coders.Algoritms choiceDec = Coders.Algoritms.NONE;

    public FragmentDecode() {
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
        progress = context.findViewById(R.id.progressBar);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decode, container, false);

        AsyncTask.execute(() -> {
            decPage = view.findViewById(R.id.decpage);
            decodeButton = decPage.findViewById(R.id.decodeButton);
            receiveButton = decPage.findViewById(R.id.receiveButton);
            imageContainer = decPage.findViewById(R.id.imagedec_container);
            decompressions = decPage.findViewById(R.id.decompressions);
            passwordContainer = decPage.findViewById(R.id.password_dec_container);
            passwordToDecode = passwordContainer.findViewById(R.id.passwordToDecode);
            imageTextMessage = imageContainer.findViewById(R.id.imageTextDecodeMessage);
            decodeImage = imageContainer.findViewById(R.id.loadDecodeImage);

            if (SettingsActivity.is_animations_enabled && !SettingsActivity.pause) {
                context.runOnUiThread(() -> setUiVisibility(View.INVISIBLE));
            }

            receiveButton.setOnClickListener((View v) -> {
                Intent intent = new Intent(getActivity(), WifiActivity.class);
                startActivity(intent);

            });

            decodeImage.setOnClickListener((View v) -> {
                passwordToDecode.setError(null);
                decodeImage.setVisibility(View.INVISIBLE);
                SettingsActivity.pause = true;
                galleryIntent();

            });

            decodeButton.setOnClickListener((View v) -> {

                if (bmpImage == null) {
                    Toasty.warning(context, getString(R.string.error_no_image), Toast.LENGTH_SHORT, true).show();
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.decode_text_dialog_layout);

                    TextView decodedText = dialog.findViewById(R.id.decodedText);
                    decodedText.setMovementMethod(new ScrollingMovementMethod());

                    dialog.findViewById(R.id.close_button).setOnClickListener(View -> {
                        decodedMessage = "";
                        dialog.dismiss();
                    });

                    dialog.findViewById(R.id.copy_button).setOnClickListener((View -> {
                        if (!(decodedMessage != null && !decodedMessage.isEmpty())) {

                            Toasty.warning(context, getString(R.string.nothing_to_copy), Toast.LENGTH_SHORT, true).show();
                            return;
                        }
                        ClipboardManager clipboard = (ClipboardManager)
                                Objects.requireNonNull(context.getSystemService(Context.CLIPBOARD_SERVICE));
                        ClipData clip = ClipData.newPlainText(SECRET_DATA_KEY, decodedMessage);
                        clipboard.setPrimaryClip(clip);
                        Toasty.success(context, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT, true).show();
                    }));
                    getDecodedText(dialog, decodedText);
                }
            });
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        try {
            if (!isVisibleToUser) {
                decodedMessage = "";
                bmpImage = null;
                setProgressVisible(false);
                decodeImage.setImageResource(android.R.color.transparent);
                imageTextMessage.setVisibility(View.VISIBLE);

                if (SettingsActivity.is_animations_enabled && !SettingsActivity.pause) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_CANCELED) {
                decodeImage.setVisibility(View.VISIBLE);

            } else if (requestCode == GALLERY && data != null) {
                showImage(data.getData());

            } else {
                imageTextMessage.setVisibility(View.INVISIBLE);
                decodeImage.setVisibility(View.VISIBLE);
                GlideApp.with(context)
                        .load(bmpImage)
                        .into(decodeImage);
            }

        } catch (Exception ex) {
            Toasty.error(context, getString(R.string.error), Toast.LENGTH_SHORT, true).show();
        }

    }


    private void getDecodedText(final Dialog dialog, TextView decodedText) {
        setProgressVisible(true);
        new Thread(() -> {

            decode(passwordToDecode.getText().toString());

            context.runOnUiThread(() -> {
                if (decodedMessage != null && !decodedMessage.isEmpty()) {
                    decodedText.setText(decodedMessage);
                    Toasty.success(context, getString(R.string.message_decoding_success), Toast.LENGTH_SHORT, true).show();
                    dialog.show();
                } else {
                    decodedText.setText(DEFAULT_TEXT_MESSAGE);
                    if (neededPassword) {
                        neededPassword = false;
                        passwordToDecode.setError(MESSAGE_MISSING_PASSWORD);
                    }
                    Toasty.error(context, getString(R.string.error_decoding_failed), Toast.LENGTH_SHORT, true).show();
                }
                setProgressVisible(false);
            });
        }
        ).start();
    }

    @Override
    public void decode(String password) {
        try {
            if (password != null && !password.isEmpty()) {
                decodedMessage = Steganographer.withInput(bmpImage)
                        .withPassword(password)
                        .decode()
                        .intoString();

            } else decodedMessage = Steganographer.withInput(bmpImage).decode().intoString();

            String d = decodedMessage;
            decodedMessage = Coders.getCoder(choiceDec).decompress(d);

        } catch (Exception | OutOfMemoryError e) {
            if (e instanceof PasswordMissingException) {
                neededPassword = true;
                Looper.prepare();
                context.runOnUiThread(() ->
                        YoYo.with(Techniques.Tada)
                                .duration(200)
                                .repeat(2)
                                .playOn(passwordToDecode));
            }
            decodedMessage = "";
        }
    }


    public void setProgressVisible(boolean vis) {
        if (vis) {
            progress.setVisibility(View.VISIBLE);
            decPage.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            decPage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void animate() {
        if (getActivity() != null) {
            setUiVisibility(View.INVISIBLE);

            YoYo.with(Techniques.SlideInRight).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    imageContainer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    YoYo.with(Techniques.SlideInRight).withListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            passwordContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            YoYo.with(Techniques.SlideInRight).withListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    decompressions.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    YoYo.with(Techniques.SlideInRight).withListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            decodeButton.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            YoYo.with(Techniques.SlideInRight).withListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {
                                                    receiveButton.setVisibility(View.VISIBLE);
                                                }
                                            }).duration(200).delay(0).playOn(receiveButton);
                                        }
                                    }).duration(200).delay(0).playOn(decodeButton);
                                }
                            }).duration(200).delay(0).playOn(decompressions);
                        }
                    }).duration(200).delay(0).playOn(passwordContainer);
                }
            }).duration(200).delay(0).playOn(imageContainer);
        }
    }


    @Override
    protected void setUiVisibility(int vis) {
        imageContainer.setVisibility(vis);
        passwordContainer.setVisibility(vis);
        decompressions.setVisibility(vis);
        decodeButton.setVisibility(vis);
        receiveButton.setVisibility(vis);
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
                        bmpImage = resource;

                        if (SettingsActivity.is_animations_enabled) {

                            context.runOnUiThread(() -> {
                                imageTextMessage.setVisibility(View.INVISIBLE);
                                decodeImage.setVisibility(View.INVISIBLE);

                                YoYo.with(Techniques.SlideInLeft).onStart((Animator a) -> {

                                    decodeImage.setVisibility(View.VISIBLE);
                                    GlideApp.with(context)
                                            .load(bmpImage)
                                            .into(decodeImage);

                                }).duration(400).delay(0)
                                        .playOn(imageContainer);
                                Toasty.info(context, getString(R.string.message_image_selected), Toast.LENGTH_SHORT, true).show();
                            });

                        } else {
                            context.runOnUiThread(() -> {
                                imageTextMessage.setVisibility(View.INVISIBLE);
                                decodeImage.setVisibility(View.VISIBLE);
                                GlideApp.with(context).load(bmpImage).into(decodeImage);
                                Toasty.info(context, getString(R.string.message_image_selected), Toast.LENGTH_SHORT, true).show();
                            });
                        }
                        return false;

                    }
                }).submit();
    }
}