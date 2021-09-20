package com.amherickeyboard.voicetyping.translator.ime;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.amherickeyboard.voicetyping.translator.R;
import com.amherickeyboard.voicetyping.translator.ThemesSharedPreference;
import com.amherickeyboard.voicetyping.translator.Translation;
import com.amherickeyboard.voicetyping.translator.activity.SplashScreenActivity;
import com.amherickeyboard.voicetyping.translator.ads.AdaptiveAds;
import com.amherickeyboard.voicetyping.translator.firebase.Analytics;
import com.amherickeyboard.voicetyping.translator.globalClass.GlobalClass;
import com.amherickeyboard.voicetyping.translator.service.KeyCodes;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.inputmethodservice.Keyboard.KEYCODE_SHIFT;
import static android.view.View.VISIBLE;
import static com.amherickeyboard.voicetyping.translator.service.KeyCodes.DONE;

public class SimpleIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, RecognitionListener {

    public KeyboardView kv;
    public static RelativeLayout parent, parentLayout;
    private Keyboard keyboardEng, numericKeyboard, numericAhmeric, numericShiftKeyboard, amhericKeyboard, symbolsShiftAmharic;
    private ConstraintLayout translatorLayout, itemsLayoutKeyboard;
    private LinearLayout itemsLayout;
    private ImageView mic, textTranslate, conversation, dictionary, theme, keyBoardBtn, back, keyBoardBtnFilled, translateBtn, closeBtn, okBtn, swapBtn;
    private EditText etText;
    private EmojiconsPopup popupWindow;
    LinearLayout bottomlinearLayout;
    private StringBuilder mComposing = new StringBuilder();
    private LottieAnimationView lottieAnimationView;
    private boolean caps = false;
    public InputConnection ic;
    public SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    String translatetoText = "";
    String inputCode, outputCode;
    private int spinnnerLeftPosition = 0;
    String spinnnerLeftName;
    String spinnnerRightName;
    private int spinnerRightPosition = 0;
    private Spinner leftSpinner, rightSpinner;
    private String[] country;
    private String[] country_code;
    StringBuilder inputStringbuilder = new StringBuilder();
    public String mInputString;
    public StringBuilder translationStringbuilder;
    public boolean isTranslatebuttonPress = false;
    boolean whiteSpinnerTextView;
    View popupView;
    public int builderLength;
    public int poss;
    ExtractedText et;
    private final boolean isAmhericToEng = false;
    private boolean isEngToAmheric = false;
    FrameLayout layout_banner;
    FrameLayout adContainerView;
    AdView adView;
    int position = -1;

    @Override
    public void onReadyForSpeech(Bundle params) { }

    @Override
    public void onBeginningOfSpeech() { }

    @Override
    public void onRmsChanged(float rmsdB) { }

    @Override
    public void onBufferReceived(byte[] buffer) { }

    @Override
    public void onEndOfSpeech() { }

