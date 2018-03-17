package no.ntnu.tomme87.imt3673.lab4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by Tomme on 17.03.2018.
 */

public class GetNicknameDialogFragment extends DialogFragment {
    public final static String TAG = "GetNicknameDialogFragment";

    public interface NoticeDialogListener {
        void onNewNickname(String nick);
        void onCancelNickname();
    }

    NoticeDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (NoticeDialogListener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setTitle(R.string.dialog_nickname_title)
                .setView(input)
                .setPositiveButton(R.string.dialog_nickname_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onNewNickname(input.getText().toString());
                    }
                })
                .setNegativeButton(R.string.dialog_nickname_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onCancelNickname();
                    }
                })
                .create();
    }
}
