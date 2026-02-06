package com.termux.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.termux.R;
import com.termux.shared.termux.TermuxConstants;
import com.termux.shared.termux.shell.command.environment.TermuxShellEnvironment;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;
import com.termux.view.TerminalView;
import com.termux.view.TerminalViewClient;

import java.io.File;
import java.util.HashMap;

public class BrainFragment extends Fragment {

    private TerminalView mTerminalView;
    private TerminalSession mTerminalSession;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brain, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTerminalView = view.findViewById(R.id.terminal_view);

        Context context = requireContext();

        // Setup environment
        TermuxShellEnvironment env = new TermuxShellEnvironment();
        HashMap<String, String> envMap = env.getEnvironment(context, false);
        String[] envArray = envMap.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .toArray(String[]::new);

        String executablePath = TermuxConstants.TERMUX_BIN_PREFIX_DIR_PATH + "/login";
        File loginFile = new File(executablePath);
        if (!loginFile.exists()) {
            executablePath = TermuxConstants.TERMUX_BIN_PREFIX_DIR_PATH + "/bash";
        }

        if (!new File(executablePath).exists()) {
             executablePath = "/system/bin/sh";
        }

        String cwd = TermuxConstants.TERMUX_HOME_DIR_PATH;
        String[] args = new String[]{};

        mTerminalSession = new TerminalSession(executablePath, cwd, args, envArray, 2000, new TerminalSessionClient() {
            @Override
            public void onTextChanged(@NonNull TerminalSession changedSession) {
                if (mTerminalView != null) mTerminalView.onScreenUpdated();
            }

            @Override
            public void onTitleChanged(@NonNull TerminalSession updatedSession) {
            }

            @Override
            public void onSessionFinished(@NonNull TerminalSession finishedSession) {
            }

            @Override
            public void onCopyTextToClipboard(@NonNull TerminalSession session, String text) {
            }

            @Override
            public void onPasteTextFromClipboard(@Nullable TerminalSession session) {
            }

            @Override
            public void onBell(@NonNull TerminalSession session) {
            }

            @Override
            public void onColorsChanged(@NonNull TerminalSession changedSession) {
            }

            @Override
            public void onTerminalCursorStateChange(boolean enabled) {
            }

            @Override
            public Integer getTerminalCursorStyle() {
                return null;
            }

            @Override
            public void setTerminalShellPid(@NonNull TerminalSession session, int pid) {
            }

            @Override public void logError(String tag, String message) {}
            @Override public void logWarn(String tag, String message) {}
            @Override public void logInfo(String tag, String message) {}
            @Override public void logDebug(String tag, String message) {}
            @Override public void logVerbose(String tag, String message) {}
            @Override public void logStackTraceWithMessage(String tag, String message, Exception e) {}
            @Override public void logStackTrace(String tag, Exception e) {}
        });

        mTerminalView.attachSession(mTerminalSession);
        mTerminalView.setTextSize(30);

        mTerminalView.setTerminalViewClient(new TerminalViewClient() {
            @Override
            public float onScale(float scale) {
                return 1.0f;
            }

            @Override
            public void onSingleTapUp(MotionEvent e) {
                mTerminalView.setFocusable(true);
                mTerminalView.setFocusableInTouchMode(true);
                mTerminalView.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mTerminalView, InputMethodManager.SHOW_IMPLICIT);
            }

            @Override
            public boolean shouldBackButtonBeMappedToEscape() {
                return false;
            }

            @Override
            public void copyModeChanged(boolean copyMode) {}

            @Override
            public boolean onKeyDown(int keyCode, android.view.KeyEvent e, TerminalSession session) {
                return false;
            }

            @Override
            public boolean onKeyUp(int keyCode, android.view.KeyEvent e) {
                return false;
            }

            @Override
            public boolean onLongPress(MotionEvent event) { return false; }

            @Override
            public boolean onCodePoint(int codePoint, boolean ctrlDown, TerminalSession session) { return false; }

            @Override
            public boolean readControlKey() { return false; }

            @Override
            public boolean readAltKey() { return false; }

            @Override
            public boolean readShiftKey() { return false; }

            @Override
            public boolean readFnKey() { return false; }

            @Override
            public void onEmulatorSet() {}

            @Override
            public boolean shouldEnforceCharBasedInput() {
                return false;
            }

            @Override
            public boolean shouldUseCtrlSpaceWorkaround() {
                return false;
            }

            @Override
            public boolean isTerminalViewSelected() {
                return true;
            }

            @Override public void logError(String tag, String message) {}
            @Override public void logWarn(String tag, String message) {}
            @Override public void logInfo(String tag, String message) {}
            @Override public void logDebug(String tag, String message) {}
            @Override public void logVerbose(String tag, String message) {}
            @Override public void logStackTraceWithMessage(String tag, String message, Exception e) {}
            @Override public void logStackTrace(String tag, Exception e) {}
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTerminalSession != null) {
            mTerminalSession.finishIfRunning();
        }
    }
}
