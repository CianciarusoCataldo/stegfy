package com.stegfy.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.stegfy.R;
import com.stegfy.fragment.FragmentEncode;
import com.stegfy.utils.compress.Coders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static com.stegfy.Constants.ERROR_FILE_CANT_OPEN;
import static com.stegfy.Constants.ERROR_SHORT_PASSWORD;
import static com.stegfy.Constants.FILE_CHOOSER_TITLE;
import static com.stegfy.Constants.FILE_TYPE_TEXT;
import static com.stegfy.Constants.MIN_PASSWORD_LENGTH;
import static com.stegfy.Constants.PASSWORD;
import static com.stegfy.Constants.SECRET_DATA_KEY;
import static com.stegfy.Constants.TEXTFILE;

/**
 * Define the structure of the Activity that handle the input text, to encode, from user.
 *
 * @author Cataldo Cianciaruso
 */
public class TextDialogActivity extends AppCompatActivity {

    /**
     * Contains the user text to encode
     **/
    private EditText text_preview;

    /**
     * The user text to encode
     **/
    private String secretText;

    /**
     * Contains the password to encode with (if set by user)
     */
    private TextView psw_field;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encode_text_dialog_layout);

        AsyncTask.execute(() -> {
            psw_field = findViewById(R.id.password_encode);
            text_preview = findViewById(R.id.text_preview);

            findViewById(R.id.browse_file_button).setOnClickListener(v -> showFileChooser());

            findViewById(R.id.cancel_button).setOnClickListener(v -> finish());

            findViewById(R.id.ok_button).setOnClickListener(v -> {
                secretText = text_preview.getText().toString();
                getSecretText();
            });
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filePath = null;
        try {
            if (requestCode == TEXTFILE) {
                if (resultCode == RESULT_OK) {
                    Uri fileUri = data.getData();
                    filePath = Objects.requireNonNull(fileUri).getPath();
                    secretText = readTextFromUri(fileUri);
                    text_preview.setText(secretText);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, ERROR_FILE_CANT_OPEN + filePath,
                    Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Handle the radio-buttons action in {@link R.layout#encode_text_dialog_layout} layout.
     * They are used to set the text compression algorithm to use during encoding.
     *
     * @param view : selected radio-button
     */
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.none:
                if (checked)
                    FragmentEncode.choiceComp = Coders.Algoritms.NONE;
                break;

            case R.id.lzw:
                if (checked)
                    FragmentEncode.choiceComp = Coders.Algoritms.LZW;
                break;
            case R.id.arithmetic:
                if (checked)
                    FragmentEncode.choiceComp = Coders.Algoritms.ARITM;
                break;
            case R.id.huffman:
                if (checked)
                    FragmentEncode.choiceComp = Coders.Algoritms.HUFFMAN;
                break;
        }
    }


    /**
     * Encode the user input text
     */
    private void getSecretText() {
        if (psw_field.length() > 0 && psw_field.length() < MIN_PASSWORD_LENGTH) {
            psw_field.setError(ERROR_SHORT_PASSWORD);

        } else if (secretText == null || secretText.isEmpty()) {
            Toasty.warning(this, getString(R.string.error_no_text), Toast.LENGTH_SHORT, true).show();

        } else {
            Intent intent = new Intent();
            secretText = Coders.getCoder(FragmentEncode.choiceComp).compress(secretText);
            intent.putExtra(PASSWORD, psw_field.getText().toString());
            intent.putExtra(SECRET_DATA_KEY, secretText);
            setResult(RESULT_OK, intent);
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = getCurrentFocus();
            if (view == null) {
                view = new View(this);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            finish();
        }
    }

    /**
     * Show default file manager to choose a text file.
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(FILE_TYPE_TEXT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, FILE_CHOOSER_TITLE),
                    TEXTFILE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.msg_install_file_manager,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Read a text from a given URI
     *
     * @param uri : the URI of the file to read from
     * @return : the text red from file
     * @throws IOException: if the file is not available
     */
    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        Objects.requireNonNull(inputStream).close();
        return stringBuilder.toString();
    }
}