    @Override
    public void onError(int error) {
        kv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String speechResultText = " " + Objects.requireNonNull(results.getStringArrayList("results_recognition")).get(0);
        String result = matches.get(0);
        Log.d("***result", result + "");
        result.concat("");
        if (speechResultText != null && speechResultText.length() > 0) {
            if (isEngToAmheric) {
                amherictoEng(speechResultText, "en-GB", "am-ET", true);
            } else {
                ic = getCurrentInputConnection();
                ic.commitText(speechResultText + " ", 1);
            }
        } else {
            ic = getCurrentInputConnection();
            ic.commitText(result + " ", 1);
        }
        kv.setVisibility(View.VISIBLE);
        bottomlinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    private void setRecognitionListner(String language) {
        if (isInternetOn()) {
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mSpeechRecognizer.setRecognitionListener(this);
            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            this.mSpeechRecognizerIntent.putExtra("android.speech.extra.LANGUAGE", "en");
            this.mSpeechRecognizerIntent.putExtra("calling_package", getPackageName());
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        } else {
            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
            bottomlinearLayout.setVisibility(View.GONE);
            kv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateInputView() {
        initializeKeyboards();
        parent = (RelativeLayout) getLayoutInflater().inflate(R.layout.keyboard, null);
        isEngToAmheric = false;
        itemsLayout = parent.findViewById(R.id.itemsLayout);
        itemsLayout.setVisibility(View.VISIBLE);
        kv = parent.findViewById(R.id.keyboard);
        kv.setKeyboard(numericKeyboard);
        kv.setOnKeyboardActionListener(this);
        parentLayout = parent.findViewById(R.id.parentLayout);
        layout_banner = parent.findViewById(R.id.mainFrame);
        bottomlinearLayout = parent.findViewById(R.id.bottomLinearLayout);
        itemsLayoutKeyboard = parent.findViewById(R.id.itemsLayoutKeyboard);
        itemsLayoutKeyboard.setVisibility(View.VISIBLE);
        mic = parent.findViewById(R.id.mic);
        back = parent.findViewById(R.id.back);
        textTranslate = parent.findViewById(R.id.textTranslateActivity);
        conversation = parent.findViewById(R.id.conversation);
        dictionary = parent.findViewById(R.id.dictionary);
        theme = parent.findViewById(R.id.theme);
        keyBoardBtn = parent.findViewById(R.id.settingsCard);
        keyBoardBtnFilled = parent.findViewById(R.id.settingsFilled);
        translateBtn = parent.findViewById(R.id.textTranslate);
        translatorLayout = parent.findViewById(R.id.translatorLayout);
        closeBtn = parent.findViewById(R.id.backBtn);
        etText = parent.findViewById(R.id.etText);
        okBtn = parent.findViewById(R.id.okBtn);
        leftSpinner = parent.findViewById(R.id.leftSpinner);
        rightSpinner = parent.findViewById(R.id.rightSpinner);
        swapBtn = parent.findViewById(R.id.swapBtn);
        lottieAnimationView = parent.findViewById(R.id.animation);

        try {
            position = new ThemesSharedPreference(SimpleIME.this).getThemePosition();
        } catch (Exception e) {
            Log.e("TAG", "onCreateInputView: "+e.getMessage());
        }
        Log.e("TAG", "onCreateInputView: "+position );
        if (position == 0) {
            firstTheme();
        } else if (position == 1) {
            secondTheme();
        } else if (position == 2) {
            thirdTheme();
        } else if (position == 3) {
            fourthTheme();
        } else if (position == 4) {
            fifthTheme();
        } else if (position == 5) {
            sixthTheme();
        }

        allClicks();
        getDataJson();
        spinner_input();
        spinner_output();
        translationStringbuilder = new StringBuilder();
        kv.setKeyboard(amhericKeyboard);
        return parent;
    }

    private void controlAd() {
        String packageName = getCurrentInputEditorInfo().packageName;
        if (!packageName.isEmpty()) {
            switch (packageName) {
                case "com.amherickeyboards.voicetyping.translator":
                case "com.google.android.googlequicksearchbox":
                case "com.google.android.talk":
                case "com.google.android.gm":
                case "com.android.chrome":
                case "com.google.android.youtube":
                case "com.android.vending":
                    layout_banner.setVisibility(View.GONE);
                    break;
                default: {
                    adRequest();
                }
            }
        }
    }

    private void adRequest() {
        layout_banner.setVisibility(View.VISIBLE);
        AdaptiveAds adaptiveAds = new AdaptiveAds(this);
        adContainerView = parent.findViewById(R.id.adContainerView);
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_id));
        adContainerView.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdSize(adaptiveAds.getAdSize());
        adView.loadAd(adRequest);
    }

    private void initializeKeyboards() {
        keyboardEng = new Keyboard(this, R.xml.qwerty_new_keyboard);
        numericKeyboard = new Keyboard(this, R.xml.symbols_new);
        numericShiftKeyboard = new Keyboard(this, R.xml.symbols_shift_new);
        amhericKeyboard = new Keyboard(this, R.xml.amheric);
        numericAhmeric = new Keyboard(this, R.xml.symbols_new_ahm);
        symbolsShiftAmharic = new Keyboard(this, R.xml.symbols_shift_amharic);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void allClicks() {
        lottieAnimationView.setAnimation("mic_animation.json");
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);

        mic.setOnClickListener(v -> {
            if (bottomlinearLayout.getVisibility() == View.VISIBLE) {
                bottomlinearLayout.setVisibility(View.GONE);
            } else {
                if (isInternetOn()) {
                    bottomlinearLayout.setVisibility(View.VISIBLE);
                    if (kv.getKeyboard() == amhericKeyboard) {
                        setRecognitionListner("am");
                    } else {
                        setRecognitionListner("en");
                    }
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                } else {
                    Toast.makeText(SimpleIME.this, "no internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lottieAnimationView.setOnClickListener(v -> {
            kv.setVisibility(View.GONE);
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            mSpeechRecognizer.startListening(intent);
        });

        back.setOnClickListener(v -> {
            bottomlinearLayout.setVisibility(View.GONE);
            mSpeechRecognizer.stopListening();
            kv.setVisibility(View.VISIBLE);
            isTranslatebuttonPress = false;
        });

        textTranslate.setOnClickListener(v -> {
            sendIntent(2);
        });

        conversation.setOnClickListener(v -> {
            sendIntent(1);
        });

        dictionary.setOnClickListener(v -> {
            sendIntent(3);
        });

        theme.setOnClickListener(v -> {
            sendIntent(4);
        });

        keyBoardBtn.setOnClickListener(v -> {
            kv.setKeyboard(keyboardEng);
            keyBoardBtn.setVisibility(View.GONE);
            keyBoardBtnFilled.setVisibility(View.VISIBLE);
            if (position == 0) {
                if (kv.getKeyboard() == keyboardEng) {
                    List<Keyboard.Key> keys = kv.getKeyboard().getKeys();
                    kv.invalidateKey(19);
                    kv.invalidateKey(27);
                    kv.invalidateKey(28);
                    kv.invalidateKey(32);
                    keys.get(19).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shift_white, getTheme());
                    keys.get(27).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_white, getTheme());
                    keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_123_white, getTheme());
                    keys.get(32).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.enter_white_clr, getTheme());
                    kv.invalidateAllKeys();
                }
            } else {
                List<Keyboard.Key> keys = kv.getKeyboard().getKeys();
                kv.invalidateKey(19);
                kv.invalidateKey(27);
                kv.invalidateKey(28);
                kv.invalidateKey(32);
                keys.get(19).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shit, getTheme());
                keys.get(27).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_icon, getTheme());
                keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_123, getTheme());
                keys.get(32).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_enter, getTheme());
                kv.invalidateAllKeys();
            }
        });

        keyBoardBtnFilled.setOnClickListener(v -> {
            kv.setKeyboard(amhericKeyboard);
            keyBoardBtnFilled.setVisibility(View.GONE);
            keyBoardBtn.setVisibility(View.VISIBLE);
            if (position == 0) {
                if (kv.getKeyboard() == amhericKeyboard) {
                    List<Keyboard.Key> keys = kv.getKeyboard().getKeys();
                    kv.invalidateKey(29);
                    kv.invalidateKey(30);
                    kv.invalidateKey(35);
                    kv.invalidateKey(19);
                    keys.get(29).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_white, getTheme());
                    keys.get(30).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_123_white, getTheme());
                    keys.get(35).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.enter_white_clr, getTheme());
                    keys.get(19).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shift_white, getTheme());
                    kv.invalidateAllKeys();
                }
            } else {
                List<Keyboard.Key> keys = kv.getKeyboard().getKeys();
                kv.invalidateKey(29);
                kv.invalidateKey(30);
                kv.invalidateKey(35);
                kv.invalidateKey(19);
                keys.get(29).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_icon, getTheme());
                keys.get(30).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_123, getTheme());
                keys.get(35).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_enter, getTheme());
                keys.get(19).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shit, getTheme());
                kv.invalidateAllKeys();
            }
        });

        translateBtn.setOnClickListener(v -> {
            translatorLayout.setVisibility(View.VISIBLE);
            itemsLayout.setVisibility(View.GONE);
            etText.setFocusableInTouchMode(true);
            etText.requestFocus();
            isTranslatebuttonPress = true;
        });

        closeBtn.setOnClickListener(v -> {
            isTranslatebuttonPress = false;
            itemsLayout.setVisibility(View.VISIBLE);
            translatorLayout.setVisibility(View.GONE);
            mComposing = new StringBuilder();
            etText.setText("");
            etText.setFocusable(false);
        });

        okBtn.setOnClickListener(v -> {
            if (isInternetOn()) {
                if (etText.getText().toString().isEmpty()) {
                    Toast.makeText(SimpleIME.this, "Enter text to translate", Toast.LENGTH_SHORT).show();
                } else {
                    String aaa = etText.getText().toString().trim();
                    try {
                        translateText(aaa, inputCode, outputCode);
                        etText.setText("");
                        mComposing.delete(0, mComposing.length());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(SimpleIME.this, "no internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        etText.setOnTouchListener((v, event) -> {
            etText.setTextIsSelectable(true);
            etText.setFocusable(true);
            etText.requestFocus();
            etText.setFocusableInTouchMode(true);
            etText.setCursorVisible(true);
            return false;
        });

        swapBtn.setOnClickListener(v -> {
            leftSpinner.setSelection(spinnerRightPosition);
            rightSpinner.setSelection(spinnnerLeftPosition);
        });
    }

    public void sendIntent(int address) {
        if (!GlobalClass.isFourgrounded) {
            Intent dialogIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dialogIntent.putExtra("activityAddress", address);
            startActivity(dialogIntent);
        }
    }

    private void firstTheme() {
        kv = parent.findViewById(R.id.keyboard);
        kv.setKeyboard(amhericKeyboard);
        kv.setOnKeyboardActionListener(SimpleIME.this);
        kv.setBackgroundResource(R.color.keybg);
        itemsLayout.setBackgroundResource(R.color.keybg);
        bottomlinearLayout.setBackgroundColor(getResources().getColor(R.color.keybg));
        translatorLayout.setBackgroundResource(R.color.keybg);
        kv.setVisibility(View.VISIBLE);
        okBtn.setImageResource(R.drawable.ic_black_translate);
        mic.setImageResource(R.drawable.ic_black_mic);
        textTranslate.setImageResource(R.drawable.ic_text_translate_keyboard);
        conversation.setImageResource(R.drawable.ic_speak_translate_keyboard);
        dictionary.setImageResource(R.drawable.ic_dictionary_keyboard);
        keyBoardBtn.setImageResource(R.drawable.ic_black_keyboard_change_button);
        keyBoardBtnFilled.setImageResource(R.drawable.ic_black_keyboard_change_button);
        theme.setImageResource(R.drawable.ic_black_theme);
        translateBtn.setImageResource(R.drawable.ic_black_translate);
        back.setImageResource(R.drawable.ic_black_back_icon);
        closeBtn.setImageResource(R.drawable.ic_black_back_icon);
        swapBtn.setImageResource(R.drawable.ic_switch_icon_keyboard);
        whiteSpinnerTextView = false;
        keyBoardBtnFilled.setVisibility(View.GONE);
        keyBoardBtn.setVisibility(View.VISIBLE);

        List<Keyboard.Key> keys = kv.getKeyboard().getKeys();
        kv.invalidateKey(29);
        kv.invalidateKey(30);
        kv.invalidateKey(35);
        kv.invalidateKey(19);
        keys.get(29).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_white, getTheme());
        keys.get(30).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_123_white, getTheme());
        keys.get(35).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.enter_white_clr, getTheme());
        keys.get(19).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shift_white, getTheme());
        kv.invalidateAllKeys();
        spinner_input();
        spinner_output();
    }

    private void secondTheme() {
        kv = parent.findViewById(R.id.keyboardOne);
        kv.setKeyboard(amhericKeyboard);
        kv.setOnKeyboardActionListener(SimpleIME.this);
        kv.setBackgroundResource(R.drawable.keyboard_bg_updated);
        itemsLayout.setBackgroundResource(R.color.itemLayoutbgClr);
        translatorLayout.setBackgroundResource(R.color.itemLayoutbgClr);
        bottomlinearLayout.setBackgroundResource(R.drawable.keyboard_bg_updated);
        kv.setVisibility(View.VISIBLE);
        whiteSpinnerTextView = true;
        mic.setImageResource(R.drawable.ic_white_mic);
        textTranslate.setImageResource(R.drawable.ic_text_translate_keyboard_white);
        conversation.setImageResource(R.drawable.ic_speak_translate_keyboard_white);
        dictionary.setImageResource(R.drawable.ic_dictionary_keyboard_white);
        keyBoardBtn.setImageResource(R.drawable.ic_white_keyboard_change_button);
        theme.setImageResource(R.drawable.ic_white_theme);
        translateBtn.setImageResource(R.drawable.ic_white_translate);
        okBtn.setImageResource(R.drawable.ic_white_translate);
        back.setImageResource(R.drawable.ic_white_back_icon);
        closeBtn.setImageResource(R.drawable.ic_white_back_icon);
        swapBtn.setImageResource(R.drawable.ic_switch_icon_keyboard_white);
        keyBoardBtnFilled.setImageResource(R.drawable.ic_white_keyboard_change_button);
        keyBoardBtnFilled.setVisibility(View.GONE);
        keyBoardBtn.setVisibility(View.VISIBLE);

        List<Keyboard.Key> keys = kv.getKeyboard().getKeys();
        kv.invalidateKey(27);
        kv.invalidateKey(32);
        kv.invalidateKey(28);
        kv.invalidateKey(19);
        keys.get(27).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_icon, getTheme());
        keys.get(32).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_enter, getTheme());
        keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_123, getTheme());
        keys.get(19).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shit, getTheme());

        kv.invalidateAllKeys();
        spinner_input();
        spinner_output();
    }

    private void thirdTheme() {
        kv = parent.findViewById(R.id.keyboardTwo);
        kv.setKeyboard(amhericKeyboard);
        kv.setOnKeyboardActionListener(SimpleIME.this);
        kv.setBackgroundResource(R.drawable.ic_background_black);
        itemsLayout.setBackgroundResource(R.drawable.ic_background_black);
        bottomlinearLayout.setBackgroundResource(R.drawable.ic_background_black);
        translatorLayout.setBackgroundResource(R.drawable.ic_background_black);
        mic.setImageResource(R.drawable.ic_white_mic);
        textTranslate.setImageResource(R.drawable.ic_text_translate_keyboard_white);
        conversation.setImageResource(R.drawable.ic_speak_translate_keyboard_white);
        dictionary.setImageResource(R.drawable.ic_dictionary_keyboard_white);
        keyBoardBtn.setImageResource(R.drawable.ic_white_keyboard_change_button);
        theme.setImageResource(R.drawable.ic_white_theme);
        translateBtn.setImageResource(R.drawable.ic_white_translate);
        okBtn.setImageResource(R.drawable.ic_white_translate);
        back.setImageResource(R.drawable.ic_white_back_icon);
        closeBtn.setImageResource(R.drawable.ic_white_back_icon);
        swapBtn.setImageResource(R.drawable.ic_switch_icon_keyboard_white);
        swapBtn.setImageResource(R.drawable.ic_switch_icon_keyboard);
        keyBoardBtnFilled.setVisibility(View.GONE);
        keyBoardBtn.setVisibility(View.VISIBLE);
        kv.setVisibility(View.VISIBLE);
        whiteSpinnerTextView = true;

        List<Keyboard.Key> keys = kv.getKeyboard().getKeys();
        kv.invalidateKey(27);
        kv.invalidateKey(32);
        kv.invalidateKey(28);
        kv.invalidateKey(19);
        keys.get(27).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_icon, getTheme());
        keys.get(32).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_enter, getTheme());
        keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_123, getTheme());
        keys.get(19).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shit, getTheme());

        kv.invalidateAllKeys();
        spinner_input();
        spinner_output();
    }

    private void fourthTheme() {
        kv = parent.findViewById(R.id.keyboardThree);
        kv.setKeyboard(amhericKeyboard);
        kv.setOnKeyboardActionListener(SimpleIME.this);
        kv.setBackgroundResource(R.drawable.keyboard_three_background);
        itemsLayout.setBackgroundResource(R.drawable.keyboard_three_items_background);
        translatorLayout.setBackgroundResource(R.drawable.keyboard_three_items_background);
        bottomlinearLayout.setBackgroundResource(R.drawable.keyboard_three_background);
        kv.setVisibility(View.VISIBLE);
        whiteSpinnerTextView = true;
        mic.setImageResource(R.drawable.ic_white_mic);
        textTranslate.setImageResource(R.drawable.ic_text_translate_keyboard_white);
        conversation.setImageResource(R.drawable.ic_speak_translate_keyboard_white);
        dictionary.setImageResource(R.drawable.ic_dictionary_keyboard_white);
        keyBoardBtn.setImageResource(R.drawable.ic_white_keyboard_change_button);
        theme.setImageResource(R.drawable.ic_white_theme);
        translateBtn.setImageResource(R.drawable.ic_white_translate);
        okBtn.setImageResource(R.drawable.ic_white_translate);
        back.setImageResource(R.drawable.ic_white_back_icon);
        closeBtn.setImageResource(R.drawable.ic_white_back_icon);
        swapBtn.setImageResource(R.drawable.ic_switch_icon_keyboard_white);
        keyBoardBtnFilled.setImageResource(R.drawable.ic_white_keyboard_change_button);
        keyBoardBtnFilled.setVisibility(View.GONE);
        keyBoardBtn.setVisibility(View.VISIBLE);

        List<Keyboard.Key> keys = kv.getKeyboard().getKeys();
        kv.invalidateKey(27);
        kv.invalidateKey(32);
        kv.invalidateKey(28);
        kv.invalidateKey(19);
        keys.get(27).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_icon, getTheme());
        keys.get(32).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_enter, getTheme());
        keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_123, getTheme());
        keys.get(19).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shit, getTheme());
        kv.invalidateAllKeys();
        spinner_input();
        spinner_output();
    }

    private void fifthTheme() {
        kv = parent.findViewById(R.id.keyboardFour);
        kv.setKeyboard(amhericKeyboard);
        kv.setOnKeyboardActionListener(SimpleIME.this);
        kv.setBackgroundResource(R.drawable.keyboard_four_background);
        itemsLayout.setBackgroundResource(R.drawable.keyboard_four_items_background);
        translatorLayout.setBackgroundResource(R.drawable.keyboard_four_items_background);
        bottomlinearLayout.setBackgroundResource(R.drawable.keyboard_four_background);
        kv.setVisibility(View.VISIBLE);
        whiteSpinnerTextView = true;
        mic.setImageResource(R.drawable.ic_white_mic);
        textTranslate.setImageResource(R.drawable.ic_text_translate_keyboard_white);
        conversation.setImageResource(R.drawable.ic_speak_translate_keyboard_white);
        dictionary.setImageResource(R.drawable.ic_dictionary_keyboard_white);
        keyBoardBtn.setImageResource(R.drawable.ic_white_keyboard_change_button);
        theme.setImageResource(R.drawable.ic_white_theme);
        translateBtn.setImageResource(R.drawable.ic_white_translate);
        okBtn.setImageResource(R.drawable.ic_white_translate);
        back.setImageResource(R.drawable.ic_white_back_icon);
        closeBtn.setImageResource(R.drawable.ic_white_back_icon);
        swapBtn.setImageResource(R.drawable.ic_switch_icon_keyboard_white);
        keyBoardBtnFilled.setImageResource(R.drawable.ic_white_keyboard_change_button);
        keyBoardBtnFilled.setVisibility(View.GONE);
        keyBoardBtn.setVisibility(View.VISIBLE);

        List<Keyboard.Key> keys = kv.getKeyboard().getKeys();
        kv.invalidateKey(27);
        kv.invalidateKey(32);
        kv.invalidateKey(28);
        kv.invalidateKey(19);
        keys.get(27).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_icon, getTheme());
        keys.get(32).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_enter, getTheme());
        keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_123, getTheme());
        keys.get(19).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shit, getTheme());
        kv.invalidateAllKeys();
        spinner_input();
        spinner_output();
    }

    private void sixthTheme() {
        kv = parent.findViewById(R.id.keyboardFive);
        kv.setKeyboard(amhericKeyboard);
        kv.setOnKeyboardActionListener(SimpleIME.this);
        kv.setBackgroundResource(R.drawable.keyboard_five_background);
        itemsLayout.setBackgroundResource(R.drawable.keyboard_five_items_background);
        translatorLayout.setBackgroundResource(R.drawable.keyboard_five_items_background);
        bottomlinearLayout.setBackgroundResource(R.drawable.keyboard_five_background);
        kv.setVisibility(View.VISIBLE);
        whiteSpinnerTextView = true;
        mic.setImageResource(R.drawable.ic_white_mic);
        textTranslate.setImageResource(R.drawable.ic_text_translate_keyboard_white);
        conversation.setImageResource(R.drawable.ic_speak_translate_keyboard_white);
        dictionary.setImageResource(R.drawable.ic_dictionary_keyboard_white);
        keyBoardBtn.setImageResource(R.drawable.ic_white_keyboard_change_button);
        theme.setImageResource(R.drawable.ic_white_theme);
        translateBtn.setImageResource(R.drawable.ic_white_translate);
        okBtn.setImageResource(R.drawable.ic_white_translate);
        back.setImageResource(R.drawable.ic_white_back_icon);
        closeBtn.setImageResource(R.drawable.ic_white_back_icon);
        swapBtn.setImageResource(R.drawable.ic_switch_icon_keyboard_white);
        keyBoardBtnFilled.setImageResource(R.drawable.ic_white_keyboard_change_button);
        keyBoardBtnFilled.setVisibility(View.GONE);
        keyBoardBtn.setVisibility(View.VISIBLE);

        List<Keyboard.Key> keys = kv.getKeyboard().getKeys();
        kv.invalidateKey(27);
        kv.invalidateKey(32);
        kv.invalidateKey(28);
        kv.invalidateKey(19);
        keys.get(27).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_icon, getTheme());
        keys.get(32).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_enter, getTheme());
        keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_123, getTheme());
        keys.get(19).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shit, getTheme());
        kv.invalidateAllKeys();
        spinner_input();
        spinner_output();
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        char code = (char) primaryCode;

        try {
            ic = getCurrentInputConnection();
            et = ic.getExtractedText(new ExtractedTextRequest(), 0);
            if (et != null && et.text.toString().length() == 0) {
                translationStringbuilder.delete(0, translationStringbuilder.length());
            }
            if (et != null) {
                poss = et.startOffset + et.selectionStart;
            }
            mInputString = inputStringbuilder.toString();
        } catch (Exception e) {
        }

        playClick(primaryCode);
        switch (primaryCode) {
            case KeyCodes.TOGGLECODEENGLISH:
                ToggleEngAmh(kv, R.drawable.ic_amharic_toggle_button, R.drawable.ic_english_toggle_button);
                break;
            case KeyCodes.AmharicNumericKeyboard:
                kv.setKeyboard(numericAhmeric);
                break;
            case KeyCodes.AmharicNumericShiftKeyboard:
                kv.setKeyboard(symbolsShiftAmharic);
                setNumericAmharicShiftIcons(symbolsShiftAmharic, kv);
                break;
            case KeyCodes.SYMBOLSHIFTAMHBTN:
                kv.setKeyboard(amhericKeyboard);
                break;
            case KeyCodes.NUMERICAMHARICKEYBOARD:
                kv.setKeyboard(numericAhmeric);
                setNumericAmharicIcons(numericAhmeric, kv);
                break;
            case KeyCodes.AMHARICKEYBOARD:
                kv.setKeyboard(amhericKeyboard);
                break;
            case KeyCodes.DEL:
                handleBackspace();
                ic.deleteSurroundingText(0, 0);
                if (inputStringbuilder.length() > 0) {
                    String text = inputStringbuilder.substring(0, inputStringbuilder.length() - 1);
                    inputStringbuilder.delete(0, inputStringbuilder.length());
                    inputStringbuilder.append(text);
                    if (inputStringbuilder.length() != 0) {
                        inputStringbuilder.setLength(inputStringbuilder.length() - 1);
                    }
                } else {
                    inputStringbuilder.delete(0, inputStringbuilder.length());
                }

                if (translationStringbuilder.length() > 0) {
                    String text = translationStringbuilder.substring(0, translationStringbuilder.length() - 1);
                    translationStringbuilder.delete(0, translationStringbuilder.length());
                    translationStringbuilder.append(text);
                    if (translationStringbuilder.length() != 0) {
                        translationStringbuilder.setLength(translationStringbuilder.length() - 1);
                    }
                } else {
                    translationStringbuilder.delete(0, translationStringbuilder.length());
                }

                et = ic.getExtractedText(new ExtractedTextRequest(), 0);
                String backspacedText = et.text.toString();
                translationStringbuilder.delete(0, translationStringbuilder.length());
                translationStringbuilder.append(backspacedText);
                poss = et.startOffset + et.selectionStart;
                builderLength = translationStringbuilder.length();
                break;
            case KEYCODE_SHIFT:
                caps = !caps;
                kv.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case DONE:
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            case KeyCodes.QWERTY:
                kv.setKeyboard(keyboardEng);
                break;
            case KeyCodes.numericKeyboard:
                kv.setKeyboard(numericKeyboard);
                setNumericEnglishIcon(numericKeyboard, kv);
                break;
            case KeyCodes.numericShiftKeyboard:
                kv.setKeyboard(numericShiftKeyboard);
                setNumericEnglishShiftIcons(numericShiftKeyboard, kv);
                break;
            case KeyCodes.EMOJI:
                inputStringbuilder.delete(0, inputStringbuilder.length());
                showEmoticons();
                break;
            case KeyCodes.SPACECODE:
                space_new(code);
                break;
            default:
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                if (etText.isFocusable()) {
                    Log.d("ISFOCUSEd", etText.isFocusable() + "");
                    mComposing.append(code).toString();
                    etText.setText(mComposing.toString());
                    etText.setCursorVisible(true);
                    etText.setSelection(mComposing.length());
                } else {
                    ic.commitText(String.valueOf(code), 1);
                    inputStringbuilder.append(code);
                }
        }
    }

    @Override
    public void onPress(int primaryCode) {
        Log.d("ONPRESS", primaryCode + "");
        if (primaryCode == KeyCodes.SPACECODE || primaryCode == KeyCodes.QWERTY || primaryCode == KeyCodes.SYMBOLSHIFTAMHBTN || primaryCode == KeyCodes.AMHARICKEYBOARD || primaryCode == KeyCodes.NUMERICAMHARICKEYBOARD || primaryCode == KeyCodes.AmharicNumericShiftKeyboard || primaryCode == KeyCodes.DEL || primaryCode == KEYCODE_SHIFT || primaryCode == KeyCodes.numericKeyboard || primaryCode == DONE || primaryCode == KeyCodes.numericShiftKeyboard || primaryCode == KeyCodes.TOGGLECODEENGLISH) {
            kv.setPreviewEnabled(false);
        } else {
            kv.setPreviewEnabled(false);
        }
    }

    private void setNumericAmharicIcons(Keyboard keyboard, KeyboardView keyboardView) {
        if (position == 0) {
            if (kv.getKeyboard() == numericAhmeric) {
                keyboardView.setKeyboard(keyboard);
                List<Keyboard.Key> keys = keyboardView.getKeyboard().getKeys();
                keyboardView.invalidateKey(20);
                keyboardView.invalidateKey(28);
                keyboardView.invalidateKey(33);
                keys.get(20).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shift_white, getTheme());
                keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_white, getTheme());
                keys.get(33).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.enter_white_clr, getTheme());
                keyboardView.invalidateAllKeys();
            }
        } else {
            keyboardView.setKeyboard(keyboard);
            List<Keyboard.Key> keys = keyboardView.getKeyboard().getKeys();
            keyboardView.invalidateKey(20);
            keyboardView.invalidateKey(28);
            keyboardView.invalidateKey(33);
            keys.get(20).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shit, getTheme());
            keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_icon, getTheme());
            keys.get(33).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_enter, getTheme());
            keyboardView.invalidateAllKeys();
        }
    }

    private void setNumericAmharicShiftIcons(Keyboard keyboard, KeyboardView keyboardView) {
        if (position == 0) {
            if (kv.getKeyboard() == symbolsShiftAmharic) {
                keyboardView.setKeyboard(keyboard);
                List<Keyboard.Key> keys = keyboardView.getKeyboard().getKeys();
                keyboardView.invalidateKey(20);
                keyboardView.invalidateKey(28);
                keyboardView.invalidateKey(33);
                keys.get(20).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shift_white, getTheme());
                keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_white, getTheme());
                keys.get(33).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.enter_white_clr, getTheme());
                keyboardView.invalidateAllKeys();
            }
        } else {
            keyboardView.setKeyboard(keyboard);
            List<Keyboard.Key> keys = keyboardView.getKeyboard().getKeys();
            keyboardView.invalidateKey(20);
            keyboardView.invalidateKey(28);
            keyboardView.invalidateKey(33);
            keys.get(20).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shit, getTheme());
            keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_icon, getTheme());
            keys.get(33).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_enter, getTheme());
            keyboardView.invalidateAllKeys();
        }
    }

    private void setNumericEnglishIcon(Keyboard keyboard, KeyboardView keyboardView) {
        if (position == 0) {
            if (kv.getKeyboard() == numericKeyboard) {
                keyboardView.setKeyboard(keyboard);
                List<Keyboard.Key> keys = keyboardView.getKeyboard().getKeys();
                keyboardView.invalidateKey(20);
                keyboardView.invalidateKey(28);
                keyboardView.invalidateKey(34);
                keys.get(20).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shift_white, getTheme());
                keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_white, getTheme());
                keys.get(34).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.enter_white_clr, getTheme());
                keyboardView.invalidateAllKeys();
            }
        } else {
            keyboardView.setKeyboard(keyboard);
            List<Keyboard.Key> keys = keyboardView.getKeyboard().getKeys();
            keyboardView.invalidateKey(20);
            keyboardView.invalidateKey(28);
            keyboardView.invalidateKey(34);
            keys.get(20).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shit, getTheme());
            keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_icon, getTheme());
            keys.get(34).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_enter, getTheme());
            keyboardView.invalidateAllKeys();
        }
    }

    private void setNumericEnglishShiftIcons(Keyboard keyboard, KeyboardView keyboardView) {
        if (position == 0) {
            if (kv.getKeyboard() == numericShiftKeyboard) {
                keyboardView.setKeyboard(keyboard);
                List<Keyboard.Key> keys = keyboardView.getKeyboard().getKeys();
                keyboardView.invalidateKey(20);
                keyboardView.invalidateKey(28);
                keyboardView.invalidateKey(33);
                keys.get(20).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shift_white, getTheme());
                keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_white, getTheme());
                keys.get(33).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.enter_white_clr, getTheme());
                keyboardView.invalidateAllKeys();
            }
        } else {
            keyboardView.setKeyboard(keyboard);
            List<Keyboard.Key> keys = keyboardView.getKeyboard().getKeys();
            keyboardView.invalidateKey(20);
            keyboardView.invalidateKey(28);
            keyboardView.invalidateKey(33);
            keys.get(20).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shit, getTheme());
            keys.get(28).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_icon, getTheme());
            keys.get(33).icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_enter, getTheme());
            keyboardView.invalidateAllKeys();
        }
    }

    @Override
    public void onRelease(int primaryCode) {
        kv.setPreviewEnabled(false);
    }

    @Override
    public void onText(CharSequence text) {
        Log.d("885655", text.toString());
    }

    @Override
    public void swipeDown() { }

    @Override
    public void swipeLeft() {
        kv.setKeyboard(numericKeyboard);
    }

    @Override
    public void swipeRight() {
        kv.setKeyboard(keyboardEng);
    }

    @Override
    public void swipeUp() { }

    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case -4:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case -5:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        kv.setKeyboard(amhericKeyboard);
        setInputView(onCreateInputView());
        keyBoardBtnFilled.setVisibility(View.GONE);
        keyBoardBtn.setVisibility(View.VISIBLE);
        bottomlinearLayout.setVisibility(View.GONE);
        kv.setVisibility(View.VISIBLE);
        etText.setFocusable(false);
        isTranslatebuttonPress = false;
        translatorLayout.setVisibility(View.GONE);
        controlAd();
        Analytics analytics = new Analytics(SimpleIME.this);
        analytics.sendEventAnalytics("Show Keyboard", "On_start_input");
        try {
            popupWindow.dismiss();
            mComposing.delete(0, mComposing.length());
            etText.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEmoticons() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.emoji_listview_layout, null);
        popupWindow = new EmojiconsPopup(popupView, this);
        if (layoutInflater != null) {
            popupWindow.setSizeForSoftKeyboard();
            popupWindow.setSize(ViewGroup.LayoutParams.MATCH_PARENT, parentLayout.getHeight());
            popupWindow.showAtLocation(kv, Gravity.BOTTOM, 0, 0);
            // If the text keyboard closes, also dismiss the emoji popup
            popupWindow.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

                @Override
                public void onKeyboardOpen(int keyBoardHeight) {
                    Log.d("keyBOARDHEIGHT", keyBoardHeight + "");
                }

                @Override
                public void onKeyboardClose() {
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });
            popupWindow.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {
                @Override
                public void onEmojiconClicked(Emojicon emojicon) {
                    inputStringbuilder.append(emojicon.getEmoji());
                    commitTyped(getCurrentInputConnection());

                }
            });
            popupWindow.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {
                @Override
                public void onEmojiconBackspaceClicked(View v) {
                    KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                    handleBackspace();
                }
            });
        }
    }

    private void commitTyped(InputConnection inputConnection) {
        if (inputStringbuilder.length() > 0) {
            inputConnection.commitText(inputStringbuilder, inputStringbuilder.length());
            inputStringbuilder.setLength(0);
        }
    }

    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            etText.setText("");
            etText.setText(mComposing);
            etText.setSelection(mComposing.length());
        } else if (length > 0) {
            etText.setText("");
            mComposing.setLength(0);
            etText.setSelection(mComposing.length());
            getCurrentInputConnection().commitText("", 0);
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
    }

    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    public String translateText(String query, String inputCode, String outputCode)
            throws ExecutionException, InterruptedException {
        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&" +
                "sl=" + inputCode + "&" +
                "tl=" + outputCode +
                "&dt=t&q=" + query.trim().replace(" ", "%20") + "&ie=UTF-8&oe=UTF-8";
        OkHttpHandler okHttpHandlers = new OkHttpHandler();
        String responses = okHttpHandlers.execute(url).get();
        return responses;
    }

    private class OkHttpHandler extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[] objects) {
            Request.Builder builder = new Request.Builder();
            builder.url(objects[0]);
            Request request = builder.build();
            try {
                Response response = client.newCall(request).execute();
                Log.i("***", "doInBackground: -=>" + response.body().toString());
                JSONArray jsonArray = new JSONArray(response.body().string());
                JSONArray jsonArray2 = jsonArray.getJSONArray(0);
                JSONArray jsonArray3 = jsonArray2.getJSONArray(0);
                String data = "";
                data = data + jsonArray3.getString(0);
                Log.i("***", "PARSED Data Response -=>>>" + data);
                return data;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("***", "OkHttpHandler Response -=>>>" + s);
            translatetoText = s;
            ic = getCurrentInputConnection();
            ic.commitText(translatetoText + " ", 1);
        }
    }

    public void spinner_input() {
        try {
            //Creating the ArrayAdapter instance having the country list
            ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //Setting the ArrayAdapter data on the Spinner
            leftSpinner.setAdapter(aa);
            leftSpinner.setSelection(14);

            leftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    Object item = parent.getItemAtPosition(pos);

                    if (whiteSpinnerTextView == true) {
                        TextView textViewLeft = (TextView) leftSpinner.getChildAt(0);
                        textViewLeft.setTextColor(getResources().getColor(R.color.white));
                    } else if (whiteSpinnerTextView == false) {
                        TextView textViewLeft = (TextView) leftSpinner.getChildAt(0);
                        textViewLeft.setTextColor(getResources().getColor(R.color.black));
                    }

                    inputCode = country_code[pos];
                    spinnnerLeftName = country.toString();
                    spinnnerLeftPosition = pos;
                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (Exception e) {

        }
    }

    private void spinner_output() {
        try {
            //Creating the ArrayAdapter instance having the country list
            ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            rightSpinner.setAdapter(aa);
            rightSpinner.setSelection(1);

            rightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    Object item = parent.getItemAtPosition(pos);
                    if (whiteSpinnerTextView == true) {
                        TextView textViewLeft = (TextView) rightSpinner.getChildAt(0);
                        textViewLeft.setTextColor(getResources().getColor(R.color.white));
                    } else if (whiteSpinnerTextView == false) {
                        TextView textViewLeft = (TextView) rightSpinner.getChildAt(0);
                        textViewLeft.setTextColor(getResources().getColor(R.color.black));
                    }
//
                    outputCode = country_code[pos];
                    spinnnerRightName = country.toString();
                    spinnerRightPosition = pos;
                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (Exception e) {

        }
    }

    void getDataJson() {
        InputStream inputStream = getResources().openRawResource(R.raw.languages);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("Text Data", byteArrayOutputStream.toString());
        try {

            JSONArray jsonArray = new JSONArray(byteArrayOutputStream.toString());
            country = new String[jsonArray.length()];
            country_code = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                String name = jObj.getString("name");
                String code = jObj.getString("code");
                Log.d("***name", name);
                country[i] = name;
                spinnnerLeftName = name;
                country_code[i] = code;
            }
            try {

            } catch (Exception e) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isInternetOn() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }

    public void ToggleEngAmh(@NonNull KeyboardView mKeyboardView, int amhermicToggleOnicon,
                             int amhermicToggleOfficon) {

        //Get All keys codes and check in loop
        Keyboard currentKeyboard = mKeyboardView.getKeyboard();
        List<Keyboard.Key> keys = currentKeyboard.getKeys();


        if (isEngToAmheric) {
            mKeyboardView.invalidateKey(29);

            keys.get(29).icon = ResourcesCompat.getDrawable(getResources(), amhermicToggleOfficon, getTheme());
            isEngToAmheric = false;

        } else {
            mKeyboardView.invalidateKey(29);
            keys.get(29).icon = ResourcesCompat.getDrawable(getResources(), amhermicToggleOnicon, getTheme());
            isEngToAmheric = true;
        }
    }

    private void space_new(char code) {
        try {
            if (code == KeyCodes.SPACECODE) {
                if (mInputString != null && mInputString.length() > 0) {
                    if (isAmhericToEng) {
                        mInputString = mInputString.trim();
                        amherictoEng(mInputString, "am-ET", "en-GB", false);
                    } else if (isEngToAmheric) {
                        if (isInternetOn()) {
                            mInputString = mInputString.trim();
                            amherictoEng(mInputString, "en-GB", "am-ET", false);
                        } else {
                            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
                            if (Character.isLetter(code) && caps) {
                                code = Character.toUpperCase(code);
                            }
                            if (isTranslatebuttonPress) {
                                etText.setText(mComposing.append(" "));
                                etText.setSelection(mComposing.length());
                            } else {
                                ic.commitText(String.valueOf(code), 1);
                            }
                        }
                    } else {
                        if (Character.isLetter(code) && caps) {
                            code = Character.toUpperCase(code);
                        }
                        if (isTranslatebuttonPress) {
                            etText.setText(mComposing.append(" "));
                            etText.setSelection(mComposing.length());
                        } else {
                            ic.commitText(String.valueOf(code), 1);
                        }
                    }

                } else {
                    if (Character.isLetter(code) && caps) {
                        code = Character.toUpperCase(code);
                    }
                    if (isTranslatebuttonPress) {
                        etText.setText(mComposing.append(" "));
                        etText.setSelection(mComposing.length());
                    } else {
                        ic.commitText(String.valueOf(code), 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SPACEE@@", e.getMessage());
        }
    }

    private void amherictoEng(String getinputtext, String inputLangCode, String
            outputLangCod, Boolean voice) {

        ic = getCurrentInputConnection();
        inputStringbuilder.delete(0, inputStringbuilder.length());
        if (voice) {
            Translation translationforvoice = new Translation(this);
            translationforvoice.runTranslation(getinputtext, outputLangCod, inputLangCode);
            translationforvoice.setTranslationComplete((translation, language) ->
                    ic.commitText(translation, 1));
        } else {
            Translation translation = new Translation(this);
            translation.runTranslation(getinputtext, outputLangCod, inputLangCode);
            translation.setTranslationComplete((translation1, language) -> {
                if (translation1 == "0") {
                    Toast.makeText(this, "internet connection error", Toast.LENGTH_SHORT).show();
                } else {
                    int cursorPosition = ic.getTextBeforeCursor(1024, 0).length();
                    ic.deleteSurroundingText(mInputString.length(), 0);
                    int wordLength = translation1.length();  //4
                    ic = getCurrentInputConnection();

                    if (builderLength > poss) {
                        // int cursor = ic.getTextAfterCursor(1024, 0).length();
                        ic.deleteSurroundingText(cursorPosition, 0);
                        Log.i("Committed", "Before insertion " + translationStringbuilder.toString());
                        translationStringbuilder.insert(poss - (wordLength), translation1 + " ");
                        Log.i("Committed", "After Committed text " + translationStringbuilder.toString());
                        String text = translationStringbuilder.toString();
                        String full_text = ic.getExtractedText(new ExtractedTextRequest(), 0).text.toString();
                        //get whole text before the cursor]
                        CharSequence afterCursorText = ic.getTextAfterCursor(full_text.length(), 0);
                        ic.deleteSurroundingText(0, afterCursorText.length());
                        ic.commitText(text, 0);
                    } else {
                        ic.deleteSurroundingText(cursorPosition, 0);
                        translationStringbuilder.append(translation1 + " ");
                        ic.commitText(translationStringbuilder.toString(), 0);
                    }
                    translationStringbuilder.delete(0, translationStringbuilder.length());
                    et = ic.getExtractedText(new ExtractedTextRequest(), 0);
                    String outputText = et.text.toString();
                    translationStringbuilder.append(outputText);
                    poss = et.startOffset + et.selectionStart;
                    builderLength = translationStringbuilder.length();
                    // Toast.makeText(this, "AM-en: " + translation1, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}

